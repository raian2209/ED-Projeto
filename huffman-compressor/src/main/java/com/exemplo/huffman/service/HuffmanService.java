package com.exemplo.huffman.service;

import com.exemplo.huffman.util.BinaryIn;
import com.exemplo.huffman.util.BinaryOut;
import com.exemplo.huffman.util.MinHeap;
import com.exemplo.huffman.util.Node;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@Service
public class HuffmanService {

    private static final int PSEUDO_EOF = 256;

    public byte[] compress(byte[] inputData) throws IOException {

        // 1. Construir Tabela de Frequência
        int[] freqTable = buildFrequencyTable(inputData);

        // 2. Construir Árvore de Huffman
        Node root = buildHuffmanTree(freqTable);

        // 3. Construir Tabela de Códigos
        Map<Integer, String> lookupMap = buildCodeTable(root);

        // Agora preenche o array local usando o Mapa
        String[] localCodeTable = new String[257];
        for (Map.Entry<Integer, String> entry : lookupMap.entrySet()) {
            localCodeTable[entry.getKey()] = entry.getValue();
        }

        // 4. Stream de Saída
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BinaryOut bitOut = new BinaryOut(baos);

        // 5. Escrever Cabeçalho (os 257 valores da tabela de frequencia)
        for (int i = 0; i < freqTable.length; i++) {
            bitOut.write(freqTable[i]);
        }

        // 6. Escrever Corpo (bits dos códigos)
        for (byte b : inputData) {
            //  garantindo que não é nula
            String code = localCodeTable[b & 0xFF];

            for (char c : code.toCharArray()) {
                // true, escreve 1 bit
                // false, escreve 0 bit
                bitOut.write(c == '1');
            }
        }

        // 7. Escrever PSEUDO_EOF
        String eofCode = localCodeTable[PSEUDO_EOF];
        for (char c : eofCode.toCharArray()) {
            bitOut.write(c == '1');
        }

        // 8. Finalizar
        bitOut.close();

        return baos.toByteArray();
    }

    /**
     * DESCOMPRIME um array de bytes usando Huffman.
     */
    public byte[] decompress(byte[] compressedData) throws IOException {

        ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
        BinaryIn bitIn = new BinaryIn(bais);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // 1. Ler Cabeçalho (257 inteiros)
        int[] freqTable = new int[257];
        for (int i = 0; i < freqTable.length; i++) {
            freqTable[i] = bitIn.readInt();
        }

        // 2. Reconstruir Árvore
        Node root = buildHuffmanTree(freqTable);

        // 3. Decodificar bits
        while (true) {
            Node current = root;

            // Percorre a árvore conforme os bits
            while (!current.isLeaf()) {
                boolean bit = bitIn.readBoolean();
                current = bit ? current.right : current.left;
            }

            if (current.value == PSEUDO_EOF) {
                break; // fim!
            }

            baos.write(current.value);
        }

        bitIn.close();
        baos.close();
        return baos.toByteArray();
    }

    // ------------------------------------------------------------
    // 1) Construção da Tabela de Frequências
    // ------------------------------------------------------------
    public int[] buildFrequencyTable(byte[] data) {
        int[] freq = new int[257];  // 0-255 + PSEUDO_EOF

        for (byte b : data) {
            freq[b & 0xFF]++;
        }

        // Marca fim de arquivo
        freq[PSEUDO_EOF] = 1;

        return freq;
    }

    // ------------------------------------------------------------
    // 2) Construção da Árvore de Huffman
    // ------------------------------------------------------------
    public Node buildHuffmanTree(int[] freqTable) {
        MinHeap pq = new MinHeap();

        // Cria nós individuais para bytes com frequência > 0
        for (int i = 0; i < freqTable.length; i++) {
            if (freqTable[i] > 0) {
                pq.minHeapInsert(new Node(i, freqTable[i]));
            }
        }

        // Junta os dois menores até restar só um
        while (pq.size() > 1) {
            Node a = pq.minHeapRemove();
            Node b = pq.minHeapRemove();
            pq.minHeapInsert(new Node(a, b));  // nó interno
        }

        return pq.minHeapRemove(); // raiz
    }

    // ------------------------------------------------------------
    // 3) Gera tabela de códigos (mapa: byte → string "10110...")
    // ------------------------------------------------------------
    public Map<Integer, String> buildCodeTable(Node root) {
        Map<Integer, String> map = new HashMap<>();
        buildCodesRecursive(root, "", map);
        return map;
    }

    private void buildCodesRecursive(Node node, String path, Map<Integer, String> map) {
        if (node.isLeaf()) {
            map.put(node.value, path);
            return;
        }
        buildCodesRecursive(node.left,  path + "0", map);
        buildCodesRecursive(node.right, path + "1", map);
    }



}
