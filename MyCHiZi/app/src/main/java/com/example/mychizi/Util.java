package com.example.mychizi;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.WindowManager;

/**
 * @Author：Danny
 * @Time： 2023/1/30 10:38
 * @Description
 */


public class Util {


    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        return width;
    }

    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        return height;
    }


    /**
     * @param x
     * @param y
     * @param rectF
     * @param Angle
     * @return
     */
    public static boolean clickHelpBoxRect(float x, float y, RectF rectF, float Angle) {
        if (rectF == null || rectF.isEmpty()) {
            return false;
        }
        boolean isClickHelpBoxRect;
        if (Angle != 0) {
            // 旋转后判断点是否在四边形内
            Matrix matrix = new Matrix();
            matrix.setRotate(Angle, rectF.centerX(), rectF.centerY());
            PointF curPoint = new PointF(x, y);

            PointF pointLT = getMapPoints(matrix, rectF.left, rectF.top);
            PointF pointRT = getMapPoints(matrix, rectF.right, rectF.top);
            PointF pointLB = getMapPoints(matrix, rectF.left, rectF.bottom);
            PointF pointRB = getMapPoints(matrix, rectF.right, rectF.bottom);
            isClickHelpBoxRect = pointInRect(curPoint, pointLT, pointRT, pointLB, pointRB);
        } else {
            isClickHelpBoxRect = rectF.contains(x, y);
        }
        return isClickHelpBoxRect;
    }


    public static boolean clickHelpBoxRect(float x, float y, float Angle, PointF pointLT, PointF pointRT, PointF pointLB, PointF pointRB, RectF rectF) {
        if (rectF == null || rectF.isEmpty()) {
            return false;
        }
        boolean isClickHelpBoxRect;
        if (Angle != 0) {
            // 旋转后判断点是否在四边形内
            PointF curPoint = new PointF(x, y);
            isClickHelpBoxRect = pointInRect(curPoint, pointLT, pointRT, pointLB, pointRB);
        } else {
            isClickHelpBoxRect = rectF.contains(x, y);
        }
        return isClickHelpBoxRect;
    }

    /**
     * 获取旋转后的坐标点
     *
     * @param matrix 转换矩阵
     * @param x      左坐标
     * @param y      右坐标
     * @return
     */
    public static PointF getMapPoints(Matrix matrix, float x, float y) {
        float[] floats = new float[]{x, y};
        matrix.mapPoints(floats);
        return new PointF(floats[0], floats[1]);
    }

    /**
     * 点是否落在四边形内
     *
     * @param curPoint 点击位置
     * @param pointLT  左上顶点
     * @param pointRT  右上顶点
     * @param pointLB  左下顶点
     * @param pointRB  右下顶点
     * @return true 在四边形内；false 不在四边形内
     */
    public static boolean pointInRect(PointF curPoint, PointF pointLT, PointF pointRT, PointF pointLB, PointF pointRB) {
        int nCount = 4;
        PointF[] rectPoints = new PointF[]{pointLT, pointLB, pointRB, pointRT};
        int nCross = 0;
        for (int i = 0; i < nCount; i++) {
            //依次取相邻的两个点
            PointF pStart = rectPoints[i];
            PointF pEnd = rectPoints[(i + 1) % nCount];

            //相邻的两个点是平行于x轴的，肯定不相交，忽略
            if (pStart.y == pEnd.y)
                continue;

            //交点在pStart,pEnd的延长线上，pCur肯定不会与pStart.pEnd相交，忽略
            if (curPoint.y < Math.min(pStart.y, pEnd.y) || curPoint.y > Math.max(pStart.y, pEnd.y))
                continue;

            //求当前点和x轴的平行线与pStart,pEnd直线的交点的x坐标
            double x = (double) (curPoint.y - pStart.y) * (double) (pEnd.x - pStart.x) / (double) (pEnd.y - pStart.y) + pStart.x;

            //若x坐标大于当前点的坐标，则有交点
            if (x > curPoint.x)
                nCross++;
        }

        // 单边交点为偶数，点在多边形之外
        return (nCross % 2 == 1);
    }
}