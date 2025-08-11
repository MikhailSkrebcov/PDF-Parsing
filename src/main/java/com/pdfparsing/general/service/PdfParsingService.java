package com.pdfparsing.general.service;

import com.pdfparsing.general.constants.PdfConstants;
import com.pdfparsing.general.dto.PdfParsingDto;
import com.pdfparsing.general.entity.PersonalInfoEntity;
import com.pdfparsing.general.exception.PdfProcessingException;
import com.pdfparsing.general.repository.PdfParsingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfParsingService {
    private final PdfParsingRepository pdfParsingRepository;
    private final PdfTextExtractorService pdfTextExtractorService;
    private final PdfDataParserService pdfDataParserService;
    private final PdfDataSaverService pdfDataSaverService;

    public PdfParsingDto parseAndSavePdf(MultipartFile file) {

        try {
            String text = pdfTextExtractorService.extractTextFromPdf(file);
            PdfParsingDto parsedData = pdfDataParserService.parse(text);
            pdfDataSaverService.save(parsedData);

            return parsedData;

        } catch (Exception e) {
            log.error(PdfConstants.LOG_PDF_PROCESSING_ERROR, file.getOriginalFilename(), e);
            throw new PdfProcessingException(PdfConstants.ERROR_PDF_PROCESSING, e.getMessage());
        }
    }

    public List<PersonalInfoEntity> getAllResults() {
        return pdfParsingRepository.findAll();
    }
}
