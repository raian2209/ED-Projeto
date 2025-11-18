package com.exemplo.huffman.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.NoSuchElementException;

/**
 * A classe BinaryIn fornece métodos para ler
 * bits de um fluxo de entrada binário.
 * (Fonte: https://algs4.cs.princeton.edu/code/edu/princeton/cs/algs4/BinaryIn.java.html)
 */
public final class BinaryIn {

    private static final int EOF = -1; // end of file
    private BufferedInputStream in; // the input stream
    private int buffer; // one character buffer
    private int n; // number of bits left in buffer

    public BinaryIn() {
        in = new BufferedInputStream(System.in);
        fillBuffer();
    }

    public BinaryIn(InputStream is) {
        in = new BufferedInputStream(is);
        fillBuffer();
    }

    public BinaryIn(Socket socket) {
        try {
            InputStream is = socket.getInputStream();
            in = new BufferedInputStream(is);
            fillBuffer();
        }
        catch (IOException ioe) {
            System.err.println("could not read socket: " + socket);
        }
    }

    public BinaryIn(URL url) {
        try {
            URLConnection site = url.openConnection();
            InputStream is = site.getInputStream();
            in = new BufferedInputStream(is);
            fillBuffer();
        }
        catch (IOException ioe) {
            System.err.println("could not open URL: '" + url + "'");
        }
    }

    public BinaryIn(String name) {
        try {
            File file = new File(name);
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                in = new BufferedInputStream(fis);
                fillBuffer();
                return;
            }

            URL url = getClass().getResource(name);

            if (url == null) {
                URI uri = new URI(name);
                if (uri.isAbsolute()) url = uri.toURL();
                else throw new IllegalArgumentException("could not read: '" + name+ "'");
            }

            URLConnection site = url.openConnection();
            InputStream is = site.getInputStream();
            in = new BufferedInputStream(is);
            fillBuffer();
        }
        catch (IOException | URISyntaxException e) {
            System.err.println("could not open: '" + name + "'");
        }
    }

    private void fillBuffer() {
        try {
            buffer = in.read();
            n = 8;
        }
        catch (IOException e) {
            System.err.println("EOF");
            buffer = EOF;
            n = -1;
        }
    }

    public boolean exists() {
        return in!= null;
    }

    public boolean isEmpty() {
        return buffer == EOF;
    }

    public boolean readBoolean() {
        if (isEmpty()) throw new NoSuchElementException("Reading from empty input stream");
        n--;
        boolean bit = ((buffer >> n) & 1) == 1;
        if (n == 0) fillBuffer();
        return bit;
    }

    public char readChar() {
        if (isEmpty()) throw new NoSuchElementException("Reading from empty input stream");

        if (n == 8) {
            int x = buffer;
            fillBuffer();
            return (char) (x & 0xff);
        }

        int x = buffer;
        x <<= (8 - n);
        int oldN = n;
        fillBuffer();
        if (isEmpty()) throw new NoSuchElementException("Reading from empty input stream");
        n = oldN;
        x |= (buffer >>> n);
        return (char) (x & 0xff);
    }

    public int readInt() {
        int x = 0;
        for (int i = 0; i < 4; i++) {
            char c = readChar();
            x <<= 8;
            x |= c;
        }
        return x;
    }

    public long readLong() {
        long x = 0;
        for (int i = 0; i < 8; i++) {
            char c = readChar();
            x <<= 8;
            x |= c;
        }
        return x;
    }

    public void close() {
        try {
            in.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not close BinaryIn");
        }
    }
}