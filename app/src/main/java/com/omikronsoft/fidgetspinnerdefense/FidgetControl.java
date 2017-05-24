package com.omikronsoft.fidgetspinnerdefense;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;

import com.omikronsoft.fidgetspinnerdefense.Painting.PaintingResources;
import com.omikronsoft.fidgetspinnerdefense.Painting.Transparency;
import com.omikronsoft.fidgetspinnerdefense.Utils.ApplicationContext;
import com.omikronsoft.fidgetspinnerdefense.Utils.AudioPlayer;
import com.omikronsoft.fidgetspinnerdefense.Utils.Globals;

/**
 * Created by Dariusz Lelek on 5/22/2017.
 * dariusz.lelek@gmail.com
 */

public class FidgetControl {
    private static FidgetControl instance;
    private float scaledWidth2, scaledHeight2, fidgetBitmapLocY, fidgetBitmapLocX, radius, angle, lastAngle, bestScore;
    private int speed, score, spins, ballScore;
    private boolean pressed;
    private long touchStartTime;
    private Matrix matrix;
    private Paint fidgetPaint, fidgetShadowPaint, fidgetBack;
    private PointF fidgetLocation, fidgetCenterLocation, touchStartLoc;
    private Bitmap scaledFidget;
    private Bitmap scaledFidgetCenter;

    private final int maxSpeed = 10000;
    private final int speedDecrease;
    private final int touchDistPercentMulti;
    private final int touchTimeLimit;

    private FidgetControl() {
        Bitmap fidget = BitmapFactory.decodeResource(Globals.getInstance().getResources(), R.drawable.fidget);
        scaledFidget = Bitmap.createScaledBitmap(fidget, Globals.getInstance().getScreenWidth(), Globals.getInstance().getScreenWidth(), true);

        Bitmap fidgetCenter = BitmapFactory.decodeResource(Globals.getInstance().getResources(), R.drawable.fidgetcenter);
        int scaleRatio = (int) (fidgetCenter.getWidth() * (scaledFidget.getWidth() / (float) fidget.getWidth()));
        scaledFidgetCenter = Bitmap.createScaledBitmap(fidgetCenter, scaleRatio, scaleRatio, true);

        touchDistPercentMulti = 50;
        touchTimeLimit = 1000;

        speedDecrease = maxSpeed / 10;
        scaledWidth2 = scaledFidget.getWidth() / 2;
        scaledHeight2 = scaledFidget.getHeight() / 2;

        radius = scaledWidth2 - scaledWidth2 / 8;

        matrix = new Matrix();

        fidgetPaint = PaintingResources.getInstance().getBitmapPaint(Transparency.OPAQUE);
        fidgetShadowPaint = PaintingResources.getInstance().getBitmapPaint(Transparency.HALF);

        float offsetY = scaledHeight2 / 3;
        PointF screenCenter = Globals.getInstance().getScreenCenter();
        fidgetBitmapLocX = 0;
        fidgetBitmapLocY = Globals.getInstance().getScreenCenter().y - scaledHeight2 + offsetY;
        fidgetLocation = new PointF(screenCenter.x, screenCenter.y + offsetY);
        fidgetCenterLocation = new PointF(fidgetLocation.x - scaledFidgetCenter.getWidth() / 2, fidgetLocation.y - scaledFidgetCenter.getWidth() / 2);

        fidgetBack = PaintingResources.getInstance().getFillPaint(ContextCompat.getColor(ApplicationContext.get(), R.color.fidget_area), Transparency.OPAQUE);
        bestScore = Globals.getInstance().getBestScore();

        resetControl();
    }

    public void resetControl() {
        pressed = false;
        touchStartTime = 0;
        angle = 0;
        ballScore = 1;

        setSpeed(0);
        setSpins(0);
        setScore(0);
    }

    public float getBestScore() {
        return bestScore;
    }

    public int getFidgetOffsetY() {
        return (int) (fidgetLocation.y - Globals.getInstance().getScreenHeight2());
    }

    public PointF getFidgetLocation() {
        return fidgetLocation;
    }

    public int getFidgetCenterWidth() {
        return scaledFidgetCenter.getWidth();
    }

    public synchronized int getSpins() {
        return spins;
    }

    public synchronized int getSpeed() {
        return speed >= 0 ? speed : 0;
    }

    public synchronized int getScore() {
        return score;
    }

    void drawFidgetBack(Canvas canvas) {
        canvas.drawCircle(fidgetLocation.x, fidgetLocation.y, radius, fidgetBack);
    }

    void drawFidget(Canvas canvas) {
        if (speed > 0) {
            canvas.drawBitmap(scaledFidget, matrix, fidgetShadowPaint);

            // rotation handle
            angle = (speed / 30);
            lastAngle = (angle + lastAngle) % 360;

            float speedDecrease = speed * 0.005f;
            speed -= speedDecrease < 1 ? 1 : speedDecrease;

            matrix.setRotate(lastAngle, scaledWidth2, scaledHeight2);
            matrix.postTranslate(fidgetBitmapLocX, fidgetBitmapLocY);

            canvas.drawBitmap(scaledFidget, matrix, fidgetPaint);
            canvas.drawBitmap(scaledFidgetCenter, fidgetCenterLocation.x, fidgetCenterLocation.y, fidgetPaint);
        } else {
            matrix.setRotate(0, scaledWidth2, scaledHeight2);
            matrix.postTranslate(fidgetBitmapLocX, fidgetBitmapLocY);
            canvas.drawBitmap(scaledFidget, matrix, fidgetPaint);
        }
    }

    boolean processTouchEvent(MotionEvent event, float x, float y) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            pressed = true;
            touchStartLoc = new PointF(x, y);
            touchStartTime = event.getEventTime();
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (pressed) {
                pressed = false;
                double touchDist = Math.sqrt(Math.pow((Math.abs(x - touchStartLoc.x)), 2) + Math.pow((Math.abs(y - touchStartLoc.y)), 2));
                long touchTime = event.getEventTime() - touchStartTime;

                if (touchDist > 0 && touchTime > 0 && touchTime < touchTimeLimit) {
                    int touchDistPercent = touchDistPercentMulti * (int) (touchDist / Globals.getInstance().getScreenHeight() * 100);
                    int spinSpeed = (touchDistPercent / (touchTime > 100 ? (int) (touchTime / 100) : 1));

                    if (spinSpeed > 0) {
                        AudioPlayer.getInstance().playSound(AudioPlayer.SoundName.SWEEP_SPIN);

                        if (Globals.getInstance().getGameState() == Globals.GameState.NOT_RUNNING) {
                            Globals.getInstance().startGame();
                        }

                        addSpin();
                        updateSpeed(spinSpeed);
                    }
                }
                touchStartTime = 0;
            }
        }

        return false;
    }

    void processHit() {
        setSpeed(getSpeed() - speedDecrease);

        if (getSpeed() <= 0) {
            AudioPlayer.getInstance().playSound(AudioPlayer.SoundName.GAME_OVER);
            float newBest = spins > 0 ? score / (float) spins : 0;
            updateBestScore(newBest);

            Globals.getInstance().endGame();
            resetControl();
        } else {
            AudioPlayer.getInstance().playSound(AudioPlayer.SoundName.BALL_HIT);
            updateScore();
        }
    }

    float getRadius() {
        return radius;
    }

    private synchronized void updateSpeed(int value) {
        int newSpeed = speed + value;
        speed = newSpeed > maxSpeed ? maxSpeed : newSpeed;
    }

    private synchronized void addSpin() {
        spins++;
    }

    private synchronized void setSpeed(int value) {
        this.speed = value;
    }

    private synchronized void setSpins(int value) {
        this.spins = value;
    }

    private synchronized void setScore(int value) {
        this.score = value;
    }

    private synchronized void updateScore() {
        this.score += ballScore;
        ballScore++;
    }

    private void updateBestScore(float newBest) {
        if (newBest > bestScore) {
            bestScore = newBest;
            Globals.getInstance().saveBestScore(bestScore);
        }
    }

    public synchronized static FidgetControl getInstance() {
        if (instance == null) {
            instance = new FidgetControl();
        }
        return instance;
    }
}
