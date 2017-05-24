package com.omikronsoft.fidgetspinnerdefense.Painting;

/**
 * Created by Dariusz Lelek on 5/22/2017.
 * dariusz.lelek@gmail.com
 */

class PaintingResource {
    private final int color;
    private final Integer width, transparency;

    static class Builder {
        private int color;
        private Integer width, transparency;

        Builder width(int w) {
            this.width = w;
            return this;
        }

        Builder color(int c) {
            this.color = c;
            return this;
        }

        Builder transparency(int t) {
            this.transparency = t;
            return this;
        }

        PaintingResource build() {
            return new PaintingResource(this);
        }
    }

    private PaintingResource(Builder b) {
        this.color = b.color;
        this.width = b.width;
        this.transparency = b.transparency;
    }

    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof PaintingResource) {
            PaintingResource pr = (PaintingResource) o;
            result = pr.color == color;
            if (result && pr.width != null && width != null) {
                result = pr.width.equals(width);
            }
            if (result && pr.transparency != null && transparency != null) {
                result = pr.transparency.equals(transparency);
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        int result = (width != null ? width : 0);
        result = 31 * result + color;
        result = 31 * result + (transparency != null ? transparency : 0);
        return result;
    }
}
