package com.visa.visa_backoffice.repository;

import com.visa.visa_backoffice.model.VisaTransformable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VisaTransformableRepository extends JpaRepository<VisaTransformable, Integer> {
    Optional<VisaTransformable> findByNumero(String numero);
    Optional<VisaTransformable> findFirstByDemandeurIdOrderByIdDesc(Integer demandeurId);
    Optional<VisaTransformable> findFirstByPasseportIdOrderByIdDesc(Integer passeportId);
}
