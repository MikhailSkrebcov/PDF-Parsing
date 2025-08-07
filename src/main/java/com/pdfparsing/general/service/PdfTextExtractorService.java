package com.pdfparsing.general.service;

import com.pdfparsing.general.constants.PdfConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class PdfTextExtractorService {

    public String extractTextFromPdf(MultipartFile file) throws IOException {
        log.info(PdfConstants.LOG_START_PDF_PROCESSING, file.getOriginalFilename());

        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            log.debug(PdfConstants.LOG_EXTRACTED_TEXT, text.substring(0, Math.min(text.length(), 500)) + "...");

            return text;
        }
    }
}
