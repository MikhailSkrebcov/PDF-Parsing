package com.pdfparsing.demo.repository;

import com.pdfparsing.demo.entity.PdfParsingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PdfParsingRepository extends JpaRepository<PdfParsingEntity, Long> {
}