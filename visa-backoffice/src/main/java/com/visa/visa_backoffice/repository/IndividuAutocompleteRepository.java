package com.visa.visa_backoffice.repository;

import com.visa.visa_backoffice.model.Individu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IndividuAutocompleteRepository extends JpaRepository<Individu, Integer> {
    @Query(value = "SELECT i.id AS individu_id, i.nom, i.prenoms AS prenom, i.date_naissance, i.contact_mada AS contact, p.numero_pass AS passeport_numero, p.date_delivrance, p.date_expiration, " +
            "i.nom_jeune_fille, i.situation_familiale, i.nationalite, i.profession, i.adresse_mada " +
            "FROM dev.individu i " +
            "LEFT JOIN dev.passeport p ON p.id_individu = i.id AND p.is_active = TRUE " +
            "WHERE LOWER(i.nom) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(i.prenoms) LIKE LOWER(CONCAT('%', :search, '%'))",
            nativeQuery = true)
    List<Object[]> searchIndividuAutocomplete(@Param("search") String search);
}
