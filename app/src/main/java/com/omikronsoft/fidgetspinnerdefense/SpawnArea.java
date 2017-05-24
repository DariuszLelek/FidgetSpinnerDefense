package com.omikronsoft.fidgetspinnerdefense;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.v4.content.ContextCompat;

import com.omikronsoft.fidgetspinnerdefense.Painting.PaintingResources;
import com.omikronsoft.fidgetspinnerdefense.Painting.Transparency;
import com.omikronsoft.fidgetspinnerdefense.Utils.ApplicationContext;
import com.omikronsoft.fidgetspinnerdefense.Utils.ArcUtils;
import com.omikronsoft.fidgetspinnerdefense.Utils.Globals;

/**
 * Created by Dariusz Lelek on 5/22/2017.
 * dariusz.lelek@gmail.com
 */

public class SpawnArea {
    private static SpawnArea instance;
    private float spawnCircleRadius;
    private int spawnPointY;
    private Paint spawnPaint, spawnBackPaint;
    private PointF spawnCircleCenter;

    private SpawnArea() {
        spawnCircleCenter = FidgetControl.getInstance().getFidgetLocation();
        spawnCircleRadius = Globals.getInstance().getScreenHeight2();

        Context context = ApplicationContext.get();

        spawnPaint = PaintingResources.getInstance().getStrokePaint(2, ContextCompat.getColor(context, R.color.spawn_line), Transparency.OPAQUE);
        spawnBackPaint = PaintingResources.getInstance().getStrokePaint((int) (spawnCircleRadius / Globals.getInstance().getPixelDensity()),
                ContextCompat.getColor(context, R.color.spawn_back), Transparency.OPAQUE);

        spawnPointY = (int) (Globals.getInstance().getScreenHeight2() / 12);
    }

    public int getSpawnPointY() {
        return spawnPointY;
    }

    void drawSpawnArea(Canvas canvas) {
        ArcUtils.drawArc(canvas, spawnCircleCenter, spawnCircleRadius + spawnCircleRadius / 2, 210, 120, spawnBackPaint);
        ArcUtils.drawArc(canvas, spawnCircleCenter, spawnCircleRadius, 210, 120, spawnPaint);
    }

    public synchronized static SpawnArea getInstance() {
        if (instance == null) {
            instance = new SpawnArea();
        }
        return instance;
    }
}
