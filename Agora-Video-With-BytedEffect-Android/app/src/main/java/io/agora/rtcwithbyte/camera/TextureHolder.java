package io.agora.rtcwithbyte.camera;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;

import io.agora.rtcwithbyte.opengl.GlUtil;


public class TextureHolder {
    protected int mSurfaceTextureID = GlUtil.NO_TEXTURE;
    protected SurfaceTexture mSurfaceTexture;
    private float mMPV[] = new float[16];

    public void onCreate(final SurfaceTexture.OnFrameAvailableListener onFrameAvailableListener) {
        if (mSurfaceTextureID == GlUtil.NO_TEXTURE) {
            mSurfaceTextureID = GlUtil.getExternalOESTextureID();
            mSurfaceTexture = new SurfaceTexture(mSurfaceTextureID);
            mSurfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                @Override
                public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                    surfaceTexture.getTransformMatrix(mMPV);
                    if (onFrameAvailableListener != null) {
                        onFrameAvailableListener.onFrameAvailable(surfaceTexture);
                    }
                }
            });
        }
    }

    public void onDestroy() {
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }

        if (mSurfaceTextureID != GlUtil.NO_TEXTURE) {
            GLES20.glDeleteTextures(1, new int[]{mSurfaceTextureID}, 0);
        }
        mSurfaceTextureID = GlUtil.NO_TEXTURE;
    }

    public void updateTexImage() {
        if (mSurfaceTexture != null)
            mSurfaceTexture.updateTexImage();
    }

    public float[] getmMPV() {
        return mMPV;
    }

    public SurfaceTexture getSurfaceTexture() {
        return mSurfaceTexture;
    }

    public int getmSurfaceTextureID() {
        return mSurfaceTextureID;
    }
}
