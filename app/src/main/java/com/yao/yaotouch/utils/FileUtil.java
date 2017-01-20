package com.yao.yaotouch.utils;

import com.yao.yaotouch.YaoTouchApp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Yao on 2016/9/21 0021.
 */
public class FileUtil {

    public static void saveFile(String name, String text) throws IOException {
//        File file = new File(YaoTouchApp.getApplication().getFilesDir(), "/" + name);
        File file = new File(YaoTouchApp.getApplication().getExternalFilesDir(null), "/" + name);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            //outputFile.createNewFile();
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(text.getBytes());
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    public static String readFile(String name) throws IOException {
        File file = new File(YaoTouchApp.getApplication().getExternalFilesDir(null), "/" + name);
        if (!file.exists()) {
            return null;
        }
        return readInStream(new FileInputStream(file));
    }

    public static String readInStream(FileInputStream inStream) throws IOException {
        //需要用到该类的write方法往内存中写入数据
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = -1;
             /*
            * fint.read()第二次读取到的数据会把第一次读取到的数据冲掉。
            */
        while ((length = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, length); //每次读取到的数据往内存写入
        }
        outStream.close();
        inStream.close();
            /*
            //得到文件的二进制数据
            * byte[] data = outStream.toByteArray();
            *outStream.close();
            *fint.close();
            *return new String(data);
             */
        return outStream.toString();
    }
}
