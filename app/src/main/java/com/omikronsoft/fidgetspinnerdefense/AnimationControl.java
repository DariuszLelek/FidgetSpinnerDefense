package com.omikronsoft.fidgetspinnerdefense;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.omikronsoft.fidgetspinnerdefense.Painting.PaintingResources;
import com.omikronsoft.fidgetspinnerdefense.Painting.Transparency;
import com.omikronsoft.fidgetspinnerdefense.Utils.Globals;
import com.omikronsoft.fidgetspinnerdefense.ui.StatusControl;

/**
 * Created by Dariusz Lelek on 5/22/2017.
 * dariusz.lelek@gmail.com
 */

public class AnimationControl extends SurfaceView implements Runnable {
    private Thread thread;
    private boolean canDraw;
    private SurfaceHolder surfaceHolder;

    private final Globals globals;
    private final PaintingResources paintRes = PaintingResources.getInstance();

    private final int FRAME_PERIOD = 1000 / Globals.getMaxFps(); // the frame period

    public AnimationControl(Context context) {
        super(context);

        globals = Globals.getInstance();
        thread = null;
        canDraw = false;
        surfaceHolder = getHolder();
    }

    @Override
    public void run() {
        Paint backPaint, adsBackPaint;

        backPaint = paintRes.getFillPaint(ContextCompat.getColor(getContext(), R.color.back_ground), Transparency.OPAQUE);
        adsBackPaint = paintRes.getFillPaint(ContextCompat.getColor(getContext(), R.color.spawn_back), Transparency.OPAQUE);
        while (canDraw) {
            if (!surfaceHolder.getSurface().isValid()) {
                continue;
            }

            long started = System.currentTimeMillis();

            Canvas canvas = surfaceHolder.lockCanvas();

            // Draw background
            canvas.drawRect(new RectF(0, 0, globals.getScreenWidth(), Globals.getInstance().getScreenHeight()), backPaint);

            // Draw Ads area
            canvas.drawRect(globals.getAddDrawPoint(), adsBackPaint);

            FidgetControl.getInstance().drawFidgetBack(canvas);
            FidgetControl.getInstance().drawFidget(canvas);

            if (globals.getGameState() == Globals.GameState.RUNNING) {
                BallControl.getInstance().drawBalls(canvas);
            }

            SpawnArea.getInstance().drawSpawnArea(canvas);
            StatusControl.getInstance().drawIndicators(canvas);

            surfaceHolder.unlockCanvasAndPost(canvas);

            float deltaTime = (System.currentTimeMillis() - started);

            int sleepTime = (int) (FRAME_PERIOD - deltaTime);
            if (sleepTime > 0) {
                try {
                    thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        boolean result = false;

        if (StatusControl.getInstance().getSoundArea().contains(x, y)) {
            Globals.getInstance().soundButtonClick();
        } else {
            result = FidgetControl.getInstance().processTouchEvent(event, x, y);
        }

        return result || super.onTouchEvent(event);
    }

    public void pause() {
        canDraw = false;
        while (true) {
            try {
                thread.join();
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        thread = null;
    }

    public void resume() {
        canDraw = true;
        thread = new Thread(this);
        thread.start();
    }

}
