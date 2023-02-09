package com.example.mychizi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * @Author：Danny
 * @Time： 2023/2/2 14:45
 * @Description
 */
public class LineView extends View {
    private Paint paint;
    private float pointLTx, pointLTy, pointRTx, pointRTy, pointLBx, pointLBy, pointRBx, pointRBy;

    public LineView(Context context) {
        super(context);
        init();
    }


    public LineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(10);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
    }


    @SuppressLint({"DrawAllocation", "Range"})
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.drawPoints(new float[]{
//                pointLTx, pointLTy,//左上
//                pointRBx, pointRBy,//右下
//                pointLBx, pointLBy,//左下
//                pointRTx, pointRTy//右上
//        }, paint);

    }


    public void setPoint(float pointLTx, float pointLTy, float pointRBx, float pointRBy, float pointRTx, float pointRTy, float pointLBx, float pointLBy) {
        this.pointLTx = pointLTx;
        this.pointLTy = pointLTy;
        this.pointRBx = pointRBx;
        this.pointRBy = pointRBy;
        this.pointRTx = pointRTx;
        this.pointRTy = pointRTy;
        this.pointLBx = pointLBx;
        this.pointLBy = pointLBy;
        invalidate();
    }
}
