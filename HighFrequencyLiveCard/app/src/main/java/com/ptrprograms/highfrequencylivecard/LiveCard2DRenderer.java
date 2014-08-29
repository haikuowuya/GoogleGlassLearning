package com.ptrprograms.highfrequencylivecard;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.SystemClock;
import android.view.SurfaceHolder;
import android.view.View;

import com.google.android.glass.timeline.DirectRenderingCallback;

/**
 * Created by paulruiz on 8/28/14.
 */
public class LiveCard2DRenderer implements DirectRenderingCallback {

    private static final String TAG = "LiveCard2DRenderer";
    private static final long FRAME_TIME_MILLIS = 33;//~30 FPS
    private SurfaceHolder mHolder;
    private boolean mPaused;
    private RenderThread mRenderThread;

    private int canvasWidth;
    private int canvasHeight;
    private int diffX = 25;
    private int incY = 1;
    private float bouncingX;
    private float bouncingY;
    private double angle;
    private Paint paint;
    private Path path;

    @Override
    public void renderingPaused(SurfaceHolder surfaceHolder, boolean b) {
        mPaused = b;
        updateRendering();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height ) {
        canvasWidth = width;
        canvasHeight = height;

        bouncingX = canvasWidth / 2;
        bouncingY = canvasHeight / 2;

        angle = - Math.PI/4.0;

        paint = new Paint( Paint.ANTI_ALIAS_FLAG );
        paint.setColor( Color.BLUE );
        paint.setStyle( Paint.Style.FILL );
        paint.setStyle( Paint.Style.STROKE );

        path = new Path();
        mHolder = holder;
        updateRendering();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mHolder = null;
        updateRendering();
    }

    private synchronized void updateRendering() {
        boolean shouldRender = mHolder != null && !mPaused;
        boolean rendering = mRenderThread != null;

        if( shouldRender != rendering ) {
            if( shouldRender ) {
                mRenderThread = new RenderThread( this );
                mRenderThread.start();
            } else {
                mRenderThread.quit();
                mRenderThread = null;
            }
        }
    }

    public void drawInCanvas( View view ) {
        Canvas canvas;

        try {
            canvas = mHolder.lockCanvas();
        } catch( Exception e ) {
            return;
        }

        if( canvas != null ) {
            bouncingX += diffX;
            bouncingY += diffX * Math.tan( angle );
            bouncingY *= incY;

            canvas.drawColor( Color.BLACK );//background
            canvas.drawCircle( bouncingX, bouncingY, 20, paint );

            if( bouncingX > canvasWidth || bouncingX < 0 ) {
                diffX = -diffX;
                angle = -angle;
            } else if( bouncingY > canvasHeight || bouncingY < 0 ) {
                angle = -angle;
            }

            float mid = canvasWidth / 2;
            float min = canvasHeight;
            float half = canvasHeight / 2;
            mid -= half;

            //add star
            paint.setStrokeWidth( min/10 );
            paint.setStyle( Paint.Style.STROKE );
            path.reset();
            paint.setStyle( Paint.Style.FILL );

            path.moveTo( mid + half * 0.5f, half * 0.84f );
            path.lineTo( mid + half * 1.5f, half * 0.84f );

            path.lineTo(mid + half * 0.68f, half * 1.45f);
            path.lineTo(mid + half * 1.0f, half * 0.5f);
            path.lineTo(mid + half * 1.32f, half * 1.45f);
            path.lineTo(mid + half * 0.5f, half * 0.84f);

            path.close();
            canvas.drawPath( path, paint );
            mHolder.unlockCanvasAndPost( canvas );

        }
    }

    private class RenderThread extends Thread {
        private boolean mShouldRun;
        LiveCard2DRenderer mRenderer;

        public RenderThread( LiveCard2DRenderer renderer ) {
            mShouldRun = true;
            mRenderer = renderer;
        }

        private synchronized boolean shouldRun() {
            return mShouldRun;
        }

        public synchronized void quit() {
            mShouldRun = false;
        }
        @Override
        public void run() {
            while (shouldRun()) {
                mRenderer.drawInCanvas(null);
                SystemClock.sleep(FRAME_TIME_MILLIS);
            }
        }
    }
}
