package com.omikronsoft.fidgetspinnerdefense.Utils;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.PointF;
import android.graphics.RectF;
import com.omikronsoft.fidgetspinnerdefense.BallControl;
import com.omikronsoft.fidgetspinnerdefense.FidgetControl;

/**
 * Created by Dariusz Lelek on 5/22/2017.
 * dariusz.lelek@gmail.com
 */

public class Globals {
    private static Globals instance;
    private int screenWidth, screenHeight;
    private float screenWidth2, screenHeight2, pixelDensity;
    private boolean soundEnabled;
    private PointF screenCenter;
    private Resources resources;
    private RectF addRect;
    private GameState gameState;
    private SharedPreferences prefs;

    private final static int ADD_HEIGHT = 50;
    private final static int MAX_FPS = 30;

    public enum GameState {
        NOT_RUNNING, RUNNING
    }

    private Globals() {
        setGameState(GameState.NOT_RUNNING);
    }

    public void setPrefs(SharedPreferences prefs) {
        this.prefs = prefs;
    }

    public boolean isSoundEnabled() {
        return prefs.getBoolean("soundEnabled", true);
    }

    public void soundButtonClick() {
        soundEnabled = !soundEnabled;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("soundEnabled", soundEnabled);
        editor.apply();

        if (!soundEnabled) {
            AudioPlayer.getInstance().stopSpinningSound();
        } else {
            if (gameState == GameState.RUNNING) {
                AudioPlayer.getInstance().playSound(AudioPlayer.SoundName.SPINNING);
            }
        }
    }

    public float getBestScore() {
        return prefs.getFloat("bestScore", 0.0f);
    }

    public void saveBestScore(float bestScore) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat("bestScore", bestScore);
        editor.apply();
    }

    public static int getMaxFps() {
        return MAX_FPS;
    }

    public synchronized GameState getGameState() {
        return gameState;
    }

    public void setScreenSizes(int width, int height) {
        screenWidth = width;
        screenHeight = height;

        screenWidth2 = screenWidth / 2;
        screenHeight2 = screenHeight / 2;

        screenCenter = new PointF(screenWidth2, screenHeight2);
    }

    public RectF getAddDrawPoint() {
        if (addRect == null) {
            addRect = new RectF(0, screenHeight - pixelDensity * ADD_HEIGHT, screenWidth, screenHeight);
        }
        return addRect;
    }

    public void startGame() {
        FidgetControl.getInstance().resetControl();
        BallControl.getInstance().resetBallsControl();
        setGameState(GameState.RUNNING);
    }

    public void endGame() {
        setGameState(GameState.NOT_RUNNING);
        AudioPlayer.getInstance().stopSpinningSound();
    }

    public int getPixelSize(int value) {
        return (int) (value * pixelDensity);
    }

    public void setPixelDensity(float density) {
        this.pixelDensity = density;
    }

    public void setResources(Resources resources) {
        this.resources = resources;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public float getScreenWidth2() {
        return screenWidth2;
    }

    public float getScreenHeight2() {
        return screenHeight2;
    }

    public PointF getScreenCenter() {
        return screenCenter;
    }

    public Resources getResources() {
        return resources;
    }

    public float getPixelDensity() {
        return pixelDensity;
    }

    private synchronized void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public synchronized static Globals getInstance() {
        if (instance == null) {
            instance = new Globals();
        }
        return instance;
    }
}
