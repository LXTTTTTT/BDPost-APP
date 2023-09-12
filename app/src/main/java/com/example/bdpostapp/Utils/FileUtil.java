package com.example.bdpostapp.Utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtil {
    /** 最外层目录 **/
    public final static String SYS_TEMMD = "md590";

    /** 缓存目录 **/
    public final static String CACHE = "cache";

    /** 缓存LOG目录 **/
    public final static String LOGS = "logs";

    /** 缓存图片文件目录 **/
    public final static String IMG = "img";

    /** 缓存图片文件目录 **/
    public final static String TEMPORARY = "temporary";

    /** 缓存音频文件目录 **/
    public final static String audio = "audio";
    /** 缓存音频文件目录 **/
    public final static String PCM = "pcm";
    /** 缓存音频文件目录 **/
    public final static String ACC = "acc";
    /** 发送图片存储路径 **/
    public final static String SEND_IMG="sendimg";
    /** 接收图片存储路径 **/
    public final static String REND_IMG="rend_img";
    /** 接收语音文件存储路径 **/
    public final static String REND_VOICE="rend_voice";
    /** 意见反馈图片存储路径 **/
    public final static String FEED_PIC="feedPic";
    /** 上传直播封面图片存储路径 **/
    public final static String live_cover_pic="livePic";
    /** 列表显示直播封面存储路径 **/
    public final static String LIVE_LIST_PIC="liveListPic";
    /** 列表显示静态地图 **/
    public final static String STATIC_MAP_IMC="staticMapImg";
    /**nomedia隐藏图片文件不被找到**/
    public final static String NOMEDIA = ".nomedia";
    /**
     * 日志文件
     *
     * @return
     */
    public static String getLogFile() {
        StringBuilder strBuf=new StringBuilder();
        String sdcard = Environment.getExternalStorageDirectory().toString();
        strBuf.append(sdcard);
        strBuf.append("/");
        strBuf.append("md590");
        File file = new File(strBuf.toString());
        if (!file.exists()) {
            file.mkdir();
        }
        return strBuf.toString();
//        File file = new File(strBuf.toString());
//        if (!file.exists()) {
//            file.mkdir();
//        }
//        strBuf.append("/");
//        strBuf.append(CACHE);
//        File file2 = new File(strBuf.toString());
//        if (!file2.exists()) {
//            file2.mkdir();
//        }
//        strBuf.append("/");
//        strBuf.append(LOGS);
//        File file3 = new File(strBuf.toString());
//        if (!file3.exists()) {
//            file3.mkdir();
//        }
//        strBuf.append("/");
//        return strBuf.toString();
    }
    public static String getFeedPicFile() {
        StringBuilder strBuf=new StringBuilder();
        String sdcard = Environment.getExternalStorageDirectory().toString();
        strBuf.append(sdcard);
        strBuf.append("/");
        strBuf.append(SYS_TEMMD);
        File file = new File(strBuf.toString());
        if (!file.exists()) {
            file.mkdir();
        }
        strBuf.append("/");
        strBuf.append(CACHE);
        File file2 = new File(strBuf.toString());
        if (!file2.exists()) {
            file2.mkdir();
        }
        strBuf.append("/");
        strBuf.append(FEED_PIC);
        File file3 = new File(strBuf.toString());
        if (!file3.exists()) {
            file3.mkdir();
        }
        strBuf.append("/");
        return strBuf.toString();
    }

    public static String getLiveCoverPicFile() {
        StringBuilder strBuf=new StringBuilder();
        String sdcard = Environment.getExternalStorageDirectory().toString();
        strBuf.append(sdcard);
        strBuf.append("/");
        strBuf.append(SYS_TEMMD);
        File file = new File(strBuf.toString());
        if (!file.exists()) {
            file.mkdir();
        }
        strBuf.append("/");
        strBuf.append(CACHE);
        File file2 = new File(strBuf.toString());
        if (!file2.exists()) {
            file2.mkdir();
        }
        strBuf.append("/");
        strBuf.append(live_cover_pic);
        File file3 = new File(strBuf.toString());
        if (!file3.exists()) {
            file3.mkdir();
        }
        createNomediaFile(strBuf.toString());
        strBuf.append("/");
        return strBuf.toString();
    }
    public static String getLiveListPicFile() {
        StringBuilder strBuf=new StringBuilder();
        String sdcard = Environment.getExternalStorageDirectory().toString();
        strBuf.append(sdcard);
        strBuf.append("/");
        strBuf.append(SYS_TEMMD);
        File file = new File(strBuf.toString());
        if (!file.exists()) {
            file.mkdir();
        }
        strBuf.append("/");
        strBuf.append(CACHE);
        File file2 = new File(strBuf.toString());
        if (!file2.exists()) {
            file2.mkdir();
        }
        strBuf.append("/");
        strBuf.append(LIVE_LIST_PIC);
        File file3 = new File(strBuf.toString());
        if (!file3.exists()) {
            file3.mkdir();
        }
        createNomediaFile(strBuf.toString());
        strBuf.append("/");
        return strBuf.toString();
    }
    public static String getMapPicFile() {
        StringBuilder strBuf=new StringBuilder();
        String sdcard = Environment.getExternalStorageDirectory().toString();
        strBuf.append(sdcard);
        strBuf.append("/");
        strBuf.append(SYS_TEMMD);
        File file = new File(strBuf.toString());
        if (!file.exists()) {
            file.mkdir();
        }
        strBuf.append("/");
        strBuf.append(CACHE);
        File file2 = new File(strBuf.toString());
        if (!file2.exists()) {
            file2.mkdir();
        }
        strBuf.append("/");
        strBuf.append(STATIC_MAP_IMC);
        File file3 = new File(strBuf.toString());
        if (!file3.exists()) {
            file3.mkdir();
        }
        createNomediaFile(strBuf.toString());
        strBuf.append("/");
        return strBuf.toString();
    }
    /**
     * 得到缓存音频文件目录
     *
     * @return
     */
    public static String getAudioAccFile() {
        String sdcard = Environment.getExternalStorageDirectory().toString();
        File file = new File(sdcard + "/" + SYS_TEMMD);
        if (!file.exists())
            file.mkdir();
        File file2 = new File(sdcard + "/" + SYS_TEMMD + "/"+ audio);
        if (!file2.exists())
            file2.mkdir();
        File file3 = new File(sdcard + "/" + SYS_TEMMD + "/"+ audio+"/"
                + ACC);
        if (!file3.exists())
            file3.mkdir();
        return sdcard + "/" + SYS_TEMMD +"/" + audio+ "/"+ACC+"/";
    }
    public static String logs=getLogFile();
    /**
     * 得到缓存音频文件目录
     *
     * @return
     */
    public static String getAudioPcmFile() {
        String sdcard = Environment.getExternalStorageDirectory().toString();
        File file = new File(sdcard + "/" + SYS_TEMMD);
        if (!file.exists())
            file.mkdir();
        File file2 = new File(sdcard + "/" + SYS_TEMMD + "/"+ audio);
        if (!file2.exists())
            file2.mkdir();
        File file3 = new File(sdcard + "/" + SYS_TEMMD + "/"+ audio+"/"
                + PCM);
        if (!file3.exists())
            file3.mkdir();
        return sdcard + "/" + SYS_TEMMD +"/" + audio+ "/"+PCM+"/";
    }
    /**
     * 得到缓存图片文件目录
     * path
     * @return
     */
    public static String getReceiveVoiceFile() {
        StringBuffer strBuf=new StringBuffer();
        strBuf.append(Environment.getExternalStorageDirectory().toString());
        strBuf.append("/");
        strBuf.append(SYS_TEMMD);
        File file = new File(strBuf.toString());
        if (!file.exists()) {
            file.mkdir();
        }
        strBuf.append("/");
        strBuf.append(audio);
        File file2 = new File(strBuf.toString());
        if (!file2.exists()) {
            file2.mkdir();
        }
        strBuf.append("/");
        strBuf.append(REND_VOICE);
        File file3 = new File(strBuf.toString());
        if (!file3.exists()) {
            file3.mkdir();
        }
        createNomediaFile(strBuf.toString());
        strBuf.append("/");
        return strBuf.toString();
    }
    /**
     * 得到缓存图片文件目录
     * path
     * @return
     */
    public static String getImgFile(String folderName) {
        StringBuffer strBuf=new StringBuffer();
        strBuf.append(Environment.getExternalStorageDirectory().toString());
        strBuf.append("/");
        strBuf.append(SYS_TEMMD);
        File file = new File(strBuf.toString());
        if (!file.exists()) {
            file.mkdir();
        }
        strBuf.append("/");
        strBuf.append(IMG);
        File file2 = new File(strBuf.toString());
        if (!file2.exists()) {
            file2.mkdir();
        }
        strBuf.append("/");
        strBuf.append(SEND_IMG);
        File file3 = new File(strBuf.toString());
        if (!file3.exists()) {
            file3.mkdir();
        }
        strBuf.append("/");
        strBuf.append(folderName);
        File file4 = new File(strBuf.toString());
        if (!file4.exists()) {
            file4.mkdir();
        }
        createNomediaFile(strBuf.toString());
        strBuf.append("/");
        return strBuf.toString();
    }

    /**
     * 得到缓存图片文件目录
     * path
     * @return
     */
    public static String getReceiveImgFile() {
        StringBuffer strBuf=new StringBuffer();
        strBuf.append(Environment.getExternalStorageDirectory().toString());
        strBuf.append("/");
        strBuf.append(SYS_TEMMD);
        File file = new File(strBuf.toString());
        if (!file.exists()) {
            file.mkdir();
        }
        strBuf.append("/");
        strBuf.append(IMG);
        File file2 = new File(strBuf.toString());
        if (!file2.exists()) {
            file2.mkdir();
        }
        strBuf.append("/");
        strBuf.append(REND_IMG);
        File file3 = new File(strBuf.toString());
        if (!file3.exists()) {
            file3.mkdir();
        }
        createNomediaFile(strBuf.toString());
        strBuf.append("/");
        return strBuf.toString();
    }
    /**
     * 得到缓存发送图片文件目录
     * path
     * @return
     */
    public static String getSendImgFile() {
        StringBuffer strBuf=new StringBuffer();
        strBuf.append(Environment.getExternalStorageDirectory().toString());
        strBuf.append("/");
        strBuf.append(SYS_TEMMD);
        File file = new File(strBuf.toString());
        if (!file.exists()) {
            file.mkdir();
        }
        strBuf.append("/");
        strBuf.append(IMG);
        File file2 = new File(strBuf.toString());
        if (!file2.exists()) {
            file2.mkdir();
        }
        strBuf.append("/");
        strBuf.append(SEND_IMG);
        File file3 = new File(strBuf.toString());
        if (!file3.exists()) {
            file3.mkdir();
        }
        createNomediaFile(strBuf.toString());
        strBuf.append("/");

        return strBuf.toString();
    }
    /**
     * 创建隐藏文件夹
     * @param file
     */
    public static void createNomediaFile(String file){
        String nodediaPath = file+ "/.nomedia";
        File nomediaFile = new File(nodediaPath);
        if (!nomediaFile.exists()){
            try {
                nomediaFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 得到缓存图片目录
     *
     * @return
     */
    public static String getCacheImage() {
        String sdcard = Environment.getExternalStorageDirectory().toString();
        File file = new File(sdcard + "/" + SYS_TEMMD);
        if (!file.exists())
            file.mkdir();
        File file2 = new File(sdcard + "/" + SYS_TEMMD + "/" + CACHE);
        if (!file2.exists())
            file2.mkdir();
        File file3 = new File(sdcard + "/" + SYS_TEMMD + "/" + CACHE + "/"
                + IMG);
        if (!file3.exists())
            file3.mkdir();
        createNomediaFile(sdcard + "/" + SYS_TEMMD + "/" + CACHE + "/" + IMG);
        return sdcard + "/" + SYS_TEMMD + "/" + CACHE + "/" + IMG + "/";
    }


    /**
     * Uri转File
     *
     * @param uri
     * @param context
     * @return
     */
    public static File getFileByUri(Uri uri, Context context) {
        String path = null;
        if ("file".equals(uri.getScheme())) {
            path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = context.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=").append("'" + path + "'").append(")");
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA}, buff.toString(), null, null);
                int index = 0;
                int dataIdx = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    index = cur.getInt(index);
                    dataIdx = cur.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    path = cur.getString(dataIdx);
                }
                cur.close();
                if (index == 0) {
                } else {
                    Uri u = Uri.parse("content://media/external/images/media/" + index);
                    //Log.i("InfoMessage", "temp uri is :" + u);
                }
            }
            if (path != null) {
                return new File(path);
            }
        } else if ("content".equals(uri.getScheme())) {
            // 4.2.2以后
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                path = cursor.getString(columnIndex);
            }
            cursor.close();

            return new File(path);
        } else {
            //Log.i(TAG, "Uri Scheme:" + uri.getScheme());
        }
        return null;
    }
    /**
     * 文件转byte
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static byte[] InputStream2ByteArray(File file) throws IOException {

        InputStream in = new FileInputStream(file);
        byte[] data = toByteArray(in);
        in.close();
        return data;
    }
    public static byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n = 0;
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        return out.toByteArray();
    }
    /**
     * 获取Assets文件夹下的json文件
     *
     * @param context
     * @param fileName 文件名
     */
    public static String getAssetsFile(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        InputStream inputStream = null;
        try {
            inputStream = context.getResources().getAssets().open(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String jsonLine;
            while ((jsonLine = bufferedReader.readLine()) != null) {
                stringBuilder.append(jsonLine);
            }
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
