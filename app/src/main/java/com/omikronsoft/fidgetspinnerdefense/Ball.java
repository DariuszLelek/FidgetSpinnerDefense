package com.omikronsoft.fidgetspinnerdefense;

import android.graphics.PointF;

/**
 * Created by Dariusz Lelek on 5/22/2017.
 * dariusz.lelek@gmail.com
 */

class Ball {
    private int id;
    private float speed;
    private PointF position;
    private float xPositionStep, yPositionStep;

    Ball(int id, float speed, PointF position, PointF target, int steps) {
        this.id = id;
        this.speed = speed;
        this.position = position;

        xPositionStep = (position.x >= target.x ? -1 : 1) * (Math.abs(position.x - target.x) / steps);
        yPositionStep = Math.abs(position.y - target.y) / steps;
    }

    synchronized PointF getPosition() {
        return position;
    }

    synchronized void updatePosition() {
        position = new PointF(position.x + speed * xPositionStep, position.y + speed * yPositionStep);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ball ball = (Ball) o;

        return id == ball.id;

    }

    @Override
    public int hashCode() {
        return id;
    }
}
