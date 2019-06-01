package com.sevensongoku.helloworld.common;

public class Tree {
    Node root;

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("root\n");
        for (Node child : root.children) {
            getString(sb, child, 0);
//            if (child.status.seat == 3) break;
        }
        return sb.toString();
    }

    void getString(StringBuilder sb, Node node, int level) {
        level += 2;
        for (int i = 0; i < level; i++) {
            sb.append("-");
        }
        sb.append(node.status.seat + "\n");
        for (Node child : node.children) {
            getString(sb, child, level);
        }
    }
}
