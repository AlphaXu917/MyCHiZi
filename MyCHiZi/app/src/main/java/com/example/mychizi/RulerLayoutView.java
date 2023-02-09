package com.example.mychizi;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * @Author：Danny
 * @Time： 2023/1/30 17:50
 * @Description
 */
public class RulerLayoutView extends FrameLayout {
    private Context context;
    private RuleView mRuleView;
    private PaintView mPaintView;
    private LineView lineView;
    private PointF pointLT, pointRT, pointLB, pointRB;

    public RulerLayoutView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public RulerLayoutView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public RulerLayoutView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     * 初始化View
     *
     * @param context
     */
    private void initView(Context context) {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.view_ruler_layout, this);
        mRuleView = findViewById(R.id.mRuleView);
        mPaintView = findViewById(R.id.mPaintView);
        mRuleView.setInterfaceView(new RuleView.InterfaceView() {
            @Override
            public void onClose() {
                //            setVisibility(GONE);
                //TODO 测试旋转
                float angle = mRuleView.getCurrAngle() + 10;
                mRuleView.setCurrAngle(angle);
                mRuleView.setRotation(angle);
                mRuleView.invalidate();
                //测试旋转后点位置
                lineView.setPoint(
                        pointLT.x, pointLT.y,//左上
                        pointRB.x, pointRB.y,//右下
                        pointRT.x, pointRT.y,//右上
                        pointLB.x, pointLB.y//左下
                );
                lineView.invalidate();
            }

            @Override
            public void onDrawLine(boolean isTop, MotionEvent ev) {
                int[] location = new int[2];
                lineView.getLocationOnScreen(location);
                int x = location[0];//获取当前位置的横坐标
                int y = location[1];//获取当前位置的纵坐标
                float evX = ev.getRawX() - x;
                float evY = ev.getRawY() - y;
                float lineY;
                float lineX;
                float k;
                float b;
                switch (ev.getAction()) {
                    //按下
                    case MotionEvent.ACTION_DOWN:
                        /**
                         * 尺子上方
                         */
                        // (2,1) (3,2)
                        // 斜率: k = (x2-x)/(y2-y)，代入(2,1) (3,2)
                        // y = (3-2)/(2-1)x + b
                        // y=1x+b
                        // 截距: b = y - x，代入(2,1)
                        // b=1-2=-1
                        // 公式:y=x- 1
                        //y = kx+b，所以b=kx-y，把斜率和第一个点代进去    你算出来y=kx+b，代入x算出y，还是代入y算出x，
                        if (mRuleView.getCurrAngle() < 90) {
                            //小于90度
//                        linX = ev.getX();
//                        float k = (pointRT.y - pointLT.y) / (pointRT.x - pointLT.x);
//                        linY = k * ev.getX();
//                        float b = pointLT.y - k * pointLT.x;
//                        linY = linY + b;
                            lineX = evX;
                            if (isTop) {
                                k = (pointRT.y - pointLT.y) / (pointRT.x - pointLT.x);
                                b = pointLT.y - k * pointLT.x;
                            } else {
                                k = (pointRB.y - pointLB.y) / (pointRB.x - pointLB.x);
                                b = pointLB.y - k * pointLB.x;
                            }
                            lineY = k * evX;
                            lineY = lineY + b;
                            //y=kx+b，k和b都知道了，代入y就算出x，代入x就算出y
                            float lineLeftX = (evY - b) / k;
//                        float lineY2 = k * ev.getX() + b;
                            PointF pointEv = new PointF(evX, evY);
                            PointF pointLeft = new PointF(lineLeftX, evY);
                            double distance1 = getDistance(pointEv, pointLeft);

                            PointF pointBottom = new PointF(evX, lineY);
                            double distance2 = getDistance(pointEv, pointBottom);
                            if (distance1 > distance2) {
                                Log.d("GGGGG", "用lineY");
                            } else {
                                lineX = pointLeft.x;
                                lineY = evY;
                                Log.d("GGGGG", "用pointLeft");
                            }
                            Log.d("GGGGG", distance1 + "-----" + distance2);

                        } else if (mRuleView.getCurrAngle() == 90) {
                            //等于90度
                            lineX = mRuleView.getX() - mRuleView.getGravity();
                            lineY = evY;
                        } else {
                            //大于90度 反过来
                            lineX = evX;
                            if (isTop) {
                                //尺子上方
                                k = (pointLT.y - pointRT.y) / (pointLT.x - pointRT.x);
                                b = pointLT.y - k * pointLT.x;
                            } else {
                                //尺子下方
                                k = (pointLB.y - pointRB.y) / (pointLB.x - pointRB.x);
                                b = pointLB.y - k * pointLB.x;
                            }
                            lineY = k * evX;

                            lineY = lineY + b;
                            float lineLeftX = (evY - b) / k;
                            PointF pointEv = new PointF(evX, evY);
                            PointF pointLeft = new PointF(lineLeftX, evY);
                            double distance1 = getDistance(pointEv, pointLeft);
                            PointF pointBottom = new PointF(evX, lineY);
                            double distance2 = getDistance(pointEv, pointBottom);
                            if (distance1 > distance2) {
                                Log.d("GGGGG", "用lineY");
                            } else {
                                lineX = pointLeft.x;
                                lineY = evY;
                                Log.d("GGGGG", "用pointLeft");
                            }
                        }
                        mPaintView.getPath().moveTo(lineX, lineY);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // 画线
                        if (mRuleView.getCurrAngle() < 90) {
                            //小于90度
//                        float k = (pointRT.y - pointLT.y) / (pointRT.x - pointLT.x);
//                        linY = k * ev.getX();
//                        float b = pointLT.y - k * pointLT.x;
//                        linY = linY + b;
//                        linX = ev.getX();
                            lineX = evX;
                            if (isTop) {
                                //尺子上方
                                k = (pointRT.y - pointLT.y) / (pointRT.x - pointLT.x);
                                b = pointLT.y - k * pointLT.x;
                            } else {
                                //尺子下方
                                k = (pointRB.y - pointLB.y) / (pointRB.x - pointLB.x);
                                b = pointLB.y - k * pointLB.x;
                            }
                            lineY = k * evX;
                            lineY = lineY + b;
                            //y=kx+b，k和b都知道了，代入y就算出x，代入x就算出y
                            float lineLeftX = (evY - b) / k;
                            PointF pointEv = new PointF(evX, evY);
                            PointF pointLeft = new PointF(lineLeftX, evY);
                            double distance1 = getDistance(pointEv, pointLeft);

                            PointF pointBottom = new PointF(evX, lineY);
                            double distance2 = getDistance(pointEv, pointBottom);
                            if (distance1 > distance2) {
                                Log.d("GGGGG", "用lineY");
                            } else {
                                lineX = pointLeft.x;
                                lineY = evY;
                                Log.d("GGGGG", "用pointLeft");
                            }

                        } else if (mRuleView.getCurrAngle() == 90) {
                            //等于90度
                            lineX = mRuleView.getX() - mRuleView.getGravity();
                            lineY = evY;
                        } else {
                            //大于90度 反过来
                            lineX = evX;
                            if (isTop) {
                                //尺子上方
                                k = (pointLT.y - pointRT.y) / (pointLT.x - pointRT.x);
                                b = pointLT.y - k * pointLT.x;
                            } else {
                                //尺子下方
                                k = (pointLB.y - pointRB.y) / (pointLB.x - pointRB.x);
                                b = pointLB.y - k * pointLB.x;
                            }
                            lineY = k * evX;
                            lineY = lineY + b;

                            float lineLeftX = (evY - b) / k;
                            PointF pointEv = new PointF(evX, evY);
                            PointF pointLeft = new PointF(lineLeftX, evY);
                            double distance1 = getDistance(pointEv, pointLeft);
                            PointF pointBottom = new PointF(evX, lineY);
                            double distance2 = getDistance(pointEv, pointBottom);
                            if (distance1 > distance2) {
                                Log.d("GGGGG", "用lineY");
                            } else {
                                lineX = pointLeft.x;
                                lineY = evY;
                                Log.d("GGGGG", "用pointLeft");
                            }
                        }
                        mPaintView.getPath().lineTo(lineX, lineY);
                        mPaintView.invalidate();
                        break;
                }


            }
        });


        //TODO 测试区域
        lineView = new LineView(context);
        addView(lineView);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        updateRulerCoordinate();
        return super.dispatchTouchEvent(ev);
    }


    /**
     * 计算距离
     *
     * @param p1
     * @param p2
     * @return
     */
    private double getDistance(PointF p1, PointF p2) {
        return Math.sqrt(Math.abs((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y)));
    }


    /**
     * 更新尺子坐标
     */
    private void updateRulerCoordinate() {
        float x0 = mRuleView.getX();
        float y0 = mRuleView.getY() + mRuleView.getGravity();
        float x1 = mRuleView.getX() + mRuleView.getWidth();
        float y2 = y0 + (mRuleView.getRulerRect().bottom - mRuleView.getRulerRect().top);
        RectF rectF = new RectF(
                x0,
                y0,
                x1,
                y2);
        Matrix matrix = new Matrix();
        matrix.setRotate(mRuleView.getCurrAngle(), mRuleView.getX(), mRuleView.getY());

        pointLT = Util.getMapPoints(matrix, rectF.left, rectF.top);
        pointRT = Util.getMapPoints(matrix, rectF.right, rectF.bottom - mRuleView.getRulerHeight());
        pointLB = Util.getMapPoints(matrix, rectF.left, rectF.top + mRuleView.getRulerHeight());
        pointRB = Util.getMapPoints(matrix, rectF.right, rectF.bottom);

        //TODO 点测试区域
        lineView.setPoint(
                pointLT.x, pointLT.y,//左上
                pointRB.x, pointRB.y,//右下
                pointRT.x, pointRT.y,//右上
                pointLB.x, pointLB.y//左下
        );
    }


}
