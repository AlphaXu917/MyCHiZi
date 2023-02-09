package com.example.mychizi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;


import java.text.DecimalFormat;

/**
 * @Author：ZJW
 * @Time： 2023/1/30 10:37
 * @Description
 */
public class RuleView extends View {

    private Paint paint;

    private final Context context;

    private static final int MAX_VALUE = 20000;
    /**
     * 起点x的坐标
     */
    private final float startX = 0;

    /**
     * 刻度线的长度
     */
    private float yLength;
    /**
     * 刻度的间隙
     */
    private float gap = 8f;
    /**
     * 文本的间隙
     */
    private float textGap = 10f;
    /**
     * 短竖线的高度
     */
    private float smallHeight = 4f;
    /**
     * 长竖线的高度
     */
    private float largeHeight = 7f;

    /**
     * 文本显示格式化
     */
    private DecimalFormat format;

    private DisplayMetrics metrics = null;
    /**
     * 文本的字体大小
     */
    private float mFontSize;
    /**
     * 刻度进制
     */
    private final float unit = 10f;//代表多少个刻度绘制一次文字

    /**
     * 控件高度
     */
    private float height = 150;
    /**
     * 刻度文字间隔
     */
    private final float txtInterval = 9f;

    /**
     * 关闭bitmap
     */
    private Bitmap closeBitmap;
    /**
     * 交互变量
     */
    private boolean isMove;//移动状态
    private float mOriginalX;
    private float mOriginalY;
    private float mOriginalRawX;
    private float mOriginalRawY;
    private float currAngle;//当前旋转角度
    private RectF closeRectF;//关闭区域
    private InterfaceView interfaceView;//事件处理回调
    private RectF topLineRectF;//上方尺子画线区域
    private RectF bottomLineRectF;//下方尺子画线区域
    private float gravity = 100;//画线范围
    private RectF rulerRect;//尺子区域
    private boolean isDraw = false;

    public RuleView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public RuleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public RuleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    public float getGravity() {
        return gravity;
    }

    public boolean isMove() {
        return isMove;
    }

    public void setCurrAngle(float currAngle) {
        this.currAngle = currAngle;
    }

    public float getCurrAngle() {
        return currAngle;
    }

    public void setInterfaceView(InterfaceView interfaceView) {
        this.interfaceView = interfaceView;
    }

    public void init() {
        closeBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_ruler_close);
        format = new DecimalFormat("0");

        metrics = new DisplayMetrics();
        WindowManager wmg = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        wmg.getDefaultDisplay().getMetrics(metrics);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(getResources().getDimension(R.dimen.text_h2));
        paint.setColor(Color.parseColor("#999999"));
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);

        mFontSize = Util.dip2px(context, 13);
        smallHeight = Util.dip2px(context, 4);
        largeHeight = Util.dip2px(context, 7);
        yLength = Util.dip2px(context, 10);
        height = Util.dip2px(context, 150);
        gap = Util.dip2px(context, 3f);


        setPivotX(0);
        setPivotY(0);
    }

    public float getRulerHeight() {
        return height;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        //尺子区域
        rulerRect = new RectF(0, gravity, getWidth(), gravity + height);
        // 画顶部刻度线
        drawTopLine(canvas);
        //画中间旋转角度
        drawAngle(canvas);
        // 画底部部刻度线
        drawBottomLine(canvas);

        //绘制顶部线条区域边框
        paint.setColor(Color.parseColor("#BFBFBF"));
        topLineRectF = new RectF(0, -gravity, getWidth(), gravity);
//        canvas.drawRect(topLineRectF, paint); TODO 测试区域
        //绘制底部线条区域边框
        bottomLineRectF = new RectF(0, height + gravity, getWidth(), height + gravity + gravity);
//        canvas.drawRect(bottomLineRectF, paint); TODO 测试区域
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public RectF getRulerRect() {
        return rulerRect;
    }

    /**
     * 画顶部刻度线
     *
     * @param canvas
     */
    private void drawTopLine(Canvas canvas) {
        //绘制灰色的线
        paint.setColor(Color.parseColor("#BFBFBF"));
        canvas.drawLine(0, gravity, getWidth(), gravity, paint);
        float startY = 1 + gravity;
        //设置刻度线颜色
        paint.setColor(Color.BLACK);
        //绘制刻度
        int index = 0;
        for (int i = 0; i <= MAX_VALUE; i++) {

            if (i % 5 == 0) {
                index++;
                if (index % 2 != 0) {
                    yLength = Util.dip2px(context, largeHeight);
                } else {
                    yLength = Util.dip2px(context, smallHeight + 2);
                }
            } else {
                yLength = Util.dip2px(context, smallHeight);
            }
            canvas.drawLine(
                    i * gap + startX,
                    startY,
                    i * gap + startX,
                    yLength + startY, paint);
        }
        //设置刻度线文字大小
        paint.setTextSize(mFontSize);
        //设置刻度线文字颜色
        paint.setColor(Color.parseColor("#FF333333"));
        //每N个刻度写一个数字
        textGap = gap * unit;
        float y = 0;
        for (int i = 0; i <= MAX_VALUE / unit; i++) {
            String text = format.format(i) + "";

            // 获取文本的宽度
            float width = Util.px2dip(context, calculateTextWidth(text, mFontSize)) / 2f;
            if (i == 0) {
                y = Util.dip2px(context, largeHeight) + width * 2;
            }
            canvas.drawText(
                    text,
                    startX - width + i * textGap,
                    y + txtInterval + gravity
                    , paint);
        }
    }

    /**
     * 画角度 TODO 这个中间位置 需要动态换算
     *
     * @param canvas
     */
    private void drawAngle(Canvas canvas) {
        float centralPoint;
        if (getX() < 0) {
            centralPoint = Util.getScreenWidth(context) / 2f - getX();
        } else {
            centralPoint = (Util.getScreenWidth(context) - getX()) / 2;
        }
        float size = Util.dip2px(context, 24);
        Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        paint.setTextSize(size);
        //设置刻度线文字颜色
        mPaint.setColor(Color.parseColor("#FF333333"));
        String text = (int) Math.abs(currAngle) + "°";
        float txtWidth = Util.px2dip(context, calculateTextWidth(text, size)) / 2f;
        float txtHeight = Util.px2dip(context, calculateTextWidth("0", size)) / 2f;
        canvas.drawText(
                text,
                centralPoint - txtWidth,
                height / 2 + txtHeight + gravity
                , paint);
        //文字外部画圆
        float radius = Util.dip2px(context, size);
        canvas.drawCircle(centralPoint, height / 2f + gravity, radius, mPaint);
        //画叉
        int gap = Util.dip2px(context, 20);//与圆的距离
        float closeBitmapX = centralPoint + radius + gap;
        float closeBitmapY = height / 2 - closeBitmap.getHeight() / 2f + gravity;
        closeRectF = new RectF(
                closeBitmapX,
                closeBitmapY,
                closeBitmapX + closeBitmap.getWidth(),
                closeBitmapY + closeBitmap.getHeight()
        );
        canvas.drawBitmap(closeBitmap, closeBitmapX, closeBitmapY, paint);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (closeBitmap != null && !closeBitmap.isRecycled()) {
            closeBitmap.recycle();
            System.gc();
        }
    }

    /**
     * 画顶部刻度线
     *
     * @param canvas
     */
    private void drawBottomLine(Canvas canvas) {
        float startY = height;
        //绘制灰色的线
        paint.setColor(Color.parseColor("#BFBFBF"));
        canvas.drawLine(0, height + gravity, getWidth(), height + gravity, paint);
        //设置刻度线颜色
        paint.setColor(Color.BLACK);
        //绘制刻度
        int index = 0;
        for (int i = 0; i <= MAX_VALUE; i++) {
            if (i % 5 == 0) {
                index++;
                if (index % 2 != 0) {
                    yLength = Util.dip2px(context, largeHeight);
                } else {
                    yLength = Util.dip2px(context, smallHeight + 2);
                }
            } else {
                yLength = Util.dip2px(context, smallHeight);
            }
            canvas.drawLine(
                    i * gap + startX,
                    startY - yLength + gravity,
                    i * gap + startX,
                    height + gravity,
                    paint);
        }
        //设置刻度线文字大小
        paint.setTextSize(mFontSize);
        //设置刻度线文字颜色
        paint.setColor(Color.parseColor("#FF333333"));
        //每N个刻度写一个数字
        textGap = gap * unit;
        for (int i = 0; i <= MAX_VALUE / unit; i++) {
            String text = format.format(i) + "";
            // 获取文本的宽度
            float width = Util.px2dip(context, calculateTextWidth(text, mFontSize)) / 2f;
            canvas.drawText(
                    text,
                    startX - width + i * textGap,
                    startY - Util.dip2px(context, largeHeight) - txtInterval + gravity,
                    paint);
        }
    }


    /**
     * 获取TextView中文本的宽度
     *
     * @param text
     * @return
     */
    private float calculateTextWidth(String text, float size) {
        if (TextUtils.isEmpty(text)) {
            return 0;
        }
        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(size * metrics.scaledDensity);
        return textPaint.measureText(text);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.d("ZJW_LOG", "dispatchTouchEvent2");
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (closeRectF.contains(ev.getX(), ev.getY())) {
                    //关闭
                    isDraw = false;
                    if (interfaceView != null) {
                        interfaceView.onClose();
                        Log.d("ZJW_LOG", "ACTION_DOWN 点击了关闭");
                    }
                    return true;

                }
                if (topLineRectF.contains(ev.getX(), ev.getY())) {
                    //尺子上方画线
                    isDraw = true;
                    if (interfaceView != null) {
                        interfaceView.onDrawLine(true, ev);
                        Log.d("ZJW_LOG", "ACTION_DOWN 点击了尺子上方画线" + "坐标值---->" + ev.getRawX() + "," + ev.getRawY());
                    }
                    return true;

                }
                if (bottomLineRectF.contains(ev.getX(), ev.getY())) {
                    //尺子下方画线
                    isDraw = true;
                    if (interfaceView != null) {
                        interfaceView.onDrawLine(false, ev);
                        Log.d("ZJW_LOG", "ACTION_DOWN 点击了尺子下方画线------------" + "坐标值---->" + ev.getX() + "," + ev.getY());
                    }
                    return true;
                }
                if (rulerRect.contains(ev.getX(), ev.getY())) {
                    {
                        //移动
                        mOriginalX = getX();
                        mOriginalY = getY();
                        mOriginalRawX = ev.getRawX();
                        mOriginalRawY = ev.getRawY();
                        isDraw = false;
                        isMove = true;
                        Log.d("ZJW_LOG", "ACTION_DOWN 移动状态");
                        return true;

                    }
                }
            case MotionEvent.ACTION_MOVE:
                if (ev.getPointerCount() >= 2) {
                    //旋转
                    isMove = false;
                    isDraw = false;
                    currAngle = rotation(ev);
                    setRotation(currAngle);
                    invalidate();
                }
                if (isMove && ev.getPointerCount() == 1) {
                    //平移
                    isDraw = false;
                    setX(mOriginalX + ev.getRawX() - mOriginalRawX);
                    setY(mOriginalY + ev.getRawY() - mOriginalRawY);
                }
                if (isDraw) {
                    if (topLineRectF.contains(ev.getX(), ev.getY())) {
                        //尺子上方画线
                        if (interfaceView != null) {
                            interfaceView.onDrawLine(true, ev);
                            Log.d("ZJW_LOG", "ACTION_DOWN 移动了尺子上方画线" + "坐标值---->" + ev.getRawX() + "," + ev.getRawY());
                        }
                        return true;

                    }
                    if (bottomLineRectF.contains(ev.getX(), ev.getY())) {
                        //尺子下方画线
                        if (interfaceView != null) {
                            interfaceView.onDrawLine(false, ev);
                            Log.d("ZJW_LOG", "ACTION_DOWN 移动了尺子下方画线------------" + "坐标值---->" + ev.getX() + "," + ev.getY());
                        }
                        return true;
                    }

                }
                return true;
            case MotionEvent.ACTION_UP:
                isMove = false;
                isDraw = false;
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    public boolean isDraw() {
        return isDraw;
    }

    /**
     * 计算角度
     *
     * @param event
     * @return
     */
    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    /**
     * 关闭监听
     */
    public interface InterfaceView {
        void onClose();//关闭

        void onDrawLine(boolean isTop, MotionEvent ev);//顶部画线
    }


}