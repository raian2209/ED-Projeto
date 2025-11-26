package com.exemplo.huffman.util;

import java.util.ArrayList;
import java.util.List;

public class MinHeap {

    private List<Node> heap;

    public MinHeap() {
        this.heap = new ArrayList<>();
    }

    // Método auxiliar necessário no Service
    public int size() {
        return heap.size();
    }

    public List<Node> getHeap() {
        return new ArrayList<>(heap);
    }

    private int leftChild(int index) {
        return 2 * index + 1;
    }

    private int rightChild(int index) {
        return 2 * index + 2;
    }

    private int parent(int index) {
        return (index - 1) / 2;
    }

    private void swap(int index1, int index2) {
        Node temp = heap.get(index1);
        heap.set(index1, heap.get(index2));
        heap.set(index2, temp);
    }

    // Min Heap Insert
    public void minHeapInsert(Node value) {
        heap.add(value);

        // atual index do valor adcionado
        int atualIndex = heap.size() - 1;


        // Se o nó atual for MENOR que o pai, sobe (swap)
        while (atualIndex > 0 && heap.get(atualIndex).compareTo(heap.get(parent(atualIndex))) < 0) {
            swap(atualIndex, parent(atualIndex));
            atualIndex = parent(atualIndex);
        }
    }

    // Min Heap Remove
    public Node minHeapRemove() {
        if (heap.isEmpty()) return null;

        // Se só tem 1, remove e retorna
        if (heap.size() == 1) return heap.remove(0);

        Node numRemove = heap.get(0);

        // Pega o último e coloca na raiz
        Node lastNode = heap.remove(heap.size() - 1);
        heap.set(0, lastNode);

        minHeapSinkDown(0);

        return numRemove;
    }

    private void minHeapSinkDown(int index) {
        int minHeap = index;

        while (true) {
            int leftNode = leftChild(index);
            int rightNode = rightChild(index);

            // Verifica se o filho da esquerda existe e é MENOR que o atual mínimo
            if (leftNode < heap.size() && heap.get(leftNode).compareTo(heap.get(minHeap)) < 0) {
                minHeap = leftNode;
            }

            // Verifica se o filho da direita existe e é MENOR que o atual mínimo
            if (rightNode < heap.size() && heap.get(rightNode).compareTo(heap.get(minHeap)) < 0) {
                minHeap = rightNode;
            }

            if (minHeap!= index) {
                swap(minHeap, index);
                index = minHeap;
            } else {
                return;
            }
        }
    }
}