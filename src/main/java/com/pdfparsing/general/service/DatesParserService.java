package com.pdfparsing.general.service;

import com.pdfparsing.general.constants.PdfConstants;
import com.pdfparsing.general.dto.PdfParsingDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DatesParserService {

    private final TextParserUtilsService textParserUtilsService;

    public void parse(String text, PdfParsingDto dto) {
        String dateStr = textParserUtilsService.extractValue(text, PdfConstants.DOCUMENT_DATE_PATTERN, PdfConstants.AGENT_SECTION_HEADER);
        dto.setDocumentDate(dateStr != null ?
                textParserUtilsService.parseDate(dateStr.replaceAll(PdfConstants.UNDERSCORE_PATTERN, "")) :
                null);

        String yearStr = textParserUtilsService.extractValue(text, PdfConstants.YEAR_PATTERN, PdfConstants.DATE_PREFIX);
        dto.setYear(yearStr != null && !yearStr.isEmpty() ? Integer.parseInt(yearStr) : null);
    }
}
