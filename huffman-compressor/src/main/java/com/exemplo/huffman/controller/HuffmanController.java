package com.exemplo.huffman.controller;


import com.exemplo.huffman.service.HuffmanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/huffman")
public class HuffmanController {

    @Autowired
    private HuffmanService huffmanService;

    @PostMapping("/compress")
    public byte[] compress(@RequestBody byte[] data) throws IOException {
        return huffmanService.compress(data);
    }

    @PostMapping("/decompress")
    public byte[] decompress(@RequestBody byte[] data) throws IOException {
        return huffmanService.decompress(data);
    }
}