package com.pdfparsing.general.service;

import com.pdfparsing.general.constants.PdfConstants;
import com.pdfparsing.general.dto.PersonalInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonalInfoParserService {

    private final TextParserUtilsService textParserUtilsService;

    public PersonalInfoDto parse(String text) {
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
}
