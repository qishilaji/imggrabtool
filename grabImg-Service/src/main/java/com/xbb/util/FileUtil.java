package com.xbb.util;

import java.io.File;
import java.io.IOException;

/**
 * @Auther: xbb
 * @Date: 2018-9-26 17:16
 * @Description:
 */
public class FileUtil {


        // 判断文件是否存在
        public static boolean createFile(File file) {
            if (!file.exists()) {
                try {
                    file.createNewFile();
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

        //创建文件夹
        public static boolean createDir(File file) {
            if (!file.exists()) {
                file.mkdir();
            }
            return true;
        }

}
