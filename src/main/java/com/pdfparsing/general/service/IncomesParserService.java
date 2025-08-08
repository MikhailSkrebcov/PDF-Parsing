package com.pdfparsing.general.service;

import com.pdfparsing.general.constants.PdfConstants;
import com.pdfparsing.general.dto.IncomeInfoDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class IncomesParserService {

    public List<IncomeInfoDto> parse(String text) {
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
}
