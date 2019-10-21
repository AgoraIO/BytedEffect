package io.agora.rtcwithbyte.renderer;

import android.graphics.Point;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.bytedance.labcv.effectsdk.BytedEffectConstants;

import java.nio.ByteBuffer;

import io.agora.rtcwithbyte.opengl.GlUtil;
import io.agora.rtcwithbyte.opengl.ProgramManager;


public class EffectRender {
    private volatile float[] mMVPMatrix = new float[16];

    protected int[] mFrameBuffers;
    protected int[] mFrameBufferTextures;
    protected int FRAME_BUFFER_NUM = 1;
    protected Point mFrameBufferShape;

    private ProgramManager mProgramManager;

    public EffectRender() {

    }

    private int mSurfaceWidth, mSurfaceHeight;


    public void setViewSize(int surfaceWidth, int surfaceHeight) {
        mSurfaceWidth = surfaceWidth;
        mSurfaceHeight = surfaceHeight;
    }


    public int prepareTexture(int width, int height) {
        initFrameBufferIfNeed(width, height);
        return mFrameBufferTextures[0];
    }

    private void initFrameBufferIfNeed(int width, int height) {
        boolean need = false;
        if (null == mFrameBufferShape || mFrameBufferShape.x != width || mFrameBufferShape.y != height) {
            need = true;
        }
        if (mFrameBuffers == null || mFrameBufferTextures == null) {
            need = true;
        }
        if (need) {
            destroyFrameBuffers();
            mFrameBuffers = new int[FRAME_BUFFER_NUM];
            mFrameBufferTextures = new int[FRAME_BUFFER_NUM];
            GLES20.glGenFramebuffers(FRAME_BUFFER_NUM, mFrameBuffers, 0);
            GLES20.glGenTextures(FRAME_BUFFER_NUM, mFrameBufferTextures, 0);
            for (int i = 0; i < FRAME_BUFFER_NUM; i++) {
                bindFrameBuffer(mFrameBufferTextures[i], mFrameBuffers[i], width, height);
            }
            mFrameBufferShape = new Point(width, height);
        }

    }

    public void destroyFrameBuffers() {
        if (mFrameBufferTextures != null) {
            GLES20.glDeleteTextures(FRAME_BUFFER_NUM, mFrameBufferTextures, 0);
            mFrameBufferTextures = null;
        }
        if (mFrameBuffers != null) {
            GLES20.glDeleteFramebuffers(FRAME_BUFFER_NUM, mFrameBuffers, 0);
            mFrameBuffers = null;
        }
    }

    /**
     * 纹理参数设置+buffer绑定
     * set texture params
     * and bind buffer
     */
    private void bindFrameBuffer(int textureId, int frameBuffer, int width, int height) {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, textureId, 0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }


    public void drawFrameOnScreen(int textureId, BytedEffectConstants.TextureFormat srcTetxureFormat, int textureWidth, int textureHeight, int rotation, boolean flipHorizontal, boolean flipVertical) {
        if (null == mProgramManager) {
            mProgramManager = new ProgramManager();
        }
        Matrix.setIdentityM(mMVPMatrix, 0);
        if (rotation % 180 == 90){
             GlUtil.getShowMatrix(mMVPMatrix,textureHeight, textureWidth, mSurfaceWidth, mSurfaceHeight);

        }else {
             GlUtil.getShowMatrix(mMVPMatrix, textureWidth, textureHeight, mSurfaceWidth, mSurfaceHeight);

        }
        GlUtil.flip(mMVPMatrix, flipHorizontal, flipVertical);

        GlUtil.rotate(mMVPMatrix, rotation);

        mProgramManager.getProgram(srcTetxureFormat).drawFrameOnScreen(textureId, mSurfaceWidth, mSurfaceHeight, mMVPMatrix);

    }

    public int drawFrameOffScreen(int srcTextureId, BytedEffectConstants.TextureFormat srcTetxureFormat, int dstWidth, int dstHeight, int cameraRotation, boolean flipHorizontal, boolean flipVertical) {
        if (null == mProgramManager) {
            mProgramManager = new ProgramManager();
        }
        Matrix.setIdentityM(mMVPMatrix, 0);
        GlUtil.flip(mMVPMatrix, flipHorizontal, flipVertical);

        GlUtil.rotate(mMVPMatrix, cameraRotation);

        return mProgramManager.getProgram(srcTetxureFormat).drawFrameOffScreen(srcTextureId, dstWidth, dstHeight, mMVPMatrix);
    }


    public void release(){
        destroyFrameBuffers();
        if (null != mProgramManager){
            mProgramManager.release();
        }



    }

    public ByteBuffer captureRenderResult(int imageWidth, int imageHeight){
        int textureId = mFrameBufferTextures[0];
        if (null == mFrameBufferTextures || textureId == GlUtil.NO_TEXTURE) {
            return null;
        }
        if (imageWidth* imageHeight == 0){
            return  null;
        }
        ByteBuffer mCaptureBuffer = ByteBuffer.allocateDirect(imageWidth* imageHeight*4);

        mCaptureBuffer.position(0);
        int[] frameBuffer = new int[1];
        GLES20.glGenFramebuffers(1,frameBuffer,0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, textureId, 0);
        GLES20.glReadPixels(0, 0, imageWidth, imageHeight,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, mCaptureBuffer);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        if (null != frameBuffer) {
            GLES20.glDeleteFramebuffers(1, frameBuffer, 0);
        }
        return mCaptureBuffer;
    }

}













