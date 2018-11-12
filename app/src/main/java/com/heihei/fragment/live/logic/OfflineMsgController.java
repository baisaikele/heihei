package com.heihei.fragment.live.logic;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.json.JSONException;
import org.json.JSONObject;

import com.base.host.AppLogic;
import com.base.http.HttpUtil;
import com.base.http.HttpUtil.DownloadListener;
import com.base.utils.FileUtils;
import com.base.utils.LogWriter;
import com.base.utils.MD5;
import com.base.utils.StringUtils;
import com.heihei.model.msg.bean.AbstractMessage;
import com.heihei.scoket.MessageDistribute;

import android.util.JsonReader;

public class OfflineMsgController {

    private static final String TAG = "OfflineMsgController";

    private ConcurrentHashMap<Long, List<AbstractMessage>> mMap;

    private String url;

    public OfflineMsgController(String url) {
        this.url = url;
        mMap = new ConcurrentHashMap<>();
    }

    public void download() {
        if (StringUtils.isEmpty(url)) {
            return;
        }
        String dstPath = AppLogic.defaultMessages.concat(MD5.getMD5(url));
        String downloadPath = AppLogic.defaultMessages.concat(System.currentTimeMillis() + "");
        final File dstFile = new File(dstPath);
        if (dstFile.exists()) {
            FileUtils.deleteDirectory(dstPath);
        }
        final File downloadFile = new File(downloadPath);
        LogWriter.d(TAG, "start down:" + url);
        HttpUtil.downloadFile(url, downloadFile, new DownloadListener() {

            @Override
            public void onSuccess(String url) {
                if (OfflineMsgController.this.url.equals(url)) {
                	LogWriter.d(TAG, "download success:" + url);
                    unzip(downloadFile, dstFile);
                    downloadFile.delete();
                    LogWriter.d(TAG, "unzip finish:" + url);
                    parse();
                }
            }

            @Override
            public void onFailed(String url) {
                if (OfflineMsgController.this.url.equals(url)) {
                	LogWriter.d(TAG, "download failed:" + url);
                }
            }
        });
    }

    public void parse() {
    	LogWriter.d(TAG, "thread:" + Thread.currentThread().getName());
        String dstPath = AppLogic.defaultMessages.concat(MD5.getMD5(url));
        File dstFile = new File(dstPath);
        if (!dstFile.exists()) {
            return;
        }
        File[] files = dstFile.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                /** 有可能文件很大，用流式解析的方式 */
                try {
                    FileReader fr = new FileReader(file);
                    JsonReader reader = new JsonReader(fr);
                    reader.beginArray();
                    while (reader.hasNext()) {
                        String str = reader.nextString();
                        parseMessage(str);
                        LogWriter.d(TAG, str);
                    }
                    reader.endArray();
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    }

    public void parseMessage(String str) {
        try {
            JSONObject json = new JSONObject(str);
            String msgStr = json.optString("msg");
            long timeStamp = json.optLong("timeStamp");
            JSONObject msgJ = new JSONObject(msgStr);
            String msgType = msgJ.optString("msgType");

            AbstractMessage message = null;

            switch (msgType) {
            case AbstractMessage.MESSAGE_TYPE_LIKE :
                message = MessageDistribute.getInstance().paserLikeMessage(msgJ);
                break;
            case AbstractMessage.MESSAGE_TYPE_TEXT :
                message = MessageDistribute.getInstance().paserTextMessage(msgJ);
                break;
            case AbstractMessage.MESSAGE_TYPE_SYSTEM :
                message = MessageDistribute.getInstance().paserSystemMessage(msgJ);
                break;
            case AbstractMessage.MESSAGE_TYPE_BARRAGE :
                message = MessageDistribute.getInstance().paserBarrageMessage(msgJ);
                break;
            case AbstractMessage.MESSAGE_TYPE_GIFT :
                message = MessageDistribute.getInstance().paserGiftMessage(msgJ);
                break;
            case AbstractMessage.MESSAGE_TYPE_ACTION :
                break;

            default:
                break;
            }

            if (message != null) {
                message.mSaveTimestamp = timeStamp;
                List<AbstractMessage> list = mMap.get(message.mSaveTimestamp);
                if (list == null)
                {
                    list = Collections.synchronizedList(new ArrayList<AbstractMessage>());
                }
                list.add(message);
                if (!mMap.containsKey(message.mSaveTimestamp))
                {
                    mMap.put(message.mSaveTimestamp, list);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<AbstractMessage> getMessageByTimestamp(Long timestamp) {
        List<AbstractMessage> list = mMap.get(timestamp);
        return list;
    }

    public void release()
    {
        if (mMap != null)
        {
            mMap.clear();
        }
    }
    
    /**
     * 解压
     * 
     * @param file
     */
    private void unzip(File zipFile, File unzipFile) {
        try {

        	LogWriter.d(TAG, "unzip:" + unzipFile.getAbsolutePath());

            // 开始解压
            ZipEntry entry = null;
            String entryFilePath = null, entryDirPath = null;
            File entryFile = null, entryDir = null;
            int index = 0, count = 0, bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;
            ZipFile zip = new ZipFile(zipFile);
            Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zip.entries();

            if (!unzipFile.exists()) {
                unzipFile.mkdirs();
            }

            String unzipFilePath = unzipFile.getAbsolutePath();
            // 循环对压缩包里的每一个文件进行解压
            while (entries.hasMoreElements()) {
                entry = entries.nextElement();
                // 构建压缩包中一个文件解压后保存的文件全路径
                entryFilePath = unzipFilePath + File.separator + entry.getName();
                // 构建解压后保存的文件夹路径
                index = entryFilePath.lastIndexOf(File.separator);
                if (index != -1) {
                    entryDirPath = entryFilePath.substring(0, index);
                } else {
                    entryDirPath = "";
                }
                entryDir = new File(entryDirPath);
                // 如果文件夹路径不存在，则创建文件夹
                if (!entryDir.exists() || !entryDir.isDirectory()) {
                    entryDir.mkdirs();
                }

                // 创建解压文件
                entryFile = new File(entryFilePath);
                if (entryFile.exists()) {
                    // 检测文件是否允许删除，如果不允许删除，将会抛出SecurityException
                    SecurityManager securityManager = new SecurityManager();
                    securityManager.checkDelete(entryFilePath);
                    // 删除已存在的目标文件
                    entryFile.delete();
                }

                // 写入文件
                bos = new BufferedOutputStream(new FileOutputStream(entryFile));
                bis = new BufferedInputStream(zip.getInputStream(entry));
                while ((count = bis.read(buffer, 0, bufferSize)) != -1) {
                    bos.write(buffer, 0, count);
                }
                bos.flush();
                bos.close();
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

}
