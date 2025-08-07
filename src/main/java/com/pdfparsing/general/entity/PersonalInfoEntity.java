package com.pdfparsing.general.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "pdf_parsing_results")
public class PersonalInfoEntity {
    @Id
    @GeneratedValue
    private UUID id;

    private String personInn;
    private String personName;
    private String taxAgentName;
    private BigDecimal totalIncome;
    private BigDecimal taxAmount;
    private LocalDate documentDate;
    private Integer year;
    private LocalDateTime parsedAt;
}
