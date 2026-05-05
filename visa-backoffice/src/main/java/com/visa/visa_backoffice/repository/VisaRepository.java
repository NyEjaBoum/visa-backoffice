package com.visa.visa_backoffice.repository;

import com.visa.visa_backoffice.model.Visa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VisaRepository extends JpaRepository<Visa, Integer> {

	Optional<Visa> findTopByNumeroStartingWithOrderByNumeroDesc(String prefix);
}
