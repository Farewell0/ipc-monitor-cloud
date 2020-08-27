/**
 * @title: FileUtils
 * @package: com.nexhome.community.utils.system
 * @project: community
 * @description:
 * @author: hades
 * @company:
 * @date: 2019/9/12 16:06
 * @version V1.0
 */
package com.starnet.ipcmonitorcloud.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @className: FileUtils
 * @description:
 * @author: hades
 * @date: 2019/9/12 16:06
 * @mark:
 */
public class FileUtils {
    public static boolean exist(String path) {
        if (path == null) {
            return false;
        }
        File file = new File(path);
        return file.exists();
    }

    public static File mkdirs(String destPath) {
        File file = new File(destPath);
        //当文件夹不存在时，mkdirs会自动创建多层目录，区别于mkdir．(mkdir如果父目录不存在则会抛出异常)
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
        return file;
    }

    public static InputStream getFileInputStream(String fileName) throws FileNotFoundException {
        if (fileName == null)
            return null;

        File file = null;
        if (fileName.startsWith("file:")) {
            file = new File(fileName.substring("file:".length()));
        }
        else if (fileName.startsWith("classpath:")) {
            String tmp = fileName.substring("classpath:".length());
            ClassLoader classLoader =  Thread.currentThread().getContextClassLoader();
            return classLoader.getResourceAsStream(tmp);
        }
        else {
            file = new File(fileName);
        }
        if (file.exists()) {
            return new FileInputStream(file);
        }
        return null;
    }

    public static boolean writeFile(String path, String content) throws Exception {
        writeFile(path, content.getBytes("utf-8"));
        return true;
    }

    public static boolean deleteFile(String path) {
        File file = new File(path);
        if (file.isFile() && file.exists()) {
            file.delete();
        }
        return true;
    }

    public static void deleteDir(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            return;
        }
        deleteDir(file);
    }

    private static void deleteDir(File file) {
        if (file.isFile()) {
            //判断是否为文件，是，则删除
            file.delete();
        } else {//不为文件，则为文件夹
            String[] childFilePath = file.list();//获取文件夹下所有文件相对路径
            for (String path:childFilePath) {
                File childFile= new File(file.getAbsoluteFile()+"/"+path);
                deleteDir(childFile);//递归，对每个都进行判断
            }
            file.delete();
        }
    }

    public static void writeFile(String path, byte[] data) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(path);
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fos)) {
            bufferedOutputStream.write(data);
            bufferedOutputStream.flush();
            fos.flush();
        }
    }

    public static boolean removeFile(String src, String dst) {
        File srcFile = new File(src);
        if (!srcFile.exists()) {
            return false;
        }
        File dstFile = new File(dst);
        if (dstFile.exists()) {
            dstFile.delete();
        }
        return srcFile.renameTo(dstFile);
    }

    public static void setCanReadAndWrite(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        file.setReadable(true, false);
        file.setExecutable(true, false);
        file.setWritable(true, false);
    }

    public static void copyFile(String srcPath, String dstPath) throws Exception {
        try (InputStream input =  new FileInputStream(srcPath);
                OutputStream output = new FileOutputStream(dstPath)) {
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
        }
    }

    public static byte[] readFile(String path) throws Exception {
        try (InputStream inputStream = new FileInputStream(path)) {
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            return data;
        }
    }

    public static List<File> getSubdirectories(String destPath) {
        List<File> subdirectories = new ArrayList<>();
        File file = new File(destPath);
        if (!file.exists()) {
            return subdirectories;
        }
        File[] subFiles = file.listFiles();
        if (subFiles == null || subFiles.length <= 0) {
            return subdirectories;
        }
        for (File subFile : subFiles) {
            if(subFile.isDirectory()){
                subdirectories.add(subFile);
            }
        }

        return subdirectories;
    }
}
