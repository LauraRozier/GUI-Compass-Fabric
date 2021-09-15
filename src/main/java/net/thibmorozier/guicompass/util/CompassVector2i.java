package net.thibmorozier.guicompass.util;

public class CompassVector2i {
    private final int x;
    private final int y;

    public CompassVector2i(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String toString() {
        return String.format("{x: %d, y: %d}", x, y);
    }
}
