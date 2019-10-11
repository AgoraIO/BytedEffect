package io.agora.rtcwithbyte.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.ExifInterface;
import android.opengl.GLES20;
import android.os.Environment;
import android.text.TextUtils;

//import com.bytedance.labcv.effectsdk.library.LogUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import library.LogUtils;

import static android.os.Environment.DIRECTORY_DCIM;

public class BitmapUtils {

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }

            long totalPixels = width * height / inSampleSize;

            final long totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels > totalReqPixelsCap) {
                inSampleSize *= 2;
                totalPixels /= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * 压缩Bitmap的大小
     *
     * @param imagePath     图片文件路径
     * @param requestWidth  压缩到想要的宽度
     * @param requestHeight 压缩到想要的高度
     * @return
     */
    public static Bitmap decodeBitmapFromFile(String imagePath, int requestWidth, int requestHeight) {
        if (!TextUtils.isEmpty(imagePath)) {
            if (requestWidth <= 0 || requestHeight <= 0) {
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                return bitmap;
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;//不加载图片到内存，仅获得图片宽高
            BitmapFactory.decodeFile(imagePath, options);
            if (options.outHeight == -1 || options.outWidth == -1) {
                try {
                    ExifInterface exifInterface = new ExifInterface(imagePath);
                    int height = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, ExifInterface.ORIENTATION_NORMAL);//获取图片的高度
                    int width = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, ExifInterface.ORIENTATION_NORMAL);//获取图片的宽度

                    options.outWidth = width;
                    options.outHeight = height;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            options.inSampleSize = calculateInSampleSize(options, requestWidth, requestHeight); //计算获取新的采样率
            LogUtils.d( "inSampleSize: " + options.inSampleSize);
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(imagePath, options);

        } else {
            return null;
        }
    }

    public static ByteBuffer bitmap2ByteBuffer(final Bitmap bitmap){
        int bytes = bitmap.getByteCount();

        ByteBuffer buffer = ByteBuffer.allocateDirect(bytes);
        bitmap.copyPixelsToBuffer(buffer);
        return buffer;

    }

    public static File saveToLocal(Bitmap bitmap){
        if (null == bitmap) return null;
        String temp = CommonUtils.createtFileName(".png");
        File dcimFile =  Environment.getExternalStoragePublicDirectory(DIRECTORY_DCIM);
        File tempFile = new File(dcimFile,temp);
        try {
            FileOutputStream fos = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            tempFile = null;
        }catch (IOException e){
            e.printStackTrace();
            tempFile = null;
        }
        return tempFile;


    }


    //Dump current binding framebuffer into bitmap
    //Note: Please invoke this method in GL thread otherwise the behaviour is undefined
    public static Bitmap bitmapFromGLFramebuffer(int width, int height, boolean flipVertical) {
        if ((width <= 0 || height <= 0)) throw new AssertionError();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        ByteBuffer buffer = ByteBuffer.allocateDirect(width*height*4);
        GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buffer);
        bitmap.copyPixelsFromBuffer(buffer);
        if (flipVertical) {
            Bitmap ret = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas ctx = new Canvas(ret);
            ctx.scale(1, -1);
            ctx.translate(0, -height);
            ctx.drawBitmap(bitmap, 0, 0, null);
            return ret;
        }
        return bitmap;
    }


    //Dump specified texture into bitmap
    //Note: Please invoke this method in GL thread otherwise the behaviour is undefined
    //Warning: This method will effect GL state, use at your own risk
    public static Bitmap bitmapFromGLTexture(int texture, int width, int height, boolean flipVertical) {
        if ((width <= 0 || height <= 0)) throw new AssertionError();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        ByteBuffer buffer = ByteBuffer.allocateDirect(width*height*4);

        int[] fbs = new int[1];
        GLES20.glGenFramebuffers(1, fbs, 0);
//        if ((GLES20.glGetError() == GLES20.GL_NO_ERROR)) throw new AssertionError();

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbs[0]);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE10);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, texture, 0);
        GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buffer);

        //unbind
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glDeleteFramebuffers(1, fbs, 0);

        bitmap.copyPixelsFromBuffer(buffer);
        if (flipVertical) {
            Bitmap ret = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas ctx = new Canvas(ret);
            ctx.scale(1, -1);
            ctx.translate(0, -height);
            ctx.drawBitmap(bitmap, 0, 0, null);
            return ret;
        }
        return bitmap;
    }


}
