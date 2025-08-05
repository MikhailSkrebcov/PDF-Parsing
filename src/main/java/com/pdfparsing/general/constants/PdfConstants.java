package com.pdfparsing.general.constants;

public class PdfConstants {

    public static final String OKATO_FIELD = "Код по ОКТМО";
    public static final String PHONE_FIELD = "Телефон";
    public static final String INN_FIELD = "ИНН";
    public static final String KPP_FIELD = "КПП";
    public static final String TAX_AGENT_FIELD = "Налоговый агент";
    public static final String REORGANIZATION_FORM_FIELD = "Форма реорганизации";

    public static final String RUSSIAN_INN_FIELD = "ИНН в Российской Федерации";
    public static final String LAST_NAME_FIELD = "Фамилия";
    public static final String FIRST_NAME_FIELD = "Имя";
    public static final String MIDDLE_NAME_FIELD = "Отчество";
    public static final String TAXPAYER_STATUS_FIELD = "Статус налогоплательщика";
    public static final String BIRTH_DATE_FIELD = "Дата рождения";
    public static final String CITIZENSHIP_FIELD = "Гражданство (код страны)";
    public static final String DOCUMENT_CODE_FIELD = "Код документа, удостоверяющего личность";
    public static final String DOCUMENT_NUMBER_FIELD = "Серия и номер документа";
    public static final String INCOME_SECTION_HEADER = "3. Доходы";
    public static final String AGENT_SECTION_HEADER = "1. Данные";
    public static final String DATE_FROM_TEXT = "от";


    public static final String YEAR_PATTERN = "за _(\\d{4})_ год";
    public static final String DOCUMENT_DATE_PATTERN = "за _\\d{4}_ год от _\\d{2}_\\. _\\d{2}_\\. _\\d{4}_";
    public static final String INCOME_TABLE_PATTERN = "\\|(\\d{2})\\|(\\d{4})\\|([\\d.,]+)\\|(\\d{3})?\\|([\\d.,]+)?\\|";
    public static final String UNDERSCORE_PATTERN = "_";
    public static final String DATE_SEPARATOR = "\\.";
    public static final String NAME_SEPARATOR = " ";
    public static final String DECIMAL_COMMA = ",";
    public static final String DECIMAL_POINT = ".";
    public static final String TEXT_EXTRACTION_PATTERN = "([\\s\\S]*?)";
    public static final String TABLE_COLUMN_SEPARATOR = "\\|";


    public static final String TOTAL_INCOME_PATTERN = "Общая сумма дохода\\|([\\d.,]+)";
    public static final String TAX_BASE_PATTERN = "Налоговая база\\|([\\d.,]+)";
    public static final String CALCULATED_TAX_PATTERN = "Сумма налога исчисленная\\|(\\d+)";
    public static final String TRANSFERRED_TAX_PATTERN = "Сумма налога перечисленная\\|(\\d+)";
    public static final String WITHHELD_TAX_PATTERN = "Сумма налога удержанная\\|(\\d+)";
    public static final String OVERPAID_TAX_PATTERN = "Сумма налога, излишне удержанная\\|(\\d+)";


    public static final String LOG_PDF_PROCESSING_ERROR = "Ошибка обработки PDF файла: {}";
    public static final String ERROR_PDF_PROCESSING = "Ошибка обработки PDF: ";
    public static final String LOG_START_PDF_PROCESSING = "Начало обработки PDF файла: {}";
    public static final String LOG_EXTRACTED_TEXT = "Извлеченный текст PDF:\n{}";
    public static final String LOG_SUCCESS_PARSE = "Успешно распарсены данные для ИНН: {}";
    public static final String LOG_SAVED_TO_DB = "Сохранено в БД с ID: {}";
}
