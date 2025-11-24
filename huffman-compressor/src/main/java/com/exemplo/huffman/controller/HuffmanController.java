package com.exemplo.huffman.controller;


import com.exemplo.huffman.service.HuffmanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/huffman")
@CrossOrigin("*")
public class HuffmanController {

    @Autowired
    private HuffmanService huffmanService;

    @PostMapping(value = "/compress", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> compress(@RequestParam("file") MultipartFile file) throws IOException {
        byte[] originalData = file.getBytes();

        byte[] compressedData = huffmanService.compress(originalData);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getOriginalFilename() + ".huff")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(compressedData);
    }

    @PostMapping(value = "/decompress", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> decompress(@RequestParam("file") MultipartFile file) throws IOException {
        byte[] compressedData = file.getBytes();

        byte[] decompressedData = huffmanService.decompress(compressedData);

        // Tenta remover a extensão .huff para o nome original, se possível
        String originalName = file.getOriginalFilename().replace(".huff", "");

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + originalName)
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(decompressedData);
    }
}