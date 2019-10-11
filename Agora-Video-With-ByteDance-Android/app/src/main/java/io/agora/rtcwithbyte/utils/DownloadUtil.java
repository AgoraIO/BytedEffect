//package io.agora.rtcwithfu.utils;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//
////import okhttp3.Call;
////import okhttp3.Callback;
////import okhttp3.OkHttpClient;
////import okhttp3.Request;
////import okhttp3.Response;
//
///**
// * 文件下载工具
// */
//public class DownloadUtil {
//    public static final String HOST = "http://cv-tob.bytedance.com/download_effect?deviceId=";
//
//    public interface DownloadListener {
//        void onDownloadSuccess(String dir, String path);
//
//        void onDownloading(int progress);
//
//        void onDownloadFail();
//
//    }
//
//    private static DownloadUtil downloadUtil = null;
//    private OkHttpClient okHttpClient;
//
//    public static DownloadUtil get() {
//        if (downloadUtil == null) {
//            downloadUtil = new DownloadUtil();
//        }
//        return downloadUtil;
//
//    }
//
//    private DownloadUtil() {
//        okHttpClient = new OkHttpClient();
//    }
//
//    public void download(final String id, final String savePath, final DownloadListener listener) {
//        try {
//
//            Request request = new Request.Builder().url(HOST + id).build();
//            okHttpClient.newCall(request).enqueue(new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    // 下载失败
//                    listener.onDownloadFail();
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    InputStream is = null;
//                    byte[] buf = new byte[2048];
//                    int len = 0;
//                    FileOutputStream fos = null;
//                    // 储存下载文件的目录
//                    try {
//                        is = response.body().byteStream();
//                        long total = response.body().contentLength();
//                        File file = new File(savePath, id + ".zip");
//                        fos = new FileOutputStream(file);
//                        long sum = 0;
//                        while ((len = is.read(buf)) != -1) {
//                            fos.write(buf, 0, len);
//                            sum += len;
//                            int progress = (int) (sum * 1.0f / total * 100);
//                            // 下载中
//                            if (progress < 0) {
//                                // 当下载的数据没有长度信息时(total = -1)，模拟一个假的进度
//                                progress = (int) (sum / 1024);
//                            }
//                            listener.onDownloading(progress);
//                        }
//                        fos.flush();
//                        // 下载完成
//                        listener.onDownloadSuccess(id, file.getAbsolutePath());
//                    } catch (Exception e) {
//                        listener.onDownloadFail();
//                    } finally {
//                        try {
//                            if (is != null)
//                                is.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        try {
//                            if (fos != null)
//                                fos.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            });
//
//        } catch (Exception e) {
//            listener.onDownloadFail();
//        }
//
//    }
//}
//
