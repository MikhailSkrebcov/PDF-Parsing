package com.pdfparsing.general.repository;

import com.pdfparsing.general.entity.PersonalInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PdfParsingRepository extends JpaRepository<PersonalInfoEntity, Long> {
}
