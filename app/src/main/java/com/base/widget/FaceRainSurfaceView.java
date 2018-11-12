package com.base.widget;

import java.util.ArrayList;
import java.util.List;

import android.animation.TimeInterpolator;
import android.animation.TypeEvaluator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.LinearInterpolator;

import com.base.animator.FaceLinearEvalutor;
import com.base.utils.DeviceInfoUtils;
import com.wmlives.heihei.R;

public class FaceRainSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private static final int DEFAULT_DURATION = 5000;
    private static final int DEFAULT_FRAME = 1000 / 60;

    public FaceRainSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public FaceRainSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FaceRainSurfaceView(Context context) {
        super(context);
        init();
    }

    private SurfaceHolder mHolder;
    private RenderThread mRenderThread;

    private void init() {
        mHolder = getHolder();
        mHolder.addCallback(this);

        setZOrderOnTop(true);
        mHolder.setFormat(PixelFormat.TRANSLUCENT);

        int width = DeviceInfoUtils.getScreenWidth(getContext());
        int height = DeviceInfoUtils.getScreenHeight(getContext());
        for (int i = 0; i < 12; i++) {
            if (i < 4) {
                startPoints.add(new Point((int) (Math.random() * (width - 200)), 150));
                endPoints.add(new Point((int) (Math.random() * (width - 200)), height + 300));
            } else if (i < 8) {
                startPoints.add(new Point((int) (Math.random() * (width - 200)), -150));
                endPoints.add(new Point((int) (Math.random() * (width - 200)), height + 150));
            } else {
                startPoints.add(new Point((int) (Math.random() * (width - 200)), -300));
                endPoints.add(new Point((int) (Math.random() * (width - 200)), height));
            }
        }

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.emoji_0);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        getHolder().getSurface().release();
        stop();
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    private Paint mPaint;
    private Bitmap bitmap;

    List<Point> startPoints = new ArrayList<>();
    List<Point> endPoints = new ArrayList<>();

    private void drawFace(List<Point> result) {
        Canvas canvas = mHolder.lockCanvas();
        // canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
        try {
            canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);

            if (result != null && result.size() > 0) {
                int count = result.size();
                Point p = null;
                for (int i = 0; i < count; i++) {
                    p = result.get(i);
                    if (p.x >= 0 && p.x <= getWidth() && p.y >= 0 && p.y <= getHeight()) {
                        // Matrix m = new Matrix();
                        // m.setTranslate(p.x, p.y);
                        // if (i % 5 == 0) {
                        // m.postScale(2, 2);
                        // }
                        // canvas.drawBitmap(bitmap, m, mPaint);
                        // Rect src = new Rect(p.x, p.y, p.x + bitmap.getWidth(), p.y + bitmap.getHeight());
                        // Rect dst = new Rect(p.x, p.y, p.x + bitmap.getWidth() * 2, p.y + bitmap.getHeight() * 2);
                        // canvas.drawBitmap(bitmap, null, dst, mPaint);
                        canvas.drawBitmap(bitmap, p.x, p.y, mPaint);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                mHolder.unlockCanvasAndPost(canvas);
            } catch (Throwable e2) {
                // TODO: handle exception
            }

        }

    }

    public void setFaceAnimatorListener(FaceAnimationListener mListener) {
        this.mListener = mListener;
    }

    public void removeAllAnimatorListeners() {
        this.mListener = null;
    }

    private FaceAnimationListener mListener;

    private volatile long startTime = 0l;

    public void start() {
        synchronized (mLock) {
            if (status != STATUS_RUNNIND) {
                status = STATUS_RUNNIND;
                startTime = System.currentTimeMillis();
                mRenderThread = new RenderThread();
                mRenderThread.start();
            }
        }
    }

    /**
     * 暂停
     */
    public void pause() {
        synchronized (mLock) {
            status = STATUS_PAUSED;
        }
    }

    /**
     * 恢复
     */
    public void resume() {
        synchronized (mLock) {
            status = STATUS_RUNNIND;
            mLock.notifyAll();
        }
    }

    /**
     * 停止
     */
    public void stop() {
        synchronized (mLock) {
            if (status != STATUS_IDLE) {
                status = STATUS_IDLE;
                mLock.notifyAll();

                if (mListener != null) {
                    mListener.onAnimationEnd(this);
                }

            }

        }

    }

    private TypeEvaluator mTypeEvaluator;
    private TimeInterpolator mInterpolator;

    /**
     * 每一帧
     */
    private void doFrame() {
        long nowTime = System.currentTimeMillis();
        long curDuration = nowTime - startTime;// 当前时间进度
        if (curDuration >= DEFAULT_DURATION) {// 大于等于duration就结束
            stop();
            return;
        }
        float t = (float) curDuration / (float) DEFAULT_DURATION;// 时间进度比例
        t = Math.min(t, 1.0f);
        if (mTypeEvaluator == null) {// 计算贝塞尔坐标的计算器
            mTypeEvaluator = new FaceLinearEvalutor();
        }

        if (mInterpolator == null) {// 插值器
            mInterpolator = new LinearInterpolator();
        }

        t = mInterpolator.getInterpolation(t);// 使用插值器后的fraction

        // 三次贝塞尔后的点的坐标
        List<Point> result = (List<Point>) mTypeEvaluator.evaluate(t, startPoints, endPoints);
        drawFace(result);// 根据坐标画bitmap
    }

    public static final int STATUS_IDLE = 0;// 初始状态或者停止状态
    public static final int STATUS_RUNNIND = 1;// 运行状态
    public static final int STATUS_PAUSED = 2;// 暂停状态

    private int status = STATUS_IDLE;

    private byte[] mLock = new byte[0];

    private class RenderThread extends Thread {

        private long preFrameTime = 0;

        @Override
        public void run() {
            super.run();

            while (status != STATUS_IDLE) {
                synchronized (mLock) {
                    if (status == STATUS_PAUSED) {
                        try {
                            mLock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                doFrame();

                // try {
                // Thread.sleep(DEFAULT_FRAME);
                // } catch (InterruptedException e) {
                // e.printStackTrace();
                // }

            }

        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getHolder().getSurface().release();
        stop();
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    public static interface FaceAnimationListener {

        public void onAnimationEnd(FaceRainSurfaceView view);

        public void onAnimationCancel(FaceRainSurfaceView view);
    }

}
