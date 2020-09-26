package io.agora.rtcwithbyte;

import java.text.SimpleDateFormat;

/**
 * 通用工具
 */
public class CommonUtils {
    private static long lastclicked = 0;
    private static final long INTERVAL = 300;

    /**
     * 判断快速点击
     * @return
     */
     public static boolean isFastClick(){
         if (System.currentTimeMillis() - lastclicked < INTERVAL) {
             lastclicked = System.currentTimeMillis();
             return true;
         }
         lastclicked = System.currentTimeMillis();
         return false;
     }

    public static synchronized String createtFileName(String suffix) {
        java.util.Date dt = new java.util.Date(System.currentTimeMillis());
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String fileName= fmt.format(dt);
        fileName = fileName + suffix; //extension, you can change it.
        return fileName;
    }



}
