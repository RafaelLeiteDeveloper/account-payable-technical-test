package com.desafio.account.payable.interfaces.controller;

import com.desafio.account.payable.application.service.AccountImportService;
import com.desafio.account.payable.application.service.QueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/v1/account/import")
@RequiredArgsConstructor
public class AccountImportController {

    private final QueueService queueService;

    @PostMapping
    public ResponseEntity<Void> importAccounts(@RequestParam("fileType") String fileType, @RequestParam("file") MultipartFile file) throws IOException {
        log.info("Received request to import accounts with file type: {}", fileType);
        log.info("Processing file of type: {} with size: {} bytes", fileType, file.getSize());

        long maxFileSize = (long) 1024 * 1024 * 1024;

        if (file.getSize() > maxFileSize) {
            log.error("File size exceeds 1GB. File size: {} bytes", file.getSize());
            throw new IllegalArgumentException("File size exceeds the maximum allowed limit of 1GB.");
        }

        queueService.sendToQueue(fileType, file.getInputStream());
        log.info("File successfully sent to queue for processing.");

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
