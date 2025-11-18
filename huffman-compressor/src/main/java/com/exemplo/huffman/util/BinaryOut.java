package com.exemplo.huffman.util;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * A classe BinaryOut fornece métodos para escrever
 * bits em um fluxo de saída binário.
 * (Fonte: https://algs4.cs.princeton.edu/code/edu/princeton/cs/algs4/BinaryOut.java.html)
 */
public final class BinaryOut {
    private BufferedOutputStream out; // the output stream
    private int buffer; // 8-bit buffer of bits to write out
    private int n; // number of bits remaining in buffer

    public BinaryOut() {
        out = new BufferedOutputStream(System.out);
    }

    public BinaryOut(OutputStream os) {
        out = new BufferedOutputStream(os);
    }

    public BinaryOut(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("filename argument is null");
        }
        if (filename.length() == 0) {
            throw new IllegalArgumentException("filename argument is the empty string");
        }
        try {
            OutputStream os = new FileOutputStream(filename);
            out = new BufferedOutputStream(os);
        }
        catch (IOException e) {
            throw new IllegalArgumentException("could not create file '" + filename + "' for writing", e);
        }
    }

    public BinaryOut(Socket socket) {
        if (socket == null) {
            throw new IllegalArgumentException("socket argument is null");
        }
        try {
            OutputStream os = socket.getOutputStream();
            out = new BufferedOutputStream(os);
        }
        catch (IOException e) {
            throw new IllegalArgumentException("could not create output stream from socket", e);
        }
    }

    private void writeBit(boolean x) {
        buffer <<= 1;
        if (x) buffer |= 1;
        n++;
        if (n == 8) clearBuffer();
    }

    private void writeByte(int x) {
        assert x >= 0 && x < 256;
        if (n == 0) {
            try {
                out.write(x);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        for (int i = 0; i < 8; i++) {
            boolean bit = ((x >>> (8 - i - 1)) & 1) == 1;
            writeBit(bit);
        }
    }

    private void clearBuffer() {
        if (n == 0) return;
        if (n > 0) buffer <<= (8 - n);
        try {
            out.write(buffer);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        n = 0;
        buffer = 0;
    }

    public void flush() {
        clearBuffer();
        try {
            out.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        flush();
        try {
            out.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(boolean x) {
        writeBit(x);
    }

    public void write(byte x) {
        writeByte(x & 0xff);
    }

    public void write(int x) {
        writeByte((x >>> 24) & 0xff);
        writeByte((x >>> 16) & 0xff);
        writeByte((x >>> 8) & 0xff);
        writeByte((x >>> 0) & 0xff);
    }

    public void write(long x) {
        writeByte((int) ((x >>> 56) & 0xff));
        writeByte((int) ((x >>> 48) & 0xff));
        writeByte((int) ((x >>> 40) & 0xff));
        writeByte((int) ((x >>> 32) & 0xff));
        writeByte((int) ((x >>> 24) & 0xff));
        writeByte((int) ((x >>> 16) & 0xff));
        writeByte((int) ((x >>> 8) & 0xff));
        writeByte((int) ((x >>> 0) & 0xff));
    }

    public void write(char x) {
        if (x >= 256) throw new IllegalArgumentException("Illegal 8-bit char = " + x);
        writeByte(x);
    }
}