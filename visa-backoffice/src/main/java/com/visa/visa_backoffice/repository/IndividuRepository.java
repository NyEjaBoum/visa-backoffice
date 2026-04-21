package com.visa.visa_backoffice.repository;

import com.visa.visa_backoffice.model.Individu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;


public interface IndividuRepository extends JpaRepository<Individu, Integer> {
	@Query("SELECT i FROM Individu i WHERE LOWER(i.nom) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(i.prenoms) LIKE LOWER(CONCAT('%', :search, '%'))")
	List<Individu> searchByNomOrPrenom(@Param("search") String search);
}
