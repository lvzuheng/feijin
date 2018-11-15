# encoding=utf-8

import json
import time

from fabric.api import env, local, hosts
from fabric.context_managers import cd, settings
from fabric.contrib.files import exists
from fabric.operations import sudo, run, put
from fabric.tasks import execute
from fabric.utils import abort

env.server_name = 'feijin'
env.server_port = '9030'

env.user = "ubuntu"
env.host_dev = ['123.207.32.167']

env.server_home= '/srv/java/%s'%env.server_name
env.server_log = '/var/log/%s'%env.server_name
env.app_out = '%s/%s.out.log'%(env.server_log,env.server_name)
#服务启动路径
env.server_start = 'bin/%s' % env.server_name


#服务进程号查询
env.server_ps_cmd = "ps -ef|grep java|grep %s|grep -v grep|awk '{print $2}'" % env.server_name



@hosts(env.host_dev) #指定远程服务器地址,用户名为env.user的值
def dev():
    pull()

#从github拉取项目
def pull():
    local('git checkout dev')
    local('git reset --hard remotes/origin/dev')
    local('git pull origin dev')

#本地通过gradle将项目打包成二进制tar包
def build():
    local('gradle clean distTar -Penv=dev')#通过gradle打包成二进制的tar包,并指定开发为开发环境的包

#将二进制包复制到远程部署服务器上
@hosts(env.host_dev)
def release():
    run('mkdir -p ' + env.release_dir)
    put(env.build_dir+'/*',env.release_dir)

def start():
    if not exists(env.server_home):
        sudo('mkdir -p %s' % env.server_home)
        sudo('chown -R %s %s' %(env.user,env.server_home))
    with cd(env.server_home):
        #修改时间顺序排列,查找服务名列表,取出最后一个修改的文件
        tarfilename = run('ls -t %s |grep %s | head -1'%(env.release_dir,env.app_name))
        print tarfilename
        if not tarfilename:
            abort('not found file')
        #将文件拷贝到部署目录
        run('cp %s/%s ./'%(env.release_dir,tarfilename))
        #获取无扩展名的文件名
        filename = '.'.join(tarfilename.split('.')[:-1]) if '.' in tarfilename else tarfilename
        print filename
        if tarfilename:
            run('rm -rf ' + filename)
            run('tar xf ' + tarfilename)#讲二进制文件解压缩
        cmd = './' +filename + '/' + env.server_start_cmd#服务的运行脚本目录
        if not exists(env.log_path):
            sudo('mkdir -p %s'%env.log_path)#创建日志文件
            sudo('chown -R %s %s'%(env.user,env.log_path))
        #sh运行服务,指定服务消息输出到日志,服务后台运行
        run("sh -c '((nohup %s > %s 2>&1) & )'" % (cmd, env.app_out), pty=False)

def stop():
    #获取服务的进程号
    pid = run(env.server_ps_cmd)
    print pid
    if pid:
        run('kill -9 %s' % pid)#如果进程正在运行就杀死进程

def check():
    ok = ''
    times = 1
    with settings(warn_only = True):
        while times < env.status_check_times:
            ok = run(env.app_status_cmd)
            if ok == 'ok':
                return True
            times = times + 1
            print('status != ok. wait for %s seconds to check again ' % str(env.status_check_interval))
            time.sleep(env.status_check_interval)
    abort('check status failed after %s retires!' % str(times))
