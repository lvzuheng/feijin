package com.data.feijin.utils;

import java.io.*;

public class FileStreamReader {

    private static FileStreamReader fileStreamReader;

    public static FileStreamReader getFileReader(){
        if(fileStreamReader == null){
            fileStreamReader = new FileStreamReader();
        }
        return fileStreamReader;
    }


    public String read(String uri){
        File file = new File(uri);
        if(!file.exists() || !file.isFile()){
            return "";
        }
        try ( BufferedReader bufferedReader  = new BufferedReader(new FileReader(file))){
            StringBuffer stringBuffer = new StringBuffer();
            String s  = new String();
            while((s = bufferedReader.readLine()) != null){
                stringBuffer.append(s);
            }
            return stringBuffer.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
        }
        return "";
    }

}
