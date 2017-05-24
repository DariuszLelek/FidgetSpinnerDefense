package com.omikronsoft.fidgetspinnerdefense.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import com.omikronsoft.fidgetspinnerdefense.FidgetControl;
import com.omikronsoft.fidgetspinnerdefense.Painting.PaintingResources;
import com.omikronsoft.fidgetspinnerdefense.Painting.Transparency;
import com.omikronsoft.fidgetspinnerdefense.R;
import com.omikronsoft.fidgetspinnerdefense.SpawnArea;
import com.omikronsoft.fidgetspinnerdefense.Utils.ApplicationContext;
import com.omikronsoft.fidgetspinnerdefense.Utils.Globals;
import java.util.Locale;

/**
 * Created by Dariusz Lelek on 5/22/2017.
 * dariusz.lelek@gmail.com
 */

public class StatusControl {
    private static StatusControl instance;
    private PointF rpmLoc, spinRpmLabel, soundLoc;
    private RectF soundArea;
    private int betsScoreY, spinBestScoreY, spinBestScoreY2, scoreY, offsetX;
    private Paint scorePaint, bestScorePaint, spinsPaint, spinsLabelPaint, rpmLabelPaint, rpmValuePaint, soundActivePaint, soundInactivePaint;
    private Bitmap soundBitmap;

    private FidgetControl fidgetControl;
    private Globals globals;

    private StatusControl() {
        globals = Globals.getInstance();
        fidgetControl = FidgetControl.getInstance();
        Context context = ApplicationContext.get();
        Resources res = globals.getResources();

        int rpmValue = res.getInteger(R.integer.rpmValue),
                numSize = res.getInteger(R.integer.numSize);

        PointF fidgetCenter = fidgetControl.getFidgetLocation();
        spinRpmLabel = new PointF(fidgetCenter.x, fidgetCenter.y - fidgetControl.getFidgetCenterWidth() / 5);
        rpmLoc = new PointF(fidgetCenter.x, fidgetCenter.y + Globals.getInstance().getPixelSize(rpmValue) / 2);

        spinBestScoreY = SpawnArea.getInstance().getSpawnPointY();
        betsScoreY = (FidgetControl.getInstance().getFidgetOffsetY() / 2) + (Globals.getInstance().getPixelSize(numSize) / 3);
        spinBestScoreY2 = (int) (2.5f * spinBestScoreY);
        offsetX = (int) (globals.getScreenWidth() - (Globals.getInstance().getScreenWidth2() / 4));
        scoreY = (int) (globals.getScreenHeight2() / 2.6f);

        soundBitmap = BitmapFactory.decodeResource(Globals.getInstance().getResources(), R.drawable.sound);
        soundLoc = new PointF(soundBitmap.getWidth() - (betsScoreY - soundBitmap.getWidth() / 2), betsScoreY - soundBitmap.getHeight() / 2);
        soundArea = new RectF(soundLoc.x - soundBitmap.getWidth() / 2, soundLoc.y - soundBitmap.getHeight() / 2,
                soundLoc.x + soundBitmap.getWidth() + soundBitmap.getWidth() / 2, soundLoc.y + soundBitmap.getHeight() + soundBitmap.getHeight() / 2);

        PaintingResources paintRes = PaintingResources.getInstance();
        scorePaint = paintRes.getTextPaintCenter(res.getInteger(R.integer.scoreLabel), ContextCompat.getColor(context, R.color.text_color), Transparency.HALF);
        bestScorePaint = paintRes.getTextPaintCenter(numSize, ContextCompat.getColor(context, R.color.spawn_line), Transparency.OPAQUE);
        spinsPaint = paintRes.getTextPaintCenter(numSize, ContextCompat.getColor(context, R.color.text_color), Transparency.OPAQUE);
        spinsLabelPaint = paintRes.getTextPaintCenter(res.getInteger(R.integer.spinsLabel), ContextCompat.getColor(context, R.color.text_color), Transparency.OPAQUE);
        rpmLabelPaint = paintRes.getTextPaintCenter(res.getInteger(R.integer.rpmLabel), ContextCompat.getColor(context, R.color.spawn_line), Transparency.OPAQUE);
        rpmValuePaint = paintRes.getTextPaintCenter(rpmValue, Color.WHITE, Transparency.OPAQUE);
        soundActivePaint = paintRes.getBitmapPaint(Transparency.OPAQUE);
        soundInactivePaint = paintRes.getBitmapPaint(Transparency.LOW);
    }

    public int getSpinBestScoreY() {
        return spinBestScoreY;
    }

    public void drawIndicators(Canvas canvas) {
        canvas.drawText(globals.getResources().getString(R.string.rpm), spinRpmLabel.x, spinRpmLabel.y, rpmLabelPaint);
        canvas.drawText(String.valueOf(fidgetControl.getSpeed()), rpmLoc.x, rpmLoc.y, rpmValuePaint);
        canvas.drawText(String.format(Locale.getDefault(), "%.2f", fidgetControl.getBestScore()), Globals.getInstance().getScreenWidth2(), betsScoreY, bestScorePaint);
        canvas.drawText(globals.getResources().getString(R.string.spins), offsetX, spinBestScoreY, spinsLabelPaint);
        canvas.drawText(String.valueOf(fidgetControl.getSpins()), offsetX, spinBestScoreY2, spinsPaint);
        canvas.drawText(String.valueOf(fidgetControl.getScore()), globals.getScreenWidth2(), scoreY, scorePaint);

        canvas.drawBitmap(soundBitmap, soundLoc.x, soundLoc.y, globals.isSoundEnabled() ? soundActivePaint : soundInactivePaint);
    }

    public RectF getSoundArea() {
        return soundArea;
    }

    public synchronized static StatusControl getInstance() {
        if (instance == null) {
            instance = new StatusControl();
        }
        return instance;
    }
}
