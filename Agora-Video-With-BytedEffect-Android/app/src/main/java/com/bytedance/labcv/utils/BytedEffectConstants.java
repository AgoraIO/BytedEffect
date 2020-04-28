// Copyright (C) 2018 Beijing Bytedance Network Technology Co., Ltd.
package com.bytedance.labcv.effectsdk;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;

public class BytedEffectConstants {

    public static final String TAG = "bef_effect_ai";

    /**
     * 错误码枚举
     * Error code enumeration
     */
    public static class BytedResultCode {

        /**
         * 成功返回
         * return success
         */
        public static final int BEF_RESULT_SUC = 0;

        /**
         * 内部错误
         * Internal error
         */
        public static final int BEF_RESULT_FAIL = -1;

    }


    /**
     * 图像格式
     * Image format
     */
    public enum PixlFormat {
        RGBA8888(0),
        BGRA8888(1),
        BGR888(2),
        RGB888(3),
        BEF_AI_PIX_FMT_YUV420P(5),
        BEF_AI_PIX_FMT_NV12(6),
        BEF_AI_PIX_FMT_NV21(7);

        private int value;

        PixlFormat(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * 纹理格式
     */
    public enum TextureFormat{
        Texure2D(GLES20.GL_TEXTURE_2D),
        Texture_Oes(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);

        private int value;

        TextureFormat(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }


    }


    /**
     * 图像旋转角
     * Image rotation
     */
    public enum Rotation {
        /**
         * 图像不需要旋转，图像中的人脸为正脸
         * The image does not need to be rotated. The face in the image is positive
         */
        CLOCKWISE_ROTATE_0(0),
        /**
         * 图像需要顺时针旋转90度，使图像中的人脸为正
         * The image needs to be rotated 90 degrees clockwise so that the face in the image is positive
         */
        CLOCKWISE_ROTATE_90(1),
        /**
         * 图像需要顺时针旋转180度，使图像中的人脸为正
         * The image needs to be rotated 180 degrees clockwise so that the face in the image is positive
         */
        CLOCKWISE_ROTATE_180(2),
        /**
         * 图像需要顺时针旋转270度，使图像中的人脸为正
         * The image needs to be rotated 270 degrees clockwise so that the face in the image is positive
         */
        CLOCKWISE_ROTATE_270(3);

        public int id = 0;

        Rotation(int id) {
            this.id = id;
        }
    }




    /**
     * 强度类型
     * The intensity of the type
     */
    public enum IntensityType {
        /**
         * 调节滤镜
         * Adjust the filter
         */
        Filter(12);

        private int id;

        public int getId() {
            return id;
        }

        IntensityType(int id) {
            this.id = id;
        }
    }












}
