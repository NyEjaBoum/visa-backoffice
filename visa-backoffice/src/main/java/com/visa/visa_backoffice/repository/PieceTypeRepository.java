package com.visa.visa_backoffice.repository;

import com.visa.visa_backoffice.model.PieceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PieceTypeRepository extends JpaRepository<PieceType, Integer> {

    List<PieceType> findByTypeVisaIsNull();

    List<PieceType> findByTypeVisaId(Integer typeVisaId);
}
