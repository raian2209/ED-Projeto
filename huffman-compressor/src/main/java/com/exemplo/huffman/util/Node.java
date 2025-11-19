package com.exemplo.huffman.util;

/*
 * Classe interna representando um nó na Árvore de Huffman.
 * Implementa Comparable para uso direto em uma PriorityQueue.
 * Esta é uma Estrutura Encadeada por definição.
 */
 public class HuffmanNode implements Comparable<HuffmanNode> {
    // O símbolo (0-255 para bytes, 256 para PSEUDO_EOF)
    private final int symbol;
    // A frequência (peso) deste nó
    private final int frequency;

    // As referências da Estrutura Encadeada
    private final HuffmanNode left, right;

    /**
     * Construtor para nós FOLHA (contêm um símbolo).
     */
    public HuffmanNode(int symbol, int frequency) {
        this.symbol = symbol;
        this.frequency = frequency;
        this.left = null;
        this.right = null;
    }

    /**
     * Construtor para nós INTERNOS (contêm outros nós).
     */
    public HuffmanNode(HuffmanNode left, HuffmanNode right) {
        this.symbol = -1; // -1 indica um nó interno
        this.frequency = left.frequency + right.frequency;
        this.left = left;
        this.right = right;
    }

    /**
     * Verifica se este nó é uma folha (não tem filhos).
     * @return true se for um nó folha, false caso contrário.
     */
    public boolean isLeaf() {
        // Conforme [47]
        return this.left == null && this.right == null;
    }

    /**
     * Método de comparação para a PriorityQueue.
     * Nós com menor frequência têm maior prioridade.
     */
    @Override
    public int compareTo(HuffmanNode other) {
        // Conforme
        return Integer.compare(this.frequency, other.frequency);
    }
}