// Copyright (C) 2018 Beijing Bytedance Network Technology Co., Ltd.
package io.agora.rtcwithbyte;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import java.io.File;

public class ResourceHelper {

    public static final String ResourceZip = "resource.zip";
    public static final String RESOURCE = "";//"resource";
    public static final String FACE = "ModelResource.bundle/ttfacemodel/tt_face_v6.0.model";
    public static final String PETFACE = "ModelResource.bundle/ttpetface/tt_petface_v2.4.model";
    public static final String DetectParamFile = "ModelResource.bundle/handmodel/tt_hand_det_v7.0.model";
    public static final String BoxRegParamFile = "ModelResource.bundle/handmodel/tt_hand_box_reg_v8.0.model";
    public static final String GestureParamFile = "ModelResource.bundle/handmodel/tt_hand_gesture_v8.0.model";
    public static final String KeyPointParamFile = "ModelResource.bundle/handmodel/tt_hand_kp_v5.0.model";
    public static final String SegParamFile = "ModelResource.bundle/handmodel/tt_hand_seg_v1.0.model";
    public static final String BEAUTY_RESOURCE = "BeautyResource.bundle";
    public static final String FILTER_RESOURCE = "FilterResource.bundle/Filter";
    public static final String RESHAPE_RESOURCE = "ReshapeResource.bundle";
    public static final String MAKEUP_RESOURCE = "BuildinMakeup.bundle";
    public static final String COMPOSE_MAKEUP_RESOURCE = "ComposeMakeup";

    public static final String STICKER_RESOURCE = "stickers";
    private static final String FACEEXTA = "ModelResource.bundle/ttfacemodel/tt_face_extra_v9.0.model";
    private static final String FACEATTRI = "ModelResource.bundle/ttfaceattri/tt_face_attribute_v4.1.model";
    private static final String FACEVERIFY = "ModelResource.bundle/ttfaceverify/tt_faceverify_v5.0.model";
    private static final String SKELETON = "ModelResource.bundle/skeleton_model/tt_skeleton_v6.0.model";
    private static final String PORTRAITMATTING = "ModelResource.bundle/mattingmodel/tt_matting_v9.0.model";
    private static final String HAIRPARSING = "ModelResource.bundle/hairparser/tt_hair_v7.0.model";

    private static final String LICENSE_NAME = "labcv_test_20190920_20191022_com.bytedance.labcv.demo_labcv_test_v3.0.0.licbag";

    private static String getResourcePath(Context context) {
        return context.getExternalFilesDir("assets").getAbsolutePath() + File.separator + RESOURCE;
    }

    public static String getModelDir(@NonNull final Context context) {
        File file = new File(new File(getResourcePath(context), "ModelResource.bundle"), "");
        return file.getAbsolutePath();
    }

    public static String getFaceModelPath(@NonNull final Context context) {
        File file = new File(getResourcePath(context), FACE);
        return file.getAbsolutePath();
    }

    public static String getPetFaceModelPath(@NonNull final Context context) {
        File file = new File(new File(getResourcePath(context), PETFACE), "");
        return file.getAbsolutePath();
    }

    public static String getFaceExtaModelPath(@NonNull final Context context) {
        File file = new File(new File(getResourcePath(context), FACEEXTA), "");
        return file.getAbsolutePath();
    }

    public static String getFaceAttriModelPath(@NonNull final Context context) {
        File file = new File(new File(getResourcePath(context), FACEATTRI), "");
        return file.getAbsolutePath();
    }

    public static String getFaceVerifyModelPath(final Context context) {
        File file = new File(new File(getResourcePath(context), FACEVERIFY), "");
        return file.getAbsolutePath();
    }

    public static String getSkeletonModelPath(@NonNull final Context context) {
        File file = new File(new File(getResourcePath(context), SKELETON), "");
        return file.getAbsolutePath();
    }

    public static String getPortraitmattingModelPath(@NonNull final Context context)
    {
        File file = new File(new File(getResourcePath(context), PORTRAITMATTING), "");
        return file.getAbsolutePath();
    }

    public static String getHairParsingModelPath(@NonNull final Context context)
    {
        File file = new File(new File(getResourcePath(context), HAIRPARSING), "");
        return file.getAbsolutePath();
    }

    public static String getHandModelPath(@NonNull final Context context,String path) {
        File file = new File(new File(getResourcePath(context), path), "");
        return file.getAbsolutePath();
    }

    public static String getLicensePath(@NonNull final Context context) {
        File file = new File(new File(getResourcePath(context), "LicenseBag.bundle"), LICENSE_NAME);
        return file.getAbsolutePath();
    }

    public static String getStickersPath(@NonNull final Context context) {
        File file = new File(new File(getResourcePath(context), "StickerResource.bundle"), "stickers");
        return file.getAbsolutePath();
    }

    public static String getComposeMakeupComposerPath(@NonNull final Context context) {
        return getResourcePath(context) + File.separator + "ComposeMakeup.bundle" + File.separator + "ComposeMakeup/composer";
    }

    public static String getComposePath(@NonNull final Context context) {
        return getResourcePath(context) + File.separator + "ComposeMakeup.bundle"  + File.separator + "ComposeMakeup/";
    }


    public static File[] getFilterResources(@NonNull final Context context) {
        return getResources(context, FILTER_RESOURCE);
    }


    public static File[] getResources(@NonNull final Context context, String type) {
        File file = new File(new File(getResourcePath(context), type), "");
        if (file.exists() && file.isDirectory())
            return file.listFiles();
        return new File[0];
    }

    public static String getStickerPath(@NonNull final Context context,String name) {
        return getStickersPath(context) + File.separator + name;

    }

    public static boolean isResourceReady(@NonNull final Context context, int versionCode) {

        SharedPreferences preferences=context.getSharedPreferences("user", Context.MODE_PRIVATE);
       boolean resourceReady = preferences.getBoolean("resource", false);
        int preVersioncode = preferences.getInt("versionCode", 0);

        // 如果上次已经拷贝过 继续检查版本号
        // Continue to check the version number if it was copied last time
       if (resourceReady && versionCode == preVersioncode){
           return true;
       }
       return false;
    }

    public static void setResourceReady(@NonNull final Context context, boolean isReady, int versionCode){
        SharedPreferences preferences=context.getSharedPreferences("user", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("resource", isReady);
        editor.putInt("versionCode", versionCode);
        editor.commit();
    }

    public static String getDownloadedStickerDir(@NonNull final Context context){
        File file = new File(new File(getResourcePath(context), "download"), "sticker");
        if (!file.exists()) {
            file.mkdirs();

        }
        return file.getAbsolutePath();


    }




}
