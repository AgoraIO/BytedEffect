//package io.agora.rtcwithfu.utils;
//
//
//import com.google.zxing.Result;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//
//public class StickerCodeParser {
//
//    public static String parseId(Result result){
//        try{
//            if (null != result){
//                JSONObject jsonObject = new JSONObject(result.getText());
//                String idStr = jsonObject.optString("id","");
//                return idStr;
//            }
//
//
//        } catch (JSONException e){
//            e.printStackTrace();
//        }
//        return "";
//
//    }
//}
