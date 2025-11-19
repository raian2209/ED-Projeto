package com.exemplo.huffman.util;


public class Node implements Comparable<Node> {
    public int freq;
    public int value;  // byte 0–255 ou 256 para PSEUDO_EOF
    public Node left, right;

    public Node(int value, int freq) {
        this.value = value;
        this.freq = freq;
    }

    public Node(Node left, Node right) {
        this.left = left;
        this.right = right;
        this.freq = left.freq + right.freq;
        this.value = -1; // nó interno
    }

    @Override
    public int compareTo(Node other) {
        return Integer.compare(this.freq, other.freq);
    }

   public boolean isLeaf() {
        return left == null && right == null;
    }
}
