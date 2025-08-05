package com.pdfparsing.general.controller;

import com.pdfparsing.general.dto.PdfParsingDto;
import com.pdfparsing.general.entity.PdfParsingEntity;
import com.pdfparsing.general.service.PdfParsingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/pdf")
@RequiredArgsConstructor
public class PdfParsingController {

    private final PdfParsingService pdfParsingService;

    @PostMapping("/parse")
    public ResponseEntity<PdfParsingDto> parsePdf(@RequestParam("file") MultipartFile file) throws IOException {
        PdfParsingDto result = pdfParsingService.parseAndSavePdf(file);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/results")
    public ResponseEntity<List<PdfParsingEntity>> getAllResults() {

        return ResponseEntity.ok(pdfParsingService.getAllResults());
    }
}