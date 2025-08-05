package com.pdfparsing.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "pdf_parsing_results")
public class PdfParsingEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )

    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "taxpayer_inn")
    private String taxpayerInn;

    @Column(name = "taxpayer_name")
    private String taxpayerName;

    @Column(name = "tax_agent_name")
    private String taxAgentName;

    @Column(name = "total_income")
    private BigDecimal totalIncome;

    @Column(name = "tax_amount")
    private BigDecimal taxAmount;

    @Column(name = "document_date")
    private LocalDate documentDate;

    @Column(name = "year")
    private Integer year;

    @Column(name = "parsed_at")
    private LocalDateTime parsedAt;
}