package com.visa.visa_backoffice.repository;

import com.visa.visa_backoffice.model.VisaTransformable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VisaTransformableRepository extends JpaRepository<VisaTransformable, Integer> {
    Optional<VisaTransformable> findByNumeroReference(String numeroReference);
}
