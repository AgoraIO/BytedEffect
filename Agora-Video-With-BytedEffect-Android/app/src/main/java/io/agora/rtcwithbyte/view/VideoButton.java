package io.agora.rtcwithbyte.view;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import io.agora.rtcwithbyte.R;
import io.agora.rtcwithbyte.utils.DensityUtils;

public class VideoButton extends View {
    public static final float STEP_PROGRESS = 0.05F;
    public static final float START_END_RATIO = 0.35F;
    public static final int PADDING = 3;
    public static final float RECTANGLE_ROUND = 10;
    public static final int DEFAULT_START_COLOR = Color.parseColor("#66FFFFFF");
    public static final int DEFAULT_END_COLOR = Color.parseColor("#88FF0000");

    private int mTotalLength;
    private int mRectangleLength;
    private int mCircleLength;
    private float mProgress = 1F;

    private int mStartColor;
    private int mEndColor;

    private Paint mPaint;
    private ArgbEvaluator mArgbEvaluator;
    private RectF mRect;

    private boolean isStarting;

    public VideoButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);

        mArgbEvaluator = new ArgbEvaluator();
        mRect = new RectF();

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.VideoButton);
        mStartColor = ta.getColor(R.styleable.VideoButton_startColor, DEFAULT_START_COLOR);
        mEndColor = ta.getColor(R.styleable.VideoButton_endColor, DEFAULT_END_COLOR);
        ta.recycle();
    }

    private void initSize(int length) {
        mTotalLength = length;
        int padding = (int) DensityUtils.dp2px(getContext(), PADDING);
        mCircleLength = length - padding * 2;
        mRectangleLength = (int) (mCircleLength * START_END_RATIO);
    }

    public void start() {
        mProgress = 1F;
        isStarting = true;
        postInvalidate();
    }

    public void stop() {
        mProgress = 0F;
        isStarting = false;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mProgress += isStarting ? -STEP_PROGRESS : STEP_PROGRESS;
        float clippedProgress = clipProgress(mProgress);

        drawWithProgress(canvas, clippedProgress);

        if (clippedProgress < 1F || clippedProgress > 0F) {
            postInvalidate();
        }
    }

    private void drawWithProgress(Canvas canvas, float progress) {
        int color = (int) mArgbEvaluator.evaluate(progress, mEndColor, mStartColor);
        mPaint.setColor(color);

        int center = mTotalLength / 2;
        int length = (int) (mRectangleLength + (mCircleLength - mRectangleLength) * progress) / 2;
        float round = RECTANGLE_ROUND + (mCircleLength / 2F - RECTANGLE_ROUND) * progress;
        mRect.set(center - length, center - length, center + length, center + length);
        canvas.drawRoundRect(mRect, round, round, mPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (h < w) {
            w = h;
        }
        initSize(w);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private float clipProgress(float progress) {
        if (progress > 1) progress = 1;
        if (progress < 0) progress = 0;
        return progress;
    }
}
