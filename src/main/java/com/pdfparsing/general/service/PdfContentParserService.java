package com.pdfparsing.general.service;

import com.pdfparsing.general.constants.PdfConstants;
import com.pdfparsing.general.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class PdfContentParserService {

    private final TextParserUtilsService textParserUtilsService;

    public TaxSummaryDto parseTaxSummary(String text) {
        TaxSummaryDto summary = new TaxSummaryDto();

        summary.setTotalIncome(parseBigDecimalField(text, PdfConstants.TOTAL_INCOME_PATTERN));
        summary.setTaxBase(parseBigDecimalField(text, PdfConstants.TAX_BASE_PATTERN));
        summary.setCalculatedTax(parseBigDecimalField(text, PdfConstants.CALCULATED_TAX_PATTERN));
        summary.setTransferredTax(parseBigDecimalField(text, PdfConstants.TRANSFERRED_TAX_PATTERN));
        summary.setWithheldTax(parseBigDecimalField(text, PdfConstants.WITHHELD_TAX_PATTERN));
        summary.setOverpaidTax(parseBigDecimalField(text, PdfConstants.OVERPAID_TAX_PATTERN));

        return summary;
    }

    public TaxAgentInfoDto parseTaxAgentInfo(String text) {
        TaxAgentInfoDto info = new TaxAgentInfoDto();

        info.setOktmo(textParserUtilsService.extractValue(text, PdfConstants.OKTMO_FIELD, PdfConstants.PHONE_FIELD));
        info.setPhone(textParserUtilsService.extractValue(text, PdfConstants.PHONE_FIELD, PdfConstants.INN_FIELD));
        info.setInn(textParserUtilsService.extractValue(text, PdfConstants.INN_FIELD, PdfConstants.KPP_FIELD));
        info.setKpp(textParserUtilsService.extractValue(text, PdfConstants.KPP_FIELD, PdfConstants.TAX_AGENT_FIELD));
        info.setName(textParserUtilsService.extractValue(text, PdfConstants.TAX_AGENT_FIELD, PdfConstants.REORGANIZATION_FORM_FIELD));

        return info;
    }

    public PersonalInfoDto parsePersonalInfo(String text) {
        PersonalInfoDto info = new PersonalInfoDto();

        info.setInn(textParserUtilsService.extractValue(text, PdfConstants.RUSSIAN_INN_FIELD, PdfConstants.LAST_NAME_FIELD));
        info.setLastName(textParserUtilsService.extractValue(text, PdfConstants.LAST_NAME_FIELD, PdfConstants.FIRST_NAME_FIELD));
        info.setFirstName(textParserUtilsService.extractValue(text, PdfConstants.FIRST_NAME_FIELD, PdfConstants.MIDDLE_NAME_FIELD));
        info.setMiddleName(textParserUtilsService.extractValue(text, PdfConstants.MIDDLE_NAME_FIELD, PdfConstants.TAXPAYER_STATUS_FIELD));

        String status = textParserUtilsService.extractValue(text, PdfConstants.TAXPAYER_STATUS_FIELD, PdfConstants.BIRTH_DATE_FIELD);
        info.setTaxpayerStatus(status != null && !status.isEmpty() ? Integer.parseInt(status) : 0);

        String birthDate = textParserUtilsService.extractValue(text, PdfConstants.BIRTH_DATE_FIELD, PdfConstants.CITIZENSHIP_FIELD);
        info.setBirthDate(textParserUtilsService.parseDate(birthDate));

        info.setCitizenshipCode(textParserUtilsService.extractValue(text, PdfConstants.CITIZENSHIP_FIELD, PdfConstants.DOCUMENT_CODE_FIELD));
        info.setDocumentCode(textParserUtilsService.extractValue(text, PdfConstants.DOCUMENT_CODE_FIELD, PdfConstants.DOCUMENT_NUMBER_FIELD));
        info.setDocumentNumber(textParserUtilsService.extractValue(text, PdfConstants.DOCUMENT_NUMBER_FIELD, PdfConstants.INCOME_SECTION_HEADER));

        return info;
    }

    public List<IncomeInfoDto> parseIncomes(String text) {
        List<IncomeInfoDto> incomes = new ArrayList<>();
        Pattern pattern = Pattern.compile(PdfConstants.INCOME_TABLE_PATTERN);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            IncomeInfoDto income = new IncomeInfoDto();
            income.setMonth(matcher.group(1));
            income.setIncomeCode(matcher.group(2));
            income.setIncomeAmount(new BigDecimal(matcher.group(3)));

            if (matcher.group(4) != null) {
                income.setDeductionCode(matcher.group(4));
                income.setDeductionAmount(new BigDecimal(matcher.group(5)));
            }
            incomes.add(income);
        }

        return incomes;
    }

    private BigDecimal parseBigDecimalField(String text, String pattern) {
        String value = textParserUtilsService.extractValue(
                text,
                pattern,
                PdfConstants.TABLE_COLUMN_SEPARATOR
        );
        return textParserUtilsService.parseBigDecimal(value);
    }
}
