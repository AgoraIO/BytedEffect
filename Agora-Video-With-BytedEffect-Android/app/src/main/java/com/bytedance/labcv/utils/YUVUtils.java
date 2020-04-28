package com.bytedance.labcv.effectsdk;

public class YUVUtils {
    /**
     * 对YUV数据转换为RGBA格式，同时支持压缩和旋转和翻转
     * 我们会首先对YUV进行尺寸压缩，然后进行格式转换，最后进行翻转操作（如果需要），因此需要确保压缩后的YUV尺寸是偶数
     * Convert YUV data to RGBA format, while supporting compression and rotation and rollover
     * we will first compress the YUV size, then format it, and then flip it (if needed),
     * so we need to make sure that the compressed YUV size is even
     * @param data yuv image data, yuv输入
     * @param dst Output in RGBA format RGBA格式输出
     * @param pixel_format 输入数据格式，目前支持YUV420P,NV12和NV21 如果不在这几种范围内，默认当成NV21处理
     *                     Input data format, currently support YUV420P,NV12 and NV21 if not in these ranges,
     *                     the default as NV21 processing
     * @param image_width 图片宽度 确保是偶数
     *                    image width, make sure to be even
     * @param image_height 图片高度 确保是偶数
     *                     image height, make sure to be even
     * @param dst_width  目标尺寸 确保是偶数
     *                      dest width, make sure to be even
     * @param dst_height 目标尺寸 确保是偶数
     *                      dest height, make sure to be even
     * @param orientation 输入图片的旋转角度，一般YUV输出有一定旋转角度，需要转正
     *                      Input picture rotation Angle, general YUV output has a certain rotation Angle, need to be positive
     * @param isFront 是否是前置摄像头，如果是前置摄像头，会对数据进行左右翻转
     *                 whether there is a front-facing camera. If it's a front-facing camera, it flips the data left and right
     */
    public static   native void YUV2RGBA(byte[]data, byte[]dst, int pixel_format, int image_width, int image_height, int dst_width, int dst_height, int orientation,boolean isFront);
    /**
     * 将RGBA格式数据转为YUV数据
     * Convert RGBA format data to YUV data
     * @param data RGBA input RGBA输入
     * @param dst YUV output YUV格式输出
     * @param pixel_format 输出YUV数据格式，目前支持YUV420P,NV12和NV21 如果不在这几种范围内，默认当成NV21处理
     *                     Input data format, currently support YUV420P,NV12 and NV21 if not in these ranges,
     *                     the default as NV21 processing
     * @param image_width 图片宽度 确保偶数
     *                    image width, make sure to be even
     * @param image_height 图片高度 确保偶数
     *                     image height, make sure to be even
     */
    public static   native void RGBA2YUV(byte[]data, byte[]dst, int pixel_format, int image_width, int image_height);

}
