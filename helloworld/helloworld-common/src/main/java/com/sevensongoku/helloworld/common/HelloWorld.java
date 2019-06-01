package com.sevensongoku.helloworld.common;

import java.util.Set;

public class HelloWorld {

    static Point[] seats = Chessboard.getInstance().seats;
//    static int[] seats = {1, 2, 3};

    public static void main(String[] args) {
        System.out.println(createTree());
    }

    static Tree createTree() {
        Tree tree = new Tree();

        Node root = new Node();
        root.isMax = true;
        root.status = new WholeStatus();
        tree.root = root;

        createChildren(root);

        return tree;
    }

    static void createChildren(Node root) {
        for (Point seat : seats) {
            if (root.status.existSeats.contains(seat)) continue;

            Node node = new Node(root, seat);
            createChildren(node);
        }
    }
}
