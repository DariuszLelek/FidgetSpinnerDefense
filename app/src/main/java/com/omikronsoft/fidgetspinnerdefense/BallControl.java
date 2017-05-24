package com.omikronsoft.fidgetspinnerdefense;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;

import com.omikronsoft.fidgetspinnerdefense.Painting.PaintingResources;
import com.omikronsoft.fidgetspinnerdefense.Painting.Transparency;
import com.omikronsoft.fidgetspinnerdefense.Utils.Globals;
import com.omikronsoft.fidgetspinnerdefense.ui.StatusControl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Dariusz Lelek on 5/22/2017.
 * dariusz.lelek@gmail.com
 */

public class BallControl {
    private static BallControl instance;
    private int ballWidth2, ballSpawnY, pendingBallId, spawnCounter, spawnDelay;
    private float ballImpactDistance, pendingBallSpeed;
    private long lastBallSpawnTime;
    private Bitmap scaledBall;
    private List<Ball> inactiveBalls;
    private PointF fidgetCenterPosition;
    private Random rand;
    private Resources res;

    private final List<Ball> activeBalls;
    private final int minimalSpawnDelay;
    private final int increaseSpawnTimeEvery = 6;
    private final int spawnDiff = 100;

    private BallControl() {
        int ballWidth = (int) (Globals.getInstance().getScreenWidth2() / 6f);
        ballWidth2 = ballWidth / 2;
        rand = new Random();
        res = Globals.getInstance().getResources();


        Bitmap ball = BitmapFactory.decodeResource(Globals.getInstance().getResources(), R.drawable.ball);
        scaledBall = Bitmap.createScaledBitmap(ball, ballWidth, ballWidth, true);
        fidgetCenterPosition = FidgetControl.getInstance().getFidgetLocation();
        ballImpactDistance = FidgetControl.getInstance().getRadius() + (ballWidth / 2);
        ballSpawnY = StatusControl.getInstance().getSpinBestScoreY();

        activeBalls = new ArrayList<>();
        inactiveBalls = new ArrayList<>();
        minimalSpawnDelay = res.getInteger(R.integer.minimalSpawnDelay);
    }

    public void resetBallsControl() {
        pendingBallId = 0;
        pendingBallSpeed = 6f;
        getActiveBalls().clear();
        spawnDelay = res.getInteger(R.integer.initialBallSpawnDelay);

        final int sleepTime = 1000 / Globals.getMaxFps();

        Thread updatePositions = new Thread() {
            @Override
            public void run() {
                while (Globals.getInstance().getGameState() == Globals.GameState.RUNNING) {
                    try {
                        synchronized (activeBalls) {
                            updateBallsPosition();

                            if (System.currentTimeMillis() - lastBallSpawnTime >= spawnDelay) {
                                spawnBall();
                                spawnCounter++;

                                if (spawnCounter % increaseSpawnTimeEvery == 0) {
                                    spawnDelay -= spawnDiff;
                                    if (spawnDelay < minimalSpawnDelay) {
                                        spawnDelay = minimalSpawnDelay;
                                    }
                                }
                            }

                            sleep(sleepTime);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        spawnBall();
        spawnCounter = 1;
        updatePositions.start();
    }

    synchronized void drawBalls(Canvas canvas) {
        for (Ball ball : getActiveBalls()) {
            PointF ballPosition = ball.getPosition();
            canvas.drawBitmap(scaledBall, ballPosition.x, ballPosition.y, PaintingResources.getInstance().getBitmapPaint(Transparency.OPAQUE));
        }
    }

    private synchronized void clearInactiveBalls() {
        for (Ball inactiveBall : inactiveBalls) {
            activeBalls.remove(inactiveBall);
        }
    }

    private synchronized void updateBallsPosition() {
        inactiveBalls.clear();

        for (Ball ball : getActiveBalls()) {
            PointF ballPosition = ball.getPosition();
            double dist = Math.sqrt(Math.pow(Math.abs((ballPosition.x + ballWidth2) - fidgetCenterPosition.x), 2)
                    + Math.pow(Math.abs((ballPosition.y + ballWidth2) - fidgetCenterPosition.y), 2));

            if (dist <= ballImpactDistance) {
                FidgetControl.getInstance().processHit();
                inactiveBalls.add(ball);
            } else {
                ball.updatePosition();
            }
        }

        if (!inactiveBalls.isEmpty()) {
            clearInactiveBalls();
        }
    }

    private synchronized void spawnBall() {
        PointF spawnPos = new PointF(rand.nextInt(Globals.getInstance().getScreenWidth()), ballSpawnY);
        Ball ball = new Ball(pendingBallId, pendingBallSpeed, spawnPos, fidgetCenterPosition, res.getInteger(R.integer.ballSteps));
        lastBallSpawnTime = System.currentTimeMillis();
        activeBalls.add(ball);
        pendingBallId++;
        pendingBallSpeed += 0.2f;
    }

    private synchronized List<Ball> getActiveBalls() {
        return activeBalls;
    }

    public synchronized static BallControl getInstance() {
        if (instance == null) {
            instance = new BallControl();
        }
        return instance;
    }
}
