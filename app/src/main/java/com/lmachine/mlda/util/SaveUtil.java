package com.lmachine.mlda.util;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by SailFlorve on 2017/9/1 0001.
 * 保存到文件工具类
 */

public class SaveUtil {
    public static boolean saveString(String str, String fineName) {
        try {
            File file = new File(Environment.getExternalStorageDirectory(), "MLDA/" + fineName);
            if (!file.exists()) {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }
            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(str.getBytes());
            outStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
