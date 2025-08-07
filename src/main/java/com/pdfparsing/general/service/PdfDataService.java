package com.pdfparsing.general.service;

import com.pdfparsing.general.constants.PdfConstants;
import com.pdfparsing.general.dto.PdfParsingDto;
import com.pdfparsing.general.entity.PersonalInfoEntity;
import com.pdfparsing.general.repository.PdfParsingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfDataService {

    private final PdfParsingRepository pdfParsingRepository;
    private final PdfContentParserService combinedPdfParserService;
    private final TextParserUtilsService textParserUtilsService;

    public PdfParsingDto parse(String text) {
        PdfParsingDto dto = new PdfParsingDto();

        dto.setTaxAgentInfo(combinedPdfParserService.parseTaxAgentInfo(text));
        dto.setPersonalInfoDto(combinedPdfParserService.parsePersonalInfo(text));
        parseDates(text, dto);
        dto.setIncomes(combinedPdfParserService.parseIncomes(text));
        dto.setTaxSummary(combinedPdfParserService.parseTaxSummary(text));

        log.info(PdfConstants.LOG_SUCCESS_PARSE, dto.getPersonalInfoDto().getInn());
        return dto;
    }

    public void save(PdfParsingDto dto) {
        try {
            PersonalInfoEntity entity = convertToEntity(dto);
            pdfParsingRepository.save(entity);
            log.info(PdfConstants.LOG_SAVED_TO_DB, entity.getId());
        } catch (Exception e) {
            log.error("Ошибка при сохранении данных в БД: {}", e.getMessage());
            throw e;
        }
    }

    private void parseDates(String text, PdfParsingDto dto) {
        String dateStr = textParserUtilsService.extractValue(text, PdfConstants.DOCUMENT_DATE_PATTERN, PdfConstants.AGENT_SECTION_HEADER);
        dto.setDocumentDate(dateStr != null ?
                textParserUtilsService.parseDate(dateStr.replaceAll(PdfConstants.UNDERSCORE_PATTERN, "")) :
                null);

        String yearStr = textParserUtilsService.extractValue(text, PdfConstants.YEAR_PATTERN, PdfConstants.DATE_PREFIX);
        dto.setYear(yearStr != null && !yearStr.isEmpty() ? Integer.parseInt(yearStr) : null);
    }

    private PersonalInfoEntity convertToEntity(PdfParsingDto dto) {
        PersonalInfoEntity entity = new PersonalInfoEntity();

        if (dto.getPersonalInfoDto() != null) {
            entity.setPersonInn(dto.getPersonalInfoDto().getInn());
            entity.setPersonName(textParserUtilsService.formatName(
                    dto.getPersonalInfoDto().getLastName(),
                    dto.getPersonalInfoDto().getFirstName(),
                    dto.getPersonalInfoDto().getMiddleName()
            ));
        }

        if (dto.getTaxAgentInfo() != null) {
            entity.setTaxAgentName(dto.getTaxAgentInfo().getName());
        }

        if (dto.getTaxSummary() != null) {
            entity.setTotalIncome(dto.getTaxSummary().getTotalIncome());
            entity.setTaxAmount(dto.getTaxSummary().getCalculatedTax());
        }

        entity.setDocumentDate(dto.getDocumentDate());
        entity.setYear(dto.getYear());
        entity.setParsedAt(LocalDateTime.now());

        return entity;
    }
}
