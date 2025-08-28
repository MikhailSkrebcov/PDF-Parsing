package com.pdfparsing.general.service;

import com.pdfparsing.general.constants.PdfConstants;
import com.pdfparsing.general.dto.PdfParsingDto;
import com.pdfparsing.general.dto.PersonalInfoDto;
import com.pdfparsing.general.entity.PersonalInfoEntity;
import com.pdfparsing.general.exception.PdfProcessingException;
import com.pdfparsing.general.mapper.ModelMapperConfig;
import com.pdfparsing.general.repository.PdfParsingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfParsingExtractService {

    private final PdfParsingRepository pdfParsingRepository;
    private final PdfDataService pdfDataParserService;
    private final PdfDataService pdfDataSaverService;
    private final ModelMapper modelMapper;

    public PdfParsingDto parseAndSavePdf(MultipartFile file) {
        try {
            String text = extractTextFromPdf(file);
            PdfParsingDto parsedData = pdfDataParserService.parse(text);
            pdfDataSaverService.save(parsedData);

            return parsedData;
        } catch (Exception e) {
            log.error(PdfConstants.LOG_PDF_PROCESSING_ERROR, file.getOriginalFilename(), e);
            throw new PdfProcessingException(PdfConstants.ERROR_PDF_PROCESSING, e.getMessage());
        }
    }

    public List<PersonalInfoDto> getAllResults() {
        List<PersonalInfoEntity> entities = pdfParsingRepository.findAll();
        return entities.stream()
                .map(entity -> modelMapper.map(entity, PersonalInfoDto.class))
                .collect(Collectors.toList());
    }

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
