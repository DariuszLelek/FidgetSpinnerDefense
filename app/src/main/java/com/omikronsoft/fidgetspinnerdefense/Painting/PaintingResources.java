package com.omikronsoft.fidgetspinnerdefense.Painting;

import android.graphics.Paint;
import com.omikronsoft.fidgetspinnerdefense.Utils.Globals;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dariusz Lelek on 5/22/2017.
 * dariusz.lelek@gmail.com
 */

public class PaintingResources {
    private static PaintingResources instance;
    private Map<PaintingResource, Paint> cachedPaints;
    private Map<Transparency, Paint> bitmapPaints;

    private PaintingResources() {
        cachedPaints = new HashMap<>();
        bitmapPaints = new HashMap<>();
    }

    public Paint getBitmapPaint(Transparency transparency) {
        Paint result;
        if (bitmapPaints.containsKey(transparency)) {
            result = bitmapPaints.get(transparency);
        } else {
            result = new Paint();
            result.setAntiAlias(true);
            result.setFilterBitmap(true);
            result.setDither(true);
            result.setAlpha(transparency.value);
            bitmapPaints.put(transparency, result);
        }
        return result;
    }

    public Paint getTextPaintCenter(int width, int color, Transparency trans) {
        Paint paint;
        PaintingResource pr = new PaintingResource.Builder().color(color).width(width).transparency(trans.value).build();
        if (cachedPaints.containsKey(pr)) {
            paint = cachedPaints.get(pr);
        } else {
            paint = new Paint();
            paint.setTextSize(width * Globals.getInstance().getPixelDensity());
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(color);
            paint.setAlpha(trans.value);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setAntiAlias(true);
            cachedPaints.put(pr, paint);
        }
        return paint;
    }

    public Paint getFillPaint(int color, Transparency trans) {
        Paint paint;
        PaintingResource pr = new PaintingResource.Builder().color(color).transparency(trans.value).build();
        if (cachedPaints.containsKey(pr)) {
            paint = cachedPaints.get(pr);
        } else {
            paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(color);
            paint.setAlpha(trans.value);
            paint.setAntiAlias(true);
            cachedPaints.put(pr, paint);
        }
        return paint;
    }

    public Paint getStrokePaint(int width, int color, Transparency trans) {
        Paint paint;
        PaintingResource pr = new PaintingResource.Builder().color(color).transparency(trans.value).width(width).build();
        if (cachedPaints.containsKey(pr)) {
            paint = cachedPaints.get(pr);
        } else {
            paint = new Paint();
            paint.setStrokeWidth(width * Globals.getInstance().getPixelDensity());
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(color);
            paint.setAlpha(trans.value);
            paint.setAntiAlias(true);
            cachedPaints.put(pr, paint);
        }
        return paint;
    }

    public synchronized static PaintingResources getInstance() {
        if (instance == null) {
            instance = new PaintingResources();
        }
        return instance;
    }
}
