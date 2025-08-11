package com.pdfparsing.general.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PdfParsingDto {

    private TaxAgentInfoDto taxAgentInfo;
    private PersonalInfoDto personalInfoDto;
    private List<IncomeInfoDto> incomes;
    private TaxSummaryDto taxSummary;
    private LocalDate documentDate;
    private Integer year;
}
