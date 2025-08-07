package com.pdfparsing.general.service;

import com.pdfparsing.general.constants.PdfConstants;
import com.pdfparsing.general.dto.TaxAgentInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaxAgentInfoParserService {

    private final TextParserUtilsService textParserUtilsService;

    public TaxAgentInfoDto parse(String text) {
        TaxAgentInfoDto info = new TaxAgentInfoDto();

        info.setOktmo(textParserUtilsService.extractValue(text, PdfConstants.OKTMO_FIELD, PdfConstants.PHONE_FIELD));
        info.setPhone(textParserUtilsService.extractValue(text, PdfConstants.PHONE_FIELD, PdfConstants.INN_FIELD));
        info.setInn(textParserUtilsService.extractValue(text, PdfConstants.INN_FIELD, PdfConstants.KPP_FIELD));
        info.setKpp(textParserUtilsService.extractValue(text, PdfConstants.KPP_FIELD, PdfConstants.TAX_AGENT_FIELD));
        info.setName(textParserUtilsService.extractValue(text, PdfConstants.TAX_AGENT_FIELD, PdfConstants.REORGANIZATION_FORM_FIELD));

        return info;
    }
}
