package com.example.bdpostapp.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FilePathUtil {

    public static String TAG = "FilePathUtil";

    public static String APP = "BDPOST";
    public static String HCScreenshot = "hcscreenshot";  // 海康摄像头拍照标识
    public static String HCAlarm = "hcalarm";  // 海康摄像头报警标识
    public static String IMG = "images";
    public static String VOI = "voice";
    public static String BMP = "bmps";
    public static String JP2 = "JP2s";
    public static String LOGS = "logs";
    public static String jpgEndWith = ".jpg";
    public static String jpegEndWith = ".jpeg";
    public static String pcmEndWith = ".pcm";
    public static String bmpEndWith = ".bmp";
    private static File mFile =  Environment.getExternalStorageDirectory();
    public final  static  String sdcard = mFile.toString();

    /***
     * 判断SD卡是否存在
     * @return
     */
    private static boolean existSDCard(){
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return true;
        }
        return false;
    }

    //判断是否可写
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    //判断是否可读
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    //检查文件夹是否存在，不存在则创建
    public static void mkdirFile(String url){
        File file = new File(url);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    // 获取app文件存储路径
    public static String getAPPFile(){
        StringBuilder strBuf=new StringBuilder();
        strBuf.append(sdcard);
        strBuf.append("/");
        strBuf.append(APP);
        mkdirFile(strBuf.toString());
        strBuf.append("/");
        return strBuf.toString();  // sdcard/app/
    }

    //获取jpg图片存储路径
    public static String getImagesFile(){
        return createPath(IMG);
    }
    //获取录音文件存储路径
    public static String getVoiceFile(){
        return createPath(VOI);
    }
    //获取BMP图片存储路径
    public static String getBmpFile(){
        return createPath(BMP);
    }
    //获取jp2存储路径
    public static String getJP2File(){
        return createPath(JP2);
    }
    //获取日志存储路径
    public static String getLogsFile(){
        return createPath(LOGS);
    }
    public static String createPath(String folder){
        StringBuilder strBuf=new StringBuilder();
        strBuf.append(getAPPFile());
        strBuf.append(folder);
        mkdirFile(strBuf.toString());
        strBuf.append("/");
        return strBuf.toString();  // sdcard/app/camera/
    }


    // 获取图片bitmap
    public static Bitmap getBitmap(String path){
        try {
            FileInputStream fis = new FileInputStream(path);
            return BitmapFactory.decodeStream(fis);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 根据分辨率压缩图片比例
     *
     * @param imgPath
     * @param w
     * @param h
     * @return
     */
    public static Bitmap compressByResolution(String imgPath, int w, int h) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath, opts);

        int width = opts.outWidth;
        int height = opts.outHeight;
        int widthScale = width / w;
        int heightScale = height / h;
        int scale;
        if (widthScale > heightScale) { //保留压缩比例小的
            scale = widthScale;
        } else {
            scale = heightScale;
        }
        if (scale < 1) {
            scale = 1;
        }
        Log.e("compressByResolution:","图片分辨率压缩比例：" + scale);

        opts.inSampleSize = scale;

        opts.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(imgPath, opts);
    }
    public static void saveBitmapFile(Bitmap bitmap, String path){
        File file=new File(path);//将要保存图片的路径
        delectFile(path);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
            Log.e("saveBitmapFile: ", "图片保存成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // 获取可用空间大小
    public static long getAvailableSize(){
        StatFs sf = new StatFs(mFile.getPath());
        long availableBlocks = sf.getAvailableBlocksLong();
//        Log.e("FileMangram","可用存储块数量:"+availableBlocks);
        long blockCount = sf.getBlockCount();
//        Log.e("FileMangram", "总存储块数量：" + blockCount);
        long size = sf.getBlockSizeLong();
//        Log.e("FileMangram", "存储块大小:" + size + "字节");
        long totalSize = blockCount * size;
//        Log.e("FileMangram", "总空间:" + totalSize + "字节");
        long availableSize = availableBlocks * size;
//        Log.e("FileMangram", "可用空间:" + availableSize + "字节");
//        writeLogs("可用空间:" + availableSize + "字节");
        return availableSize;
    }

    //检查文件空间，存储不足时清理旧图片
    public static void checkSpace(){
        if (getAvailableSize() < (1024 * 1024 * 10)){
            writeLogs("可用空间小于:"+(1024 * 1024 * 10)+"字节");
            File file = new File(getImagesFile());
            File[] templist = file.listFiles();
            int scale = (int) (templist.length * 0.1);
            for (int i=0; i<scale;i++){
                String delFile = templist[i].getName();
                delectFile(delFile);
            }
        }
    }

    // 删除文件
    public static void delectFile(String delFile){
        File file = new File(delFile);
        if (file.exists() && file.isFile()){
            file.delete();
            Log.e("delectFile: ", "图片删除成功");
        }
    }

    public static void writeLogs(String content) {
        try {
            String info = "*************** Error ***************\n" ;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            String today = sdf.format(new Date());
            String curTime = sdf.format(new Date());
            info += curTime + "\n" ;
            info += content + "\n" ;

            String file_path = getLogsFile()+"log_x_" +today+ ".txt";
//            String file_path = getLogsFile()+"log_x_" + ".txt";
            final File kmlFile = new File(file_path);
            if (!kmlFile.exists()) {
                kmlFile.createNewFile();
            }
            RandomAccessFile randomAccessFile = new RandomAccessFile(kmlFile, "rw");
            randomAccessFile.seek(kmlFile.length());
            randomAccessFile.write(info.getBytes());
            randomAccessFile.close();
            Log.e(TAG, "写入日志成功，路径: " + file_path );
        } catch (Exception e) {
            Log.e(TAG, "writeLogs:写入失败");
            e.printStackTrace();
        }
    }

}
