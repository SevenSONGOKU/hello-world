package com.sevensongoku.helloworld.common;

import java.util.HashSet;
import java.util.Set;

public class WholeStatus {
    Set<Point> maxSeats;
    Set<Point> minSeats;
    Set<Point> existSeats;
    Point seat;

    public WholeStatus() {
        maxSeats = new HashSet<Point>();
        minSeats = new HashSet<Point>();
        existSeats = new HashSet<Point>();
    }

    public WholeStatus(WholeStatus status) {
        this();
        maxSeats.addAll(status.maxSeats);
        minSeats.addAll(status.minSeats);
        existSeats.addAll(status.existSeats);
    }

    WholeStatus addMaxSeat(Point seat) {
        maxSeats.add(seat);
        existSeats.add(seat);
        return this;
    }

    WholeStatus addMinSeat(Point seat) {
        minSeats.add(seat);
        existSeats.add(seat);
        return this;
    }

    boolean isEnd() {
        return existSeats.size() == Chessboard.TOTAL
                || maxWin()
                || minWin();
    }

    boolean maxWin() {
        Point[] points = (Point[])maxSeats.toArray();
        return Chessboard.isWin(points);
    }

    boolean minWin() {
        Point[] points = (Point[])minSeats.toArray();
        return Chessboard.isWin(points);
    }
}
