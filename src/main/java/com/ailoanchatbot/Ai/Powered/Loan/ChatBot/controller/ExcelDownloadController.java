package com.ailoanchatbot.Ai.Powered.Loan.ChatBot.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("download")
public class ExcelDownloadController {

    private static final String FILE_DIRECTORY = "C:/chatbot_excels/";

    @GetMapping("/{queryId}")
    public ResponseEntity<FileSystemResource> downloadExcel(@PathVariable String queryId) {
        File file = new File(FILE_DIRECTORY + queryId + ".xlsx");

        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + queryId + ".xlsx")
                .body(new FileSystemResource(file));
    }
}
