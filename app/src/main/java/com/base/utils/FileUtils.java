package com.base.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.util.Base64;

import com.base.host.HostApplication;
import com.base.widget.toast.ToastHelper;

/**
 * 文件操作工具包
 * 
 * @author fengbb (http://www.6clue.com/)
 * @version 1.0
 * @created 2014-3-5 16:26:20
 */
public class FileUtils {

    private static final String TAG = "FileUtils";

    /**
     * 缓存基本目录
     */
    public static final String BASE_PATH = "heihei";
    public static final String AUDIO_CATCH = "audio";
    public static final String CHATIMAGE_CATCH = "chatImage";

    public static class SharePrefrenceUtil {

        private static SharedPreferences mSpInstance;

        private static SharedPreferences getSharePreferences(Context context) {
            // return new DemaiSession(context, Constants.BUSINESS_PREFERENCES);
            if (mSpInstance == null) {
                mSpInstance = context.getSharedPreferences(BASE_PATH, Context.MODE_MULTI_PROCESS);
            }
            return mSpInstance;
        }

        @SuppressLint("NewApi")
        public static void put(Context context, String name, Object key) {
            getSharePreferences(context);
            if (key instanceof String) {
                mSpInstance.edit().putString(name, (String) key).commit();
            } else if (key instanceof Boolean) {
                mSpInstance.edit().putBoolean(name, (Boolean) key).commit();
            } else if (key instanceof Long) {
                mSpInstance.edit().putLong(name, (Long) key).commit();
            } else if (key instanceof Set<?>) {
                mSpInstance.edit().putStringSet(name, (Set<String>) key).commit();
            } else if (key instanceof Integer) {
                mSpInstance.edit().putInt(name, (Integer) key).commit();

            }
        }

        @SuppressLint("NewApi")
        public static Object get(Context context, String name, Object tag) {
            getSharePreferences(context);
            Object object = null;
            if (tag instanceof String) {
                object = mSpInstance.getString(name, null);
            } else if (tag instanceof Boolean) {
                object = mSpInstance.getBoolean(name, false);
            } else if (tag instanceof Long) {
                object = mSpInstance.getLong(name, 0);
            } else if (tag instanceof Integer) {
                object = mSpInstance.getInt(name, (Integer) tag);

            } else if (tag instanceof Float) {
                object = mSpInstance.getFloat(name, (Float) tag);

            } else if (tag instanceof HashSet<?>) {
                object = mSpInstance.getStringSet(name, (Set<String>) tag);
            }
            return object;
        }

        public static List<Entry<String, Object>> getAll(Context context) {
            getSharePreferences(context);
            List<Entry<String, Object>> list = new ArrayList<Map.Entry<String, Object>>();
            Map<String, ?> map = mSpInstance.getAll();
            Set set = map.entrySet();
            Iterator iterator = set.iterator();

            while (iterator.hasNext()) {
                Map.Entry entry = (Entry) iterator.next();
                list.add(entry);
            }
            return list;
        }

        public static void deleteData(Context context, String content) {
            getSharePreferences(context);
            mSpInstance.edit().remove(content).commit();
        }
    }

    /**
     * 写文本文件 在Android系统中，文件保存在 /data/data/PACKAGE_NAME/files 目录下
     * 
     * @param context
     * @param msg
     */
    public static void write(Context context, String fileName, String content) {
        if (content == null)
            content = "";
        try {
            FileOutputStream out = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            OutputStreamWriter fos = new OutputStreamWriter(out, "UTF-8");
            fos.write(content);
            // fos.write(content.getBytes());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @TODO: 写文件到sdcard
     * @param path
     * @param fileName
     * @param inputStream
     * @throws Exception
     */
    public static void write(String fileName, InputStream inputStream) throws Exception {
        File file = new File(fileName);
        FileOutputStream outStream = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inputStream.close();
    }

    /**
     * 把字符串写到sd卡
     * 
     * @param relativeFileName
     *            文件的相对名字（不包括根目录）
     * @param content
     *            字符串
     * @return 1表示成功，0表示失败，-1表示没有sd卡
     */
    public static int write2SDcard(String relativeFileName, String content) {
        int result = 0;
        if (!checkSaveLocationExists()) {// sd卡未安装
            result = -1;
        }
        if (relativeFileName.contains("/")) {
            String pa[] = relativeFileName.split("/");
            String path = "";
            for (int i = 0; i < pa.length - 1; i++) {
                if (pa[i] != null && !pa[i].equals("")) {
                    if (i > 0) {
                        path = path + "/" + pa[i];
                    } else {
                        path += pa[i];
                    }
                    File file = new File(Environment.getExternalStorageDirectory(), path);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                }
            }
        }
        FileWriter writer = null;
        File file = new File(getSDpath() + "/" + relativeFileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            // writer = new FileWriter(file, true);
            writer = new FileWriter(file);
            writer.write(content);
            // writer.write("\r\n");
            writer.flush();
            result = 1;
        } catch (FileNotFoundException e) {
        	LogWriter.w("ImageFileCache", "FileNotFoundException");
        } catch (IOException e) {
            e.printStackTrace();
            result = 0;
        } finally {
            if (writer != null) {
                try {

                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        // System.out.println(result + " result write sk");
        return result;
    }

    /**
     * 把字符串写到sd卡
     * 
     * @param relativeFileName
     *            文件的相对名字（不包括根目录）
     * @param content
     *            字符串
     * @return 1表示成功，0表示失败，-1表示没有sd卡
     */
    public static int write2SDcard(String relativeFileName, String content, boolean isAppend) {
        int result = 0;
        if (!checkSaveLocationExists()) {// sd卡未安装
            result = -1;
        }
        if (relativeFileName.contains("/")) {
            String pa[] = relativeFileName.split("/");
            String path = "";
            for (int i = 0; i < pa.length - 1; i++) {
                if (pa[i] != null && !pa[i].equals("")) {
                    if (i > 0) {
                        path = path + "/" + pa[i];
                    } else {
                        path += pa[i];
                    }
                    File file = new File(Environment.getExternalStorageDirectory(), path);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                }
            }
        }
        FileWriter writer = null;
        File file = new File(getSDpath() + "/" + relativeFileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            if (isAppend) {

                writer = new FileWriter(file, true);
            } else {
                writer = new FileWriter(file);
            }
            writer.write(content);
            writer.write("\r\n");
            writer.flush();
            result = 1;
        } catch (FileNotFoundException e) {
        	LogWriter.w("ImageFileCache", "FileNotFoundException");
        } catch (IOException e) {
            e.printStackTrace();
            result = 0;
        } finally {
            if (writer != null) {
                try {

                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        // System.out.println(result + " result write sk");
        return result;
    }

    /**
     * 把字符串写到sd卡
     * 
     * @param relativeFileName
     *            文件的绝对路径
     * @param content
     *            字符串
     * @return 1表示成功，0表示失败，-1表示没有sd卡
     */
    public static int write2SDcardAbsolute(String fileName, String content, boolean isAppend) {
        int result = 0;
        if (!checkSaveLocationExists()) {// sd卡未安装
            result = -1;
        }
        if (fileName.contains("/")) {
            String pa[] = fileName.split("/");
            String path = "";
            for (int i = 0; i < pa.length - 1; i++) {
                if (pa[i] != null && !pa[i].equals("")) {
                    if (i > 0) {
                        path = path + "/" + pa[i];
                    } else {
                        path += pa[i];
                    }
                    File file = new File(path);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                }
            }
        }
        FileWriter writer = null;
        File file = new File(fileName);
        try {
            if (file.isDirectory()) {
                return 0;
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            if (isAppend) {
                writer = new FileWriter(file, true);
            } else {
                writer = new FileWriter(file);
            }
            writer.write(content);
            writer.write("\r\n");
            writer.flush();
            result = 1;
        } catch (FileNotFoundException e) {
        	LogWriter.w("ImageFileCache", "FileNotFoundException");
        } catch (IOException e) {
            e.printStackTrace();
            result = 0;
        } finally {
            if (writer != null) {
                try {

                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        // System.out.println(result + " result write sk");
        return result;
    }

    public static int getAvailMemory(Context context) {// 获取android当前可用内存大小

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo mi = new MemoryInfo();
        am.getMemoryInfo(mi);// mi.availMem; 当前系统的可用内存
        return (int) (mi.availMem / 1024 / 1024);// 将获取的内存大小规格化
    }

    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {

        }
    }

    public static String read(Context context, String fileName, boolean tag) {
        if (tag) {
            FileInputStream f;
            StringBuilder sb = new StringBuilder("");
            // makeRootDirectory(fileName);
            try {
                f = context.openFileInput(fileName);
                InputStreamReader iStream = new InputStreamReader(f, "UTF-8");
                BufferedReader reader = new BufferedReader(iStream);
                byte[] buf = new byte[1024];
                int hasRead = 0;
                String line;
                // while ((hasRead = f.read(buf)) > 0) {
                while ((line = reader.readLine()) != null) {
                    // sb.append(new String(buf, 0, hasRead));
                    sb.append(line);
                }
                f.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return sb.toString();
        } else {
            return read(context, fileName);
        }
    }

    /**
     * 读取文本文件
     * 
     * @param context
     * @param fileName
     * @return
     */
    public static String read(Context context, String fileName) {
        try {
            if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
                File file = new File(Environment.getExternalStorageDirectory(), fileName);
                if (!file.exists()) {
                    return "";
                }

                byte[] array = new byte[1024];
                int len = -1;
                // ByteArrayOutputStream bos = new ByteArrayOutputStream();
                // FileInputStream fis = new FileInputStream(file);
                InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
                BufferedReader reader = new BufferedReader(isr);
                String line;
                String dateString = "";
                while ((line = reader.readLine()) != null) {
                    dateString += line;
                }
                /*
                 * while ((len = fis.read(array)) != -1) { bos.write(array, 0, len); }
                 */
                isr.close();
                reader.close();
                // bos.close();
                // fis.close();
                return dateString.toString();

            } else {
                FileInputStream in = context.openFileInput(fileName);
                return readInStream(in);
            }
        } catch (Exception e) {
        	LogWriter.d(TAG, e.getMessage());
            return "";
        }
    }

    /**
     * 读取文本文件
     * 
     * @param context
     * @param fileName
     * @return
     */
    public static String readSDCard(Context context, String fileName) {
        try {
            if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
                File file = new File(fileName);
                if (!file.exists()) {
                    return null;
                }
                InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
                BufferedReader reader = new BufferedReader(isr);
                String line;
                String dateString = "";
                while ((line = reader.readLine()) != null) {
                    dateString += line;
                }
                /*
                 * while ((len = fis.read(array)) != -1) { bos.write(array, 0, len); }
                 */
                isr.close();
                reader.close();
                return dateString;
            }
        } catch (Exception e) {
        	LogWriter.d(TAG, e.getMessage());
            return null;
        }
        return null;
    }

    public static String readInStream(InputStream inStream) {
        try {
            String dateString = "", line;
            InputStreamReader isr = new InputStreamReader(inStream, "UTF-8");
            BufferedReader reader = new BufferedReader(isr);
            while ((line = reader.readLine()) != null) {
                dateString += line;
            }
            isr.close();
            reader.close();
            inStream.close();
            return dateString;
        } catch (IOException e) {
        	LogWriter.i("FileTest", e.getMessage());
        }
        return null;
    }

    /**
     * 创建一个文件，创建成功返回true
     * 
     * @param filePath
     * @return
     */
    public static boolean createFile(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }

                return file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static File createFile(String folderPath, String fileName) {
        File destDir = new File(folderPath);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        return new File(folderPath, fileName);
    }

    // tag=true写手机内存；tag=false写sdcard;
    public static boolean writeFile(Context context, byte[] buffer, String folder, String fileName, boolean tag) {
        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if (tag && getAvailMemory(context) > 2) {
            write(context, fileName, new String(buffer));
        } else if (sdCardExist) {
            writeFile(context, buffer, folder, fileName);
        }
        return true;
    }

    /**
     * 向手机写图片
     * 
     * @param buffer
     * @param folder
     * @param fileName
     * @return
     */
    public static boolean writeFile(Context context, byte[] buffer, String folder, String fileName) {
        // clearUrlData(context, false);
        boolean writeSucc = false;
        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        String folderPath = "";
        if (sdCardExist) {
            folderPath = Environment.getExternalStorageDirectory() + File.separator + folder + File.separator;
            File fileDir = new File(folderPath);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }

            LogWriter.d(TAG, "folderPath + fileName:" + folderPath + fileName);
            File file = new File(folderPath + fileName);

            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                writer.write(new String(buffer));
                // out.write(buffer);
                writeSucc = true;
                writer.close();
            } catch (Exception e) {
            	LogWriter.d(TAG, "write:" + e.getMessage());
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            write(context, fileName, buffer.toString());
        }

        return writeSucc;
    }

    public static boolean WriteBitMap(String folder, String fileName, Bitmap bitmap) {
        boolean writeSucc = false;
        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);

        // String folderPath = "";
        if (sdCardExist) {
            // folderPath = Environment.getExternalStorageDirectory()
            // + File.separator + folder;
            File fileDir = new File(folder);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }

            File file = new File(folder + fileName);
            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
                writeSucc = true;
            } catch (Exception e) {
            	LogWriter.d(TAG, "write:" + e.getMessage());
            }
        }

        return writeSucc;
    }

    public static boolean saveImgToGallery(String filePath, Context context) {
        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (!sdCardExist)
            return false;
        try {
            ContentValues values = new ContentValues();
            values.put("datetaken", new Date().toString());
            values.put("mime_type", "image/jpg");
            values.put("_data", filePath);
            ContentResolver cr = context.getContentResolver();
            cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private static final int MB = 1024 * 1024;
    private static int CACHE_SIZE = 90;
    private static final int FREE_SD_SPACE_NEEDED_TO_CACHE = 10;

    // // 删除接口缓存数据
    // public static void clearUrlData(Context context, boolean delete) {
    // File dir0 = new File(GFile.GENFILENAME + "/URLs");
    // File[] files0 = dir0.listFiles();
    // if (!android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
    // return;
    // }
    // if (files0 == null) {
    // return;
    // }
    // for (int i = 0; i < files0.length; i++) {
    // if (files0[i].getName().equals(StringUtils.getUid(context))) {
    // File[] files = files0[i].listFiles();
    // if (files == null) {
    // return;
    // }
    // int dirSize = 0;
    // for (int j = 0; j < files.length; j++) {
    // if (files[j].getName().endsWith(WHOLESALE_CONV.DATA_CONV)) {
    // dirSize += files[j].length();
    // }
    // }
    // if ((dirSize > CACHE_SIZE * MB || FREE_SD_SPACE_NEEDED_TO_CACHE > sdFreeSpace()) && files.length > 0
    // || delete) {
    // int remove = (int) ((0.8 * files.length) + 1);
    // Arrays.sort(files, new FileLastModifySort());
    // for (int j = 0; j < remove; j++) {
    // String nameString = files[j].getName();
    // if (nameString.contains(WHOLESALE_CONV.DATA_CONV)
    // && nameString.startsWith(GFile.SUPERCARD_START)
    // && !nameString.equals(GFile.SUPERCARD_START + WHOLESALE_CONV.DATA_CONV)) {
    // files[j].delete();
    // }
    // }
    // }
    // } else {
    // files0[i].delete();
    // }
    // }
    // // File dir = new File(GFile.GENFILENAME + "/URLs" + "/" + StringUtils.getUid(context));
    //
    // }

    public static class FileLastModifySort implements Comparator<File> {

        public int compare(File a, File b) {
            if (a.lastModified() > b.lastModified()) {
                return 1;
            } else if (a.lastModified() == b.lastModified()) {
                return 0;
            } else {
                return -1;
            }
        }
    }

    public static int sdFreeSpace() {
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        double sdFreeMB = 0;
        int sysVersion = Integer.parseInt(VERSION.SDK);
        // if (sysVersion >= 8 && sysVersion < 19) {
        sdFreeMB = ((double) statFs.getAvailableBlocks() * (double) statFs.getBlockSize()) / MB;
        // }
        return (int) sdFreeMB;
    }

    public static boolean writeFile(String buffer, String folder, String fileName) {
        boolean writeSucc = false;
        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);

        String folderPath = "";
        if (sdCardExist) {
            folderPath = Environment.getExternalStorageDirectory() + File.separator + folder;
            File fileDir = new File(folderPath);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }

            File file = new File(folderPath + fileName);

            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                writer.write(new String(buffer));
                // out.write(buffer);
                writeSucc = true;
                writer.close();
            } catch (Exception e) {
            	LogWriter.d(TAG, "write:" + e.getMessage());
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return writeSucc;
    }

    /**
     * 根据文件绝对路径获取文件名
     * 
     * @param filePath
     * @return
     */
    public static String getFileName(String filePath) {
        if (StringUtils.isEmpty(filePath))
            return "";
        return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
    }

    /**
     * 根据文件的绝对路径获取文件名但不包含扩展名
     * 
     * @param filePath
     * @return
     */
    public static String getFileNameNoFormat(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return "";
        }
        int point = filePath.lastIndexOf('.');
        return filePath.substring(filePath.lastIndexOf(File.separator) + 1, point);
    }

    /**
     * 获取文件扩展名
     * 
     * @param fileName
     * @return
     */
    public static String getFileFormat(String fileName) {
        if (StringUtils.isEmpty(fileName))
            return "";

        int point = fileName.lastIndexOf('.');
        return fileName.substring(point + 1);
    }

    /**
     * Java文件操作 获取不带扩展名的文件名
     */
    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    /***
     * 获取文件扩展名
     * 
     * @param filename
     * @return 返回文件扩展名
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    /**
     * 获取文件大小
     * 
     * @param filePath
     * @return
     */
    public static long getFileSize(String filePath) {
        long size = 0;

        File file = new File(filePath);
        if (file != null && file.exists()) {
            size = file.length();
        }
        return size;
    }

    /**
     * 获取文件大小
     * 
     * @param size
     *            字节
     * @return
     */
    public static String getFileSize(long size) {
        if (size <= 0)
            return "0";
        java.text.DecimalFormat df = new java.text.DecimalFormat("##.##");
        float temp = (float) size / 1024;
        if (temp >= 1024) {
            return df.format(temp / 1024) + "M";
        } else {
            return df.format(temp) + "K";
        }
    }

    /**
     * 转换文件大小
     * 
     * @param fileS
     * @return B/KB/MB/GB
     */
    public static String formatFileSize(long fileS) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS == 0l) {
            fileSizeString = "0MB";
        } else if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 获取目录大小
     * 
     * @param dir
     * @return
     */
    public static long getDirSize(File dir) {
        if (dir == null) {
            return 0;
        }
        if (!dir.isDirectory()) {
            return 0;
        }
        long dirSize = 0;
        File[] files = dir.listFiles();

        if (files == null) {
            return 0;
        }

        for (File file : files) {
            if (file.isFile()) {
                dirSize += file.length();
            } else if (file.isDirectory()) {
                dirSize += file.length();
                dirSize += getDirSize(file); // 递归调用继续统计
            }
        }
        return dirSize;
    }

    /**
     * 获取目录文件个数
     * 
     * @param f
     * @return
     */
    public long getFileList(File dir) {
        long count = 0;
        File[] files = dir.listFiles();
        count = files.length;
        for (File file : files) {
            if (file.isDirectory()) {
                count = count + getFileList(file);// 递归
                count--;
            }
        }
        return count;
    }

    public static byte[] toBytes(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int ch;
        while ((ch = in.read()) != -1) {
            out.write(ch);
        }
        byte buffer[] = out.toByteArray();
        out.close();
        return buffer;
    }

    /**
     * 检查文件是否存在
     * 
     * @param name
     * @return
     */
    public static boolean checkFileExists(String name) {
        boolean status;
        if (!name.equals("")) {
            File path = Environment.getExternalStorageDirectory();
            File newPath = new File(path.toString() + name);
            status = newPath.exists();
        } else {
            status = false;
        }
        return status;
    }

    /**
     * 检查路径是否存在
     * 
     * @param path
     * @return
     */
    public static boolean checkFilePathExists(String path) {
        return new File(path).exists();
    }

    /**
     * 计算SD卡的剩余空间
     * 
     * @return 返回-1，说明没有安装sd卡
     */
    public static long getFreeDiskSpace() {
        String status = Environment.getExternalStorageState();
        long freeSpace = 0;
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            try {
                File path = Environment.getExternalStorageDirectory();
                StatFs stat = new StatFs(path.getPath());
                long blockSize = stat.getBlockSize();
                long availableBlocks = stat.getAvailableBlocks();
                freeSpace = availableBlocks * blockSize / 1024;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return -1;
        }
        return (freeSpace);
    }

    /**
     * 新建目录
     * 
     * @param directoryName
     * @return
     */
    public static boolean createDirectory(String directoryName) {
        boolean status;
        if (!directoryName.equals("")) {
            File path = Environment.getExternalStorageDirectory();
            File newPath = new File(path.toString() + directoryName);
            status = newPath.mkdir();
            status = true;
        } else status = false;
        return status;
    }

    public static String getSDpath() {
        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        File sdDir = null;
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
        }
        if (sdDir != null) {
            return sdDir.toString();
        } else {
            return "";
        }

    }

    /**
     * 检查是否安装SD卡
     * 
     * @return
     */
    public static boolean checkSaveLocationExists() {
        String sDCardStatus = Environment.getExternalStorageState();
        boolean status;
        if (sDCardStatus.equals(Environment.MEDIA_MOUNTED)) {
            status = true;
        } else status = false;
        return status;
    }

    /**
     * 检查是否安装外置的SD卡
     * 
     * @return
     */
    public static boolean checkExternalSDExists() {

        Map<String, String> evn = System.getenv();
        return evn.containsKey("SECONDARY_STORAGE");
    }

    /**
     * 删除目录(包括：目录里的所有文件)
     * 
     * @param fileName
     * @return
     */
    public static boolean deleteDirectory(String fileName) {
        boolean status;
        SecurityManager checker = new SecurityManager();

        if (!fileName.equals("")) {
            File newPath = new File(fileName);
            checker.checkDelete(newPath.toString());
            if (newPath.isDirectory()) {
                String[] listfile = newPath.list();
                // delete all files within the specified directory and then
                // delete the directory
                try {
                    for (int i = 0; i < listfile.length; i++) {
                        File deletedFile = new File(newPath.toString() + "/" + listfile[i].toString());
                        deletedFile.delete();
                    }
                    newPath.delete();
                    LogWriter.i("DirectoryManager deleteDirectory", fileName);
                    status = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    status = false;
                }

            } else status = false;
        } else status = false;
        return status;
    }

    /**
     * 删除文件
     * 
     * @param fileName
     * @return
     */
    public static boolean deleteFile(String fileName) {
        boolean status;
        SecurityManager checker = new SecurityManager();

        if (!fileName.equals("")) {

            File path = Environment.getExternalStorageDirectory();
            File newPath = new File(path.toString() + fileName);
            checker.checkDelete(newPath.toString());
            if (newPath.isFile()) {
                try {
                	LogWriter.i("DirectoryManager deleteFile", fileName);
                    newPath.delete();
                    status = true;
                } catch (SecurityException se) {
                    se.printStackTrace();
                    status = false;
                }
            } else status = false;
        } else status = false;
        return status;
    }

    /**
     * 删除空目录 返回 0代表成功 ,1 代表没有删除权限, 2代表不是空目录,3 代表未知错误
     * 
     * @return
     */
    public static int deleteBlankPath(String path) {
        File f = new File(path);
        if (!f.canWrite()) {
            return 1;
        }
        if (f.list() != null && f.list().length > 0) {
            return 2;
        }
        if (f.delete()) {
            return 0;
        }
        return 3;
    }

    /**
     * 重命名
     * 
     * @param oldName
     * @param newName
     * @return
     */
    public static boolean reNamePath(String oldName, String newName) {
        File f = new File(oldName);
        return f.renameTo(new File(newName));
    }

    /**
     * 删除文件
     * 
     * @param filePath
     */
    public static boolean deleteFileWithPath(String filePath) {
        SecurityManager checker = new SecurityManager();
        File f = new File(filePath);
        checker.checkDelete(filePath);
        if (f.isFile()) {
        	LogWriter.i("DirectoryManager deleteFile", filePath);
            f.delete();
            return true;
        }
        return false;
    }

    /**
     * 清空一个文件夹
     * 
     * @param files
     */
    public static void clearFileWithPath(String filePath) {
        List<File> files = FileUtils.listPathFiles(filePath);
        if (files.isEmpty()) {
            return;
        }
        for (File f : files) {
            if (f.isDirectory()) {
                clearFileWithPath(f.getAbsolutePath());
            } else {
                f.delete();
            }
        }
    }

    public static boolean isSDCardMounted() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {

            return false;
        }
    }

    /**
     * 获取SD卡的根目录
     * 
     * @return
     */
    public static String getSDRoot() {

        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * 获取手机外置SD卡的根目录
     * 
     * @return
     */
    public static String getExternalSDRoot() {

        Map<String, String> evn = System.getenv();

        return evn.get("SECONDARY_STORAGE");
    }

    /**
     * 列出root目录下所有子目录
     * 
     * @param path
     * @return 绝对路径
     */
    public static List<String> listPath(String root) {
        List<String> allDir = new ArrayList<String>();
        SecurityManager checker = new SecurityManager();
        File path = new File(root);
        checker.checkRead(root);
        // 过滤掉以.开始的文件夹
        if (path.isDirectory()) {
            for (File f : path.listFiles()) {
                if (f.isDirectory() && !f.getName().startsWith(".")) {
                    allDir.add(f.getAbsolutePath());
                }
            }
        }
        return allDir;
    }

    /**
     * 获取一个文件夹下的所有文件
     * 
     * @param root
     * @return
     */
    public static List<File> listPathFiles(String root) {
        List<File> allDir = new ArrayList<File>();
        SecurityManager checker = new SecurityManager();
        File path = new File(root);
        checker.checkRead(root);
        File[] files = path.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isFile())
                    allDir.add(f);
                else listPath(f.getAbsolutePath());
            }
        }
        return allDir;
    }

    public enum PathStatus {
        SUCCESS, EXITS, ERROR
    }

    /**
     * 创建目录
     * 
     * @param path
     */
    public static PathStatus createPath(String newPath) {
        File path = new File(newPath);
        if (path.exists()) {
            return PathStatus.EXITS;
        }
        if (path.mkdir()) {
            return PathStatus.SUCCESS;
        } else {
            return PathStatus.ERROR;
        }
    }

    /**
     * 截取路径名
     * 
     * @return
     */
    public static String getPathName(String absolutePath) {
        int start = absolutePath.lastIndexOf(File.separator) + 1;
        int end = absolutePath.length();
        return absolutePath.substring(start, end);
    }

    /**
     * 获取应用程序缓存文件夹下的指定目录
     * 
     * @param context
     * @param dir
     * @return
     */
    public static String getAppCache(Context context, String dir) {
        String savePath = context.getCacheDir().getAbsolutePath() + "/" + dir + "/";
        File savedir = new File(savePath);
        if (!savedir.exists()) {
            savedir.mkdirs();
        }
        savedir = null;
        return savePath;
    }

    public static File getBasepath() {
        File appCacheDir = null;
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            appCacheDir = new File(Environment.getExternalStorageDirectory(), BASE_PATH);
        }

        // if (appCacheDir == null) {
        // if (new File(IConfig.APP_CACHE_DIR_M).exists()) {
        // appCacheDir = new File(new File(IConfig.APP_CACHE_DIR_M), BASE_PATH);
        // } else {
        // appCacheDir = new File(IConfig.APP_CACHE_DIR);
        // }
        // }

        if (appCacheDir == null) {
            // 没有挂载SD卡 就存手机data/data下
            String savePath = HostApplication.getInstance().getCacheDir().getAbsolutePath();
            appCacheDir = new File(savePath);
        }

        if (!appCacheDir.exists())
            appCacheDir.mkdirs();
        return appCacheDir;
    }

    public static File getAudioPath() {
        File file = new File(getBasepath(), AUDIO_CATCH);
        if (!file.exists())
            file.mkdirs();
        return file;
    }

    public static File getChatImagePath() {
        File file = new File(getBasepath(), CHATIMAGE_CATCH);
        if (!file.exists())
            file.mkdirs();
        return file;
    }

    /**
     * 上传图片返回的地址取文件名
     * 
     * @param uri
     * @return
     */
    public static String uriImageFileName(String uri) {
        if (uri != null) {
            return uri.substring(uri.lastIndexOf(File.separator) + 1);// "/"
        }
        return null;
    }

    /*
     * to base64
     */
    @SuppressLint("NewApi")
    public static String fileToBase64(String filename) {
        FileInputStream fis = null;
        byte buffers[] = null;
        try {
            File file = new File(filename);
            if (!file.exists())
                return null;

            fis = new FileInputStream(file);
            buffers = new byte[(int) file.length()];
            fis.read(buffers);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (buffers != null) {
            String result = null;
            try {
                byte[] base64 = Base64.encode(buffers, Base64.DEFAULT);
                result = new String(base64, "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        } else {
            return null;
        }
    }

    /*
     * to base64
     */
    @SuppressLint("NewApi")
    public static String fileToBase64stream(File file) {
        FileInputStream fis = null;
        byte buffers[] = null;
        try {
            fis = new FileInputStream(file);
            buffers = new byte[(int) file.length()];
            fis.read(buffers);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (buffers != null) {
            String result = null;
            try {
                byte[] base64 = Base64.encode(buffers, Base64.DEFAULT);
                result = new String(base64, "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        } else {
            return null;
        }
    }

    /*
     * to base64
     */
    @SuppressLint("NewApi")
    public static String test055(String filename) {
        String filedata = "";

        try {
            File inFile = new File(filename);
            FileInputStream fileInputStream = new FileInputStream(inFile);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int i;
            // 转化为字节数组流
            while ((i = fileInputStream.read()) != -1) {
                byteArrayOutputStream.write(i);
            }
            fileInputStream.close();
            // 把文件存在一个字节数组中
            byte[] filea = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();
            byte[] base64 = Base64.encode(filea, Base64.DEFAULT);
            filedata = new String(base64, "utf-8");
            return filedata;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return filedata;

    }

    /***
     * 写入图片
     * 
     * @param bitmap
     * @param destPath
     * @param quality
     */
    public static void writeImage(Bitmap bitmap, String destPath, int quality) {
        try {
            deleteFile(destPath);
            if (createFile(destPath)) {
                FileOutputStream out = new FileOutputStream(destPath);
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)) {
                    out.flush();
                    out.close();
                    out = null;
                }
            }
        } catch (IOException e) {
            // e.printStackTrace();
            // LogUtil.d(e.getMessage());
        }
    }

    /**
     * 拷贝文件
     * 
     * @param src
     * @param dst
     */
    public static void fileCopyFile(File src, File dst) {
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(dst));
            bis = new BufferedInputStream(new FileInputStream(src));
            byte[] byteArray = new byte[1024];
            int len = 0;
            while ((len = bis.read(byteArray)) != -1) {
                bos.write(byteArray, 0, len);
                bos.flush();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public static void writeLog(Context context, String content) {

        FileWriter writer = null;
        try {
            if (!checkSaveLocationExists()) {// sd卡未安装
                ToastHelper.makeText(context, "SD卡未安装", 0).show();
                return;
            }
            File file = new File(Environment.getExternalStorageDirectory(), "JiyuLog.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            writer = new FileWriter(file, true);
            writer.write("===>" + content);
            writer.write("\r\n");
            writer.flush();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 使用文件通道的方式复制文件
     * 
     * @param s
     *            源文件
     * @param t
     *            复制到的新文件
     */

    public static void fileChannelCopy(File s, File t) {
        if (s == null || !s.exists() || t == null)
            return;
        if (t.exists()) {
            t.delete();
        }

        try {
            t.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            fi = new FileInputStream(s);
            fo = new FileOutputStream(t);
            in = fi.getChannel();// 得到对应的文件通道
            out = fo.getChannel();// 得到对应的文件通道
            in.transferTo(0, in.size(), out);// 连接两个通道，并且从in通道读取，然后写入out通道
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fi.close();
                in.close();
                fo.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}