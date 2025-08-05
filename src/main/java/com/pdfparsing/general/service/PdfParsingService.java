package com.pdfparsing.general.service;

import com.pdfparsing.general.constants.PdfConstants;
import com.pdfparsing.general.dto.*;
import com.pdfparsing.general.entity.PdfParsingEntity;
import com.pdfparsing.general.exception.PdfProcessingException;
import com.pdfparsing.general.repository.PdfParsingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfParsingService {

    private final PdfParsingRepository pdfParsingRepository;

    public PdfParsingDto parseAndSavePdf(MultipartFile file) {
        try {
            PdfParsingDto parsedData = parsePdfFile(file);
            saveParsingResult(parsedData);

            return parsedData;

        } catch (Exception e) {
            log.error(PdfConstants.LOG_PDF_PROCESSING_ERROR, file.getOriginalFilename(), e);
            throw new PdfProcessingException(PdfConstants.ERROR_PDF_PROCESSING, e.getMessage());        }
    }

    public List<PdfParsingEntity> getAllResults() {

        return pdfParsingRepository.findAll();
    }

    private PdfParsingDto parsePdfFile(MultipartFile file) throws IOException {

        log.info(PdfConstants.LOG_START_PDF_PROCESSING, file.getOriginalFilename());

        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            log.debug(PdfConstants.LOG_EXTRACTED_TEXT, text.substring(0, Math.min(text.length(), 500)) + "...");

            return parseExtractedText(text);
        }
    }

    private PdfParsingDto parseExtractedText(String text) {

        PdfParsingDto dto = new PdfParsingDto();

        dto.setTaxAgentInfo(parseTaxAgentInfo(text));
        dto.setTaxPayerInfo(parseTaxPayerInfo(text));
        parseDocumentDates(text, dto);
        dto.setIncomes(parseIncomes(text));
        dto.setTaxSummary(parseTaxSummary(text));

        log.info(PdfConstants.LOG_SUCCESS_PARSE, dto.getTaxPayerInfo().getInn());

        return dto;
    }

    private TaxAgentInfoDto parseTaxAgentInfo(String text) {

        TaxAgentInfoDto info = new TaxAgentInfoDto();

        info.setOkato(extractValue(text, PdfConstants.OKATO_FIELD, PdfConstants.PHONE_FIELD));
        info.setPhone(extractValue(text, PdfConstants.PHONE_FIELD, PdfConstants.INN_FIELD));
        info.setInn(extractValue(text, PdfConstants.INN_FIELD, PdfConstants.KPP_FIELD));
        info.setKpp(extractValue(text, PdfConstants.KPP_FIELD, PdfConstants.TAX_AGENT_FIELD));
        info.setName(extractValue(text, PdfConstants.TAX_AGENT_FIELD, PdfConstants.REORGANIZATION_FORM_FIELD));

        return info;
    }

    private TaxPayerInfoDto parseTaxPayerInfo(String text) {

        TaxPayerInfoDto info = new TaxPayerInfoDto();

        info.setInn(extractValue(text, PdfConstants.RUSSIAN_INN_FIELD, PdfConstants.LAST_NAME_FIELD));
        info.setLastName(extractValue(text, PdfConstants.LAST_NAME_FIELD, PdfConstants.FIRST_NAME_FIELD));
        info.setFirstName(extractValue(text, PdfConstants.FIRST_NAME_FIELD, PdfConstants.MIDDLE_NAME_FIELD));
        info.setMiddleName(extractValue(text, PdfConstants.MIDDLE_NAME_FIELD, PdfConstants.TAXPAYER_STATUS_FIELD));

        String status = extractValue(text, PdfConstants.TAXPAYER_STATUS_FIELD, PdfConstants.BIRTH_DATE_FIELD);
        info.setTaxpayerStatus(status != null && !status.isEmpty() ? Integer.parseInt(status) : 0);

        String birthDate = extractValue(text, PdfConstants.BIRTH_DATE_FIELD, PdfConstants.CITIZENSHIP_FIELD);
        info.setBirthDate(parseDate(birthDate));

        info.setCitizenshipCode(extractValue(text, PdfConstants.CITIZENSHIP_FIELD, PdfConstants.DOCUMENT_CODE_FIELD));
        info.setDocumentCode(extractValue(text, PdfConstants.DOCUMENT_CODE_FIELD, PdfConstants.DOCUMENT_NUMBER_FIELD));
        info.setDocumentNumber(extractValue(text, PdfConstants.DOCUMENT_NUMBER_FIELD, PdfConstants.INCOME_SECTION_HEADER));

        return info;
    }

    private void parseDocumentDates(String text, PdfParsingDto dto) {

        String dateStr = extractValue(text, PdfConstants.DOCUMENT_DATE_PATTERN, PdfConstants.AGENT_SECTION_HEADER);
        dto.setDocumentDate(dateStr != null ? parseDate(dateStr.replaceAll(PdfConstants.UNDERSCORE_PATTERN, "")) : null);

        String yearStr = extractValue(text, PdfConstants.YEAR_PATTERN, PdfConstants.DATE_FROM_TEXT);
        dto.setYear(yearStr != null && !yearStr.isEmpty() ? Integer.parseInt(yearStr) : null);
    }

    private List<IncomeInfoDto> parseIncomes(String text) {

        List<IncomeInfoDto> incomes = new ArrayList<>();
        Pattern pattern = Pattern.compile(PdfConstants.INCOME_TABLE_PATTERN);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            IncomeInfoDto income = new IncomeInfoDto();
            income.setMonth(matcher.group(1));
            income.setIncomeCode(matcher.group(2));
            income.setIncomeAmount(parseBigDecimal(matcher.group(3)));

            if (matcher.group(4) != null) {
                income.setDeductionCode(matcher.group(4));
                income.setDeductionAmount(parseBigDecimal(matcher.group(5)));
            }
            incomes.add(income);
        }

        return incomes;
    }

    private TaxSummaryDto parseTaxSummary(String text) {

        TaxSummaryDto summary = new TaxSummaryDto();

        summary.setTotalIncome(parseBigDecimal(extractValue(text, PdfConstants.TOTAL_INCOME_PATTERN, PdfConstants.TABLE_COLUMN_SEPARATOR)));
        summary.setTaxBase(parseBigDecimal(extractValue(text, PdfConstants.TAX_BASE_PATTERN, PdfConstants.TABLE_COLUMN_SEPARATOR)));
        summary.setCalculatedTax(parseBigDecimal(extractValue(text, PdfConstants.CALCULATED_TAX_PATTERN, PdfConstants.TABLE_COLUMN_SEPARATOR)));
        summary.setTransferredTax(parseBigDecimal(extractValue(text, PdfConstants.TRANSFERRED_TAX_PATTERN, PdfConstants.TABLE_COLUMN_SEPARATOR)));
        summary.setWithheldTax(parseBigDecimal(extractValue(text, PdfConstants.WITHHELD_TAX_PATTERN, PdfConstants.TABLE_COLUMN_SEPARATOR)));
        summary.setOverpaidTax(parseBigDecimal(extractValue(text, PdfConstants.OVERPAID_TAX_PATTERN, PdfConstants.TABLE_COLUMN_SEPARATOR)));

        return summary;
    }

    private void saveParsingResult(PdfParsingDto dto) {

        PdfParsingEntity entity = new PdfParsingEntity();
        entity.setTaxpayerInn(dto.getTaxPayerInfo().getInn());
        entity.setTaxpayerName(formatName(
                dto.getTaxPayerInfo().getLastName(),
                dto.getTaxPayerInfo().getFirstName(),
                dto.getTaxPayerInfo().getMiddleName()
        ));
        entity.setTaxAgentName(dto.getTaxAgentInfo().getName());
        entity.setTotalIncome(dto.getTaxSummary().getTotalIncome());
        entity.setTaxAmount(dto.getTaxSummary().getCalculatedTax());
        entity.setDocumentDate(dto.getDocumentDate());
        entity.setYear(dto.getYear());
        entity.setParsedAt(LocalDateTime.now());

        pdfParsingRepository.save(entity);
        log.info(PdfConstants.LOG_SAVED_TO_DB, entity.getId());
    }


    private String extractValue(String text, String startRegex, String endRegex) {

        if (text == null) return null;
        Pattern pattern = Pattern.compile(startRegex + PdfConstants.TEXT_EXTRACTION_PATTERN + endRegex);
        Matcher matcher = pattern.matcher(text);

        return matcher.find() ? matcher.group(1).trim() : null;
    }

    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.isEmpty()) {

            return BigDecimal.ZERO;
        }

        return new BigDecimal(value.replace(PdfConstants.DECIMAL_COMMA, PdfConstants.DECIMAL_POINT));
    }

    private LocalDate parseDate(String dateStr) {

        if (dateStr == null || dateStr.isEmpty()) return null;
        String[] parts = dateStr.split(PdfConstants.DATE_SEPARATOR);

        return LocalDate.of(
                Integer.parseInt(parts[2].trim()),
                Integer.parseInt(parts[1].trim()),
                Integer.parseInt(parts[0].trim())
        );
    }

    private String formatName(String lastName, String firstName, String middleName) {

        return String.join(PdfConstants.NAME_SEPARATOR, lastName, firstName, middleName).trim();
    }
}
