package com.sevensongoku.helloworld.common;

public class Point {
    int x;
    int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Point ?
                x == ((Point)o).x && y == ((Point)o).y
                : false;
    }
}
