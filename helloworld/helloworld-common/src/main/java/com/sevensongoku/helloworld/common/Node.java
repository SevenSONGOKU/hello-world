package com.sevensongoku.helloworld.common;

import java.util.HashSet;
import java.util.Set;

public class Node {
    Set<Node> children;
    boolean isMax;
    Node parent;
    WholeStatus status;
    int minValue;
    int maxValue;

    public Node() {
        children = new HashSet<Node>();
    }

    public Node(Node parent) {
        this();
        isMax = !parent.isMax;
        this.parent = parent;

        parent.children.add(this);
    }

    public Node(Node parent, Point seat) {
        this(parent);
        status = new WholeStatus(parent.status);
        status.seat = seat;
        if (isMax) {
            status.addMaxSeat(seat);
        } else {
            status.addMinSeat(seat);
        }
    }

    boolean isLeaf() {
        return status.isEnd();
    }

    int getEvaluation() {
        if (isLeaf()) {
            if (status.maxWin()) {
                return 1;
            } else if (status.minWin()) {
                return -1;
            } else {
                return 0;
            }
        } else {
            return isMax ? maxValue : minValue;
        }
    }
}
