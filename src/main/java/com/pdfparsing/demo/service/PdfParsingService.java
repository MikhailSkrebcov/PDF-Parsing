package com.pdfparsing.demo.service;

import com.pdfparsing.demo.dto.*;
import com.pdfparsing.demo.entity.PdfParsingEntity;
import com.pdfparsing.demo.exception.PdfProcessingException;
import com.pdfparsing.demo.repository.PdfParsingRepository;
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

    public PdfParsingDto parseAndSavePdf(MultipartFile file) throws IOException {
        try {
            PdfParsingDto parsedData = parsePdfFile(file);
            saveParsingResult(parsedData);
            return parsedData;
        } catch (Exception e) {
            throw new PdfProcessingException("Ошибка обработки PDF", e);
        }
    }

    private PdfParsingDto parsePdfFile(MultipartFile file) throws IOException {
        log.info("Начало обработки PDF файла: {}", file.getOriginalFilename());

        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            log.debug("Текст из PDF:\n{}", text);

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

        log.info("Успешно распарсены данные: {}", dto);
        return dto;
    }

    private TaxAgentInfoDto parseTaxAgentInfo(String text) {
        TaxAgentInfoDto info = new TaxAgentInfoDto();
        info.setOkato(extractValue(text, "Код по ОКТМО", "Телефон"));
        info.setPhone(extractValue(text, "Телефон", "ИНН"));
        info.setInn(extractValue(text, "ИНН", "КПП"));
        info.setKpp(extractValue(text, "КПП", "Налоговый агент"));
        info.setName(extractValue(text, "Налоговый агент", "Форма реорганизации"));
        return info;
    }

    private TaxPayerInfoDto parseTaxPayerInfo(String text) {
        TaxPayerInfoDto info = new TaxPayerInfoDto();
        info.setInn(extractValue(text, "ИНН в Российской Федерации", "Фамилия"));
        info.setLastName(extractValue(text, "Фамилия", "Имя"));
        info.setFirstName(extractValue(text, "Имя", "Отчество"));
        info.setMiddleName(extractValue(text, "Отчество", "Статус"));

        String status = extractValue(text, "Статус налогоплательщика", "Дата рождения");
        info.setTaxpayerStatus(status != null ? Integer.parseInt(status) : 0);

        String birthDate = extractValue(text, "Дата рождения", "Гражданство");
        info.setBirthDate(birthDate != null ? parseDate(birthDate) : null);

        info.setCitizenshipCode(extractValue(text, "Гражданство \\(код страны\\)", "Код документа"));
        info.setDocumentCode(extractValue(text, "Код документа, удостоверяющего личность", "Серия и номер"));
        info.setDocumentNumber(extractValue(text, "Серия и номер документа", "3. Доходы"));
        return info;
    }

    private void parseDocumentDates(String text, PdfParsingDto dto) {
        String dateStr = extractValue(text, "за _\\d{4}_ год от _\\d{2}_\\. _\\d{2}_\\. _\\d{4}_", "1. Данные");
        dto.setDocumentDate(dateStr != null ? parseDate(dateStr.replaceAll("_", "")) : null);

        String yearStr = extractValue(text, "за _(\\d{4})_ год", "от");
        dto.setYear(yearStr != null ? Integer.parseInt(yearStr) : 0);
    }

    private List<IncomeInfoDto> parseIncomes(String text) {
        List<IncomeInfoDto> incomes = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\|(\\d{2})\\|(\\d{4})\\|([\\d.,]+)\\|(\\d{3})?\\|([\\d.,]+)?\\|");
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
        summary.setTotalIncome(parseBigDecimal(extractValue(text, "Общая сумма дохода\\|([\\d.,]+)", "\\|")));
        summary.setTaxBase(parseBigDecimal(extractValue(text, "Налоговая база\\|([\\d.,]+)", "\\|")));
        summary.setCalculatedTax(parseBigDecimal(extractValue(text, "Сумма налога исчисленная\\|(\\d+)", "\\|")));
        summary.setTransferredTax(parseBigDecimal(extractValue(text, "Сумма налога перечисленная\\|(\\d+)", "\\|")));
        summary.setWithheldTax(parseBigDecimal(extractValue(text, "Сумма налога удержанная\\|(\\d+)", "\\|")));
        summary.setOverpaidTax(parseBigDecimal(extractValue(text, "Сумма налога, излишне удержанная\\|(\\d+)", "\\|")));
        return summary;
    }

    private void saveParsingResult(PdfParsingDto dto) {
        PdfParsingEntity entity = new PdfParsingEntity();
        entity.setTaxpayerInn(dto.getTaxPayerInfo().getInn());
        entity.setTaxpayerName(formatTaxpayerName(dto.getTaxPayerInfo()));
        entity.setTaxAgentName(dto.getTaxAgentInfo().getName());
        entity.setTotalIncome(dto.getTaxSummary().getTotalIncome());
        entity.setTaxAmount(dto.getTaxSummary().getCalculatedTax());
        entity.setDocumentDate(dto.getDocumentDate());
        entity.setYear(dto.getYear());
        entity.setParsedAt(LocalDateTime.now());

        pdfParsingRepository.save(entity);
        log.info("Данные сохранены в БД: {}", entity);
    }

    private String formatTaxpayerName(TaxPayerInfoDto info) {
        return String.format("%s %s %s",
                info.getLastName(),
                info.getFirstName(),
                info.getMiddleName()).trim();
    }

    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(value.replace(",", "."));
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        String[] parts = dateStr.split("\\.");
        return LocalDate.of(
                Integer.parseInt(parts[2].trim()),
                Integer.parseInt(parts[1].trim()),
                Integer.parseInt(parts[0].trim())
        );
    }

    private String extractValue(String text, String startRegex, String endRegex) {
        Pattern pattern = Pattern.compile(startRegex + "([\\s\\S]*?)" + endRegex);
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group(1).trim() : null;
    }

    public List<PdfParsingEntity> getAllResults() {
        return pdfParsingRepository.findAll();
    }
}