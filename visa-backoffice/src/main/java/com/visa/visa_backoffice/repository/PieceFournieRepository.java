package com.visa.visa_backoffice.repository;

import com.visa.visa_backoffice.model.PieceFournie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PieceFournieRepository extends JpaRepository<PieceFournie, Integer> {
}
