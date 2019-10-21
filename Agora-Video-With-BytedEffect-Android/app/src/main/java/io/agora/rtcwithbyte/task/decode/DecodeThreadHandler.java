package io.agora.rtcwithbyte.task.decode;

import android.graphics.Rect;
import android.hardware.camera2.CameraDevice;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DecodeThreadHandler extends Handler {
    static final int DECODE = 1001;
    static final int SUCCEED = 1002;
    static final int FAIL = 1003;

    private static final String THREAD_NAME = "DECODE THREAD";
    private static final Set<BarcodeFormat> QR_CODE_FORMATS = EnumSet.of(BarcodeFormat.QR_CODE);
    private final Map<DecodeHintType,Object> HINTS = new EnumMap<>(DecodeHintType.class);

    private HandlerThread mHandlerThread;
    private boolean isRunning = false;
    private MultiFormatReader mMultiFormatReader;

    private DecodeThreadHandler(HandlerThread thread) {
        super(thread.getLooper());
        this.mHandlerThread = thread;

        initReader();
    }

    private void initReader() {
        mMultiFormatReader = new MultiFormatReader();
        List<BarcodeFormat> decodeFormats = new ArrayList<>(QR_CODE_FORMATS);
        HINTS.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
        HINTS.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, null);
        mMultiFormatReader.setHints(HINTS);
    }

    void resume() {
        isRunning = true;
    }

    void pause() {
        isRunning = false;
        removeCallbacksAndMessages(null);
    }

    void quit() {
        mHandlerThread.quit();
    }

//    @Override
//    public void handleMessage(Message msg) {
//        if (!isRunning || null == mMultiFormatReader) {
//            return;
//        }
//        ByteBuffer buffer = (ByteBuffer) msg.obj;
//        int width = msg.arg1;
//        int height = msg.arg2;
//        Result result = decode(buffer.array(), width, height);
//        Messenger messenger = msg.replyTo;
//        Message resultMsg = obtainMessage();
//        try {
//            if (null != result) {
//                resultMsg.what = SUCCEED;
//                resultMsg.obj = result;
//            } else {
//                resultMsg.what = FAIL;
//            }
//            messenger.send(resultMsg);
//        } catch (RemoteException ex) {
//            ex.printStackTrace();
//        }
//
//    }

    /**
     * 解码逻辑
     */
//    private Result decode(byte[] bytes, int width, int height) {
//        Result rawResult = null;
//        RGBLuminanceSource source = new RGBLuminanceSource(width, height, byte2Int(bytes));
//        Rect frameRect = CameraDevice.get().getFrameRect(width, height);
//        if (source != null && null != frameRect) {
//            int left = frameRect.left > source.getWidth() ? 0 : frameRect.left;
//            int top = frameRect.top > source.getHeight() ? 0 : frameRect.top;
//            int right = left +  frameRect.width()> source.getWidth()?source.getWidth():left +  frameRect.width();
//            int bottom = top + frameRect.height()> source.getHeight()?source.getHeight():top+frameRect.height();
//
//            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source.crop(left, top, right - left, bottom -top)));
//            try {
//                rawResult = mMultiFormatReader.decode(bitmap);
//            } catch (ReaderException re) {
//                // continue
//                re.printStackTrace();
//            } finally {
//                mMultiFormatReader.reset();
//            }
//        }
//        return rawResult;
//    }

    private int[] byte2Int(byte[] b){
        // 数组长度对4余数
        int r;
        byte[] copy;
        if ((r = b.length % 4) != 0) {
            copy = new byte[b.length - r + 4];
            System.arraycopy(b, 0, copy, 0, b.length);
        } else {
            copy = b;
        }
        int[] x = new int[copy.length /4 + 1];
        int pos = 0;
        for (int i = 0; i < x.length - 1; i ++) {
            x[i] = (copy[pos] << 24 & 0xff000000) | (copy[pos+1] << 16 & 0xff0000) | (copy[pos+2] << 8 & 0xff00) | (copy[pos+3] & 0xff);
            pos += 4;
        }
        x[x.length - 1] = r;
        return x;
    }

    static DecodeThreadHandler createThreadHandler() {
        HandlerThread thread = new HandlerThread(THREAD_NAME);
        thread.start();
        return new DecodeThreadHandler(thread);
    }
}
