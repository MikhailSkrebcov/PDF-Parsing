package com.pdfparsing.general.service;

import com.pdfparsing.general.constants.PdfConstants;
import com.pdfparsing.general.dto.PdfParsingDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfDataParserService {
    private final TaxAgentInfoParserService taxAgentInfoParserService;
    private final PersonalInfoParserService personalInfoParserService;
    private final DatesParserService datesParserService;
    private final IncomesParserService incomesParserService;
    private final TaxSummaryParserService taxSummaryParserService;

    public PdfParsingDto parse(String text) {
        PdfParsingDto dto = new PdfParsingDto();

        dto.setTaxAgentInfo(taxAgentInfoParserService.parse(text));
        dto.setPersonalInfoDto(personalInfoParserService.parse(text));
        datesParserService.parse(text, dto);
        dto.setIncomes(incomesParserService.parse(text));
        dto.setTaxSummary(taxSummaryParserService.parse(text));

        log.info(PdfConstants.LOG_SUCCESS_PARSE, dto.getPersonalInfoDto().getInn());
        return dto;
    }
}
