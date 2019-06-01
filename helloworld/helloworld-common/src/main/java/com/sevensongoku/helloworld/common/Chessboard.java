package com.sevensongoku.helloworld.common;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class Chessboard {
    static final int LENGTH = 3;
    static final int WIN_LENGTH = 3;
    static final int TOTAL = (int)Math.pow(LENGTH, 2);

    private static Chessboard chessboard;
    Point[] seats;

    private Chessboard() {
        Set<Point> points = new HashSet<>();
        for (int i = 1; i <= LENGTH; i++) {
            for (int j = 1; j <= LENGTH; j++) {
                points.add(new Point(i, j));
            }
        }
        this.seats = (Point[])points.toArray();
    }

    static Chessboard getInstance() {
        return Optional.ofNullable(chessboard).orElse(new Chessboard());
    }

    static boolean onLine(Point a, Point b) {
        return a.x == b.x
                || a.y == b.y
                || Math.abs(a.x - b.x) == Math.abs(a.y - b.y);
    }

    static boolean isWin(Point[] points) {
        for (int i = 0; i <= points.length - WIN_LENGTH; i++) {
            int count = 1;
            for (int j = i + 1; j < points.length; j++) {
                if (onLine(points[i], points[j])) {
                    count++;
                }
                if (count == WIN_LENGTH) {
                    return true;
                }
            }
        }
        return false;
    }
}
