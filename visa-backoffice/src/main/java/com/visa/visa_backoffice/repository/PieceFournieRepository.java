package com.visa.visa_backoffice.repository;

import com.visa.visa_backoffice.model.PieceFournie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PieceFournieRepository extends JpaRepository<PieceFournie, Integer> {
	List<PieceFournie> findByDemandeIdOrderByIdAsc(Integer demandeId);
}
