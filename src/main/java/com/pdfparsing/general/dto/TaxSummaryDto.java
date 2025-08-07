package com.pdfparsing.general.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TaxSummaryDto {

    private BigDecimal totalIncome;
    private BigDecimal taxBase;
    private BigDecimal calculatedTax;
    private BigDecimal transferredTax;
    private BigDecimal withheldTax;
    private BigDecimal overpaidTax;
}
