package com.visa.visa_backoffice.repository;

import com.visa.visa_backoffice.model.PieceFichier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PieceFichierRepository extends JpaRepository<PieceFichier, Integer> {

    List<PieceFichier> findByPieceFournie_Id(Integer pieceFournieId);

    boolean existsByPieceFournie_Id(Integer pieceFournieId);

    void deleteByPieceFournie_Id(Integer pieceFournieId);
}
