package com.pdfparsing.general.service;

import com.pdfparsing.general.constants.PdfConstants;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TextParserUtilsService {

    public String extractValue(String text, String startRegex, String endRegex) {
        if (text == null) {
            return null;
        }

        Pattern pattern = Pattern.compile(startRegex + PdfConstants.TEXT_EXTRACTION_PATTERN + endRegex);
        Matcher matcher = pattern.matcher(text);

        return matcher.find() ? matcher.group(1).trim() : null;
    }

    public BigDecimal parseBigDecimal(String value) {
        if (value == null || value.isEmpty()) {
            return BigDecimal.ZERO;
        }

        String cleanedValue = value.replaceAll("[^\\d,-.]", "")
                .replace(",", ".");

        try {
            return new BigDecimal(cleanedValue);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    public LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }

        try {
            String[] parts = dateStr.split(PdfConstants.DATE_SEPARATOR);
            return LocalDate.of(
                    Integer.parseInt(parts[2].trim()),
                    Integer.parseInt(parts[1].trim()),
                    Integer.parseInt(parts[0].trim())
            );
        } catch (Exception e) {
            return null;
        }
    }

    public String formatName(String lastName, String firstName, String middleName) {
        if (lastName == null) lastName = "";
        if (firstName == null) firstName = "";
        if (middleName == null) middleName = "";

        return String.join(PdfConstants.NAME_SEPARATOR, lastName, firstName, middleName).trim();
    }
}
