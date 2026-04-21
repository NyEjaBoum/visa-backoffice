package com.visa.visa_backoffice.repository;

import com.visa.visa_backoffice.model.Visa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VisaRepository extends JpaRepository<Visa, Integer> {
	Optional<Visa> findTopByReferenceStartingWithOrderByReferenceDesc(String prefix);
}
