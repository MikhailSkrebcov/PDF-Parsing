package com.pdfparsing.general.controller;

import com.pdfparsing.general.dto.PdfParsingDto;
import com.pdfparsing.general.entity.PersonalInfoEntity;
import com.pdfparsing.general.service.PdfParsingExtractService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController("/pdf")
@RequiredArgsConstructor
public class PdfParsingController {

    private final PdfParsingExtractService pdfParsingService;

    @PostMapping("/pdf/parse")
    public PdfParsingDto parsePdf(@RequestParam("file") MultipartFile file) {
        return pdfParsingService.parseAndSavePdf(file);
    }

    @GetMapping("/pdf/results")
    public List<PersonalInfoEntity> getAllResults() {
        return pdfParsingService.getAllResults();
    }
}
