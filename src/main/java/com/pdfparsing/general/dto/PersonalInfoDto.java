package com.pdfparsing.general.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PersonalInfoDto {

    private String inn;
    private String lastName;
    private String firstName;
    private String middleName;
    private Integer taxpayerStatus;
    private LocalDate birthDate;
    private String citizenshipCode;
    private String documentCode;
    private String documentNumber;
}
