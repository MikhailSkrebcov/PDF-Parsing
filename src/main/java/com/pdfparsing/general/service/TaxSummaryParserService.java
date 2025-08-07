package com.pdfparsing.general.service;

import com.pdfparsing.general.constants.PdfConstants;
import com.pdfparsing.general.dto.TaxSummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TaxSummaryParserService {

    private final TextParserUtilsService textParserUtilsService;

    public TaxSummaryDto parse(String text) {
        TaxSummaryDto summary = new TaxSummaryDto();

        summary.setTotalIncome(parseBigDecimalField(text, PdfConstants.TOTAL_INCOME_PATTERN));
        summary.setTaxBase(parseBigDecimalField(text, PdfConstants.TAX_BASE_PATTERN));
        summary.setCalculatedTax(parseBigDecimalField(text, PdfConstants.CALCULATED_TAX_PATTERN));
        summary.setTransferredTax(parseBigDecimalField(text, PdfConstants.TRANSFERRED_TAX_PATTERN));
        summary.setWithheldTax(parseBigDecimalField(text, PdfConstants.WITHHELD_TAX_PATTERN));
        summary.setOverpaidTax(parseBigDecimalField(text, PdfConstants.OVERPAID_TAX_PATTERN));

        return summary;
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
