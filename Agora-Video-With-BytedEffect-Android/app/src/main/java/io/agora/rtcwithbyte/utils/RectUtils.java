package io.agora.rtcwithbyte.utils;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by yangcihang on 2018/2/22.
 */

public class RectUtils {
    /**
     * Transform the upright full rectangle so that it bounds the original rotated image,
     * given by the orientation. Transform the upright partial rectangle such that it would apply
     * to the same region of the transformed full rectangle.
     * <p>
     * The top-left of the transformed full rectangle will always be placed at (0, 0).
     *
     * @param orientation The exif orientation (0, 90, 180, 270) of the original image. The
     *                    transformed full and partial rectangles will be in this orientation's
     *                    coordinate space.
     * @param fullRect    The upright full rectangle. This rectangle will be modified.
     * @param partialRect The upright partial rectangle. This rectangle will be modified.
     */
    public static void rotateRectForOrientation(final int orientation, final Rect fullRect,
                                                final Rect partialRect) {
        final Matrix matrix = new Matrix();
        // Exif orientation specifies how the camera is rotated relative to the actual subject.
        // First rotate in the opposite direction.
        matrix.setRotate(-orientation);
        final RectF fullRectF = new RectF(fullRect);
        final RectF partialRectF = new RectF(partialRect);
        matrix.mapRect(fullRectF);
        matrix.mapRect(partialRectF);
        // Then translate so that the upper left corner of the rotated full rect is at (0,0).
        matrix.reset();
        matrix.setTranslate(-fullRectF.left, -fullRectF.top);
        matrix.mapRect(fullRectF);
        matrix.mapRect(partialRectF);
        // Orientation transformation is complete.
        fullRect.set((int) fullRectF.left, (int) fullRectF.top, (int) fullRectF.right,
                (int) fullRectF.bottom);
        partialRect.set((int) partialRectF.left, (int) partialRectF.top, (int) partialRectF.right,
                (int) partialRectF.bottom);
    }

    public static void rotateRect(final int degrees, final int px, final int py, final Rect rect) {
        final RectF rectF = new RectF(rect);
        final Matrix matrix = new Matrix();
        matrix.setRotate(degrees, px, py);
        matrix.mapRect(rectF);
        rect.set((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
    }
}