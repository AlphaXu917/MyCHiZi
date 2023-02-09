package com.example.mychizi;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * @Author：Danny
 * @Time： 2023/2/2 16:00
 * @Description
 */
public class PaintView extends View {
    private Paint mPaint;
    private Path mPath;
    private Bitmap mBitmap;
    private Canvas mCanvas;

    public PaintView(Context context) {
        super(context);
        init(context);
    }

    public PaintView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PaintView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

    }


    private void init(Context context) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true); // 去除锯齿
        mPaint.setStrokeWidth(3);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.parseColor("#FF333333"));
        mPath = new Path();
        mBitmap = Bitmap.createBitmap(Util.getScreenWidth(context), Util.getScreenHeight(context), Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mCanvas.drawColor(Color.TRANSPARENT);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, 0, 0, null);
        canvas.drawPath(mPath, mPaint);
    }


    public Bitmap getPaintBitmap() {
        return resizeImage(mBitmap, 320, 480);
    }

    public Path getPath() {
        return mPath;
    }

    // 缩放
    public static Bitmap resizeImage(Bitmap bitmap, int width, int height) {
        int originWidth = bitmap.getWidth();
        int originHeight = bitmap.getHeight();
        float scaleWidth = ((float) width) / originWidth;
        float scaleHeight = ((float) height) / originHeight;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, originWidth,
                originHeight, matrix, true);
        return resizedBitmap;
    }

    /**
     * 清除画板
     */
    public void clear() {
        if (mCanvas != null) {
            mPath.reset();
            mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            invalidate();
        }
    }
}

