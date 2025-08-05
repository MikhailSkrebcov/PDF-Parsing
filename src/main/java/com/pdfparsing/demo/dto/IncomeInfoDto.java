package com.pdfparsing.demo.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class IncomeInfoDto {

    private String month;
    private String incomeCode;
    private BigDecimal incomeAmount;
    private String deductionCode;
    private BigDecimal deductionAmount;
}
