# encoding=utf-8

import json
import time

from fabric.api import env, local, hosts
from fabric.context_managers import cd, settings
from fabric.contrib.files import exists
from fabric.operations import sudo, run, put
from fabric.tasks import execute
from fabric.utils import abort

env.host_one = ['123.207.32.167']
env.host_remaining = ['123.207.32.167']
env.host_tmp = []
env.host_all = env.host_one

env.host_test = ['123.207.32.167']

env.build_command = 'distTar'
env.build_dir = './build/distributions'
env.release_dir = '/home/ubuntu/develop/release'

env.app_name = 'feijin'
env.app_port = '9030'  # 慎重:修改端口,同时需要修改slb:a8_service_bet的端口映射
env.app_status_cmd = 'curl http://localhost:9030/%s/status' % env.app_name

env.app_home = '/srv/java/%s' % env.app_name
env.app_on_file = env.app_home + '/on'
env.app_out = '/var/log/%s/%s.out.log' % (env.app_name, env.app_name)
env.app_start_cmd = 'bin/%s %s' % (env.app_name, env.app_port)
env.app_kill_cmd = "kill -9 $(ps -ef|grep java|grep %s|grep -v grep|awk '{print $2}')" % env.app_name
env.app_ps_cmd = "ps -ef|grep java|grep %s|grep -v grep|awk '{print $2}'" % env.app_name

env.hosts = ['123.207.32.167']            # 指定 hosts 远程主机
env.key_filename = '/root/.ssh/id_rsa'     # 指定你的私钥文件
env.user = 'ubuntu'

env.stop_wait_time = 10
env.status_check_interval = 5
env.status_check_times = 60


# 中间变量
env.slb_ids = []

def rnotify(msg=''):
    tm = time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())
    rmsg = tm + ':' + env.app_name + '===>' + msg
    data = {'text': rmsg}
    local('curl -X POST --data-urlencode \'payload=' + json.dumps(data) + '\' ' + env.notify_api)


def test():
    execute(pull_test)
    execute(build_test)
    execute(release_test)
    execute(deploy_test)


def pull_test():
    local('git checkout master')
    local('git reset --hard remotes/origin/master')
    local('git pull origin master')



@hosts(env.host_test)
def release_test():
    run('mkdir -p ' + env.release_dir)
    put(env.build_dir + '/*', env.release_dir)


def build_test():
    local('gradle -Penv=test clean ' + env.build_command)
    env.build_file = local('ls ' + env.build_dir, capture=True)


def deploy_test(host=''):
    if not host:
        host = env.host_test[0]
    env.hosts = [host]
    execute(offline)
    execute(restart)
    execute(check_status)
    execute(online)


def first():
    rnotify('master:begin first ...')
    execute(pull)
    execute(build)
    execute(release)
    execute(deploy_one)
    rnotify('master:end first ...')


def left():
    rnotify('master:begin left ...')
    execute(deploy_remaining)
    rnotify('master:end left ...')


def tmp():
    local('gradle -Penv=prod clean ' + env.build_command)
    rnotify('master:begin left ...')
    execute(release_tmp)
    execute(deploy_tmp)
    rnotify('master:end left ...')


def user():
    execute(deploy_user)


def all():
    execute(first)
    execute(left)


def pull():
    local('git checkout master')
    local('git reset --hard remotes/origin/master')
    local('git pull origin master')


def build():
    local('gradle -Penv=prod clean ' + env.build_command)
    env.build_file = local('ls ' + env.build_dir, capture=True)


def release():
    run('mkdir -p ' + env.release_dir)
    put(env.build_dir + '/*', env.release_dir)


@hosts(env.host_tmp)
def release_tmp():
    run('mkdir -p ' + env.release_dir)
    put(env.build_dir + '/*', env.release_dir)


def deploy_one(host=''):
    if not host:
        host = env.host_one[0]
    env.hosts = [host]
    execute(offline, host)
    execute(restart)
    execute(check_status)
    execute(online, host)


def deploy_remaining():
    # for host in env.host_remaining:
    #    execute(deploy_one, host)
    execute(deploy_service)
    execute(deploy_user)


def deploy_tmp():
    for host in env.host_tmp:
        execute(deploy_one, host)


def deploy_user():
    env.slb_ids = env.slb_user_ids
    for host in env.host_user:
        execute(deploy_one, host)


def deploy_service():
    env.slb_ids = env.slb_service_ids
    for host in env.host_service:
        execute(deploy_one, host)


def deploy_all():
    for host in env.host_all:
        execute(deploy_one, host)


def rollback_one(zipfilename='', host=''):
    if not host:
        host = env.host_one[0]
    env.hosts = [host]
    execute(restart, zipfilename=zipfilename, rollback=True)
    execute(check_status)
    execute(online)


def rollback_remaining(zipfilename=''):
    for host in env.host_remaining:
        execute(rollback_one, zipfilename, host)


def rollback_all(zipfilename=''):
    for host in env.host_all:
        execute(rollback_one, zipfilename, host)


def check_status():
    ok = ''
    times = 1
    with settings(warn_only=True):
        while times < env.status_check_times:
            ok = run(env.app_status_cmd)
            if ok == 'ok':
                return True
            times = times + 1
            print('status return: {}. wait for {} seconds to check again '.format(ok, env.status_check_interval))
            time.sleep(env.status_check_interval)
    abort('check status failed after %s retires!' % str(times))


def offline(innerIps=''):
    if exists(env.app_on_file):
        run('rm -f %s' % env.app_on_file)
    if innerIps != '':
        slb_remove(innerIps)
    print('wait for %s seconds before stop' % str(env.stop_wait_time))
    time.sleep(env.stop_wait_time)


def online(innerIps=''):
    if innerIps != '':
        slb_add(innerIps)
    run('touch ' + env.app_on_file)


def restart(zipfilename='', rollback=False):
    execute(stop)
    if not zipfilename:
        if rollback:
            zipfilename = run('ls -t %s |head -2|tail -1' % env.release_dir)
        else:
            zipfilename = run('ls -t %s |head -1' % env.release_dir)
    if not zipfilename:
        abort('no tar found! can not restart! rollback: %s' % str(rollback))
    execute(start, zipfilename)


def stop():
    pid = run(env.app_ps_cmd)
    if pid:
        pids = pid.splitlines(False)
        if pids:
            print("pids: %s" % pids)
            for p in pids:
                run('kill -9 %s' % p)


def start(zipfilename):
    with cd(env.app_home):
        zipfile = env.release_dir + '/' + zipfilename
        run('cp -f ' + zipfile + ' ./')
        filename = '.'.join(zipfilename.split('.')[:-1]) if '.' in zipfilename else zipfilename
        run('rm -rf ' + filename)
        run('tar xf ' + zipfilename)

        # create current file
        run('rm current')
        run('ln -s {} current'.format(filename))
        cmd = './' + filename + '/' + env.app_start_cmd
        run("sh -c '((nohup %s > %s 2>&1) & )'" % (cmd, env.app_out), pty=False)

        pid = run(env.app_ps_cmd)
        run('echo {} > pid'.format(pid))


def slb_add(innerIps=''):
    slb_action('slbAdd.php', innerIps)


def slb_remove(innerIps=''):
    slb_action('slbRemove.php', innerIps)


def slb_action(action='', innerIps=''):
    if not env.slb_ids:
        return
    for slb_id in env.slb_ids:
        run("curl '%s/%s?slbId=%s&hostIp=%s'" % (env.slb_host, action, slb_id, innerIps))


@hosts(env.host_all)
def pre_env():
    java_home = run('echo $JAVA_HOME')
    sudo('mkdir -p %s/jre/lib/fonts/fallback' % java_home)
    dis = '%s/jre/lib/fonts/fallback' % java_home
    put('./font/*', dis, use_sudo=True)
    with cd('$JAVA_HOME/jre/lib/fonts/fallback'):
        sudo('chown -R --reference=%s %s/jre/lib/fonts/fallback' % (java_home, java_home))
        sudo('chmod 644 %s/jre/lib/fonts/fallback/*' % java_home)


@hosts(env.host_test)
def pre_env_test():
    java_home = run('echo $JAVA_HOME')
    sudo('mkdir -p %s/jre/lib/fonts/fallback' % java_home)
    dis = '%s/jre/lib/fonts/fallback' % java_home
    put('./font/*', dis, use_sudo=True)
    with cd('$JAVA_HOME/jre/lib/fonts/fallback'):
        sudo('chown -R --reference=%s %s/jre/lib/fonts/fallback' % (java_home, java_home))
        sudo('chmod 644 %s/jre/lib/fonts/fallback/*' % java_home)
