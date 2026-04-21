package com.visa.visa_backoffice.repository;

import com.visa.visa_backoffice.model.Individu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IndividuCompletRepository extends JpaRepository<Individu, Integer> {

    interface IndividuCompletProjection {
        Integer getIndividuId();
        String getNom();
        String getPrenom();
        String getNomJeuneFille();
        LocalDate getDateNaissance();
        String getSituationFamiliale();
        String getNationalite();
        String getProfession();
        String getAdresseMada();
        String getContact();
        String getPasseportNumero();
        LocalDate getDateDelivrance();
        LocalDate getDateExpiration();
    }

    @Query(value =
            "SELECT " +
            "  i.id AS individu_id, " +
            "  i.nom AS nom, " +
            "  i.prenoms AS prenom, " +
            "  i.nom_jeune_fille AS nom_jeune_fille, " +
            "  i.date_naissance AS date_naissance, " +
            "  sf.libelle AS situation_familiale, " +
            "  i.nationalite AS nationalite, " +
            "  i.profession AS profession, " +
            "  i.adresse_mada AS adresse_mada, " +
            "  i.contact_mada AS contact, " +
            "  p.numero_pass AS passeport_numero, " +
            "  p.date_delivrance AS date_delivrance, " +
            "  p.date_expiration AS date_expiration " +
            "FROM dev.individu i " +
            "LEFT JOIN dev.situation_familiale sf ON sf.id = i.id_situation_familiale " +
            "LEFT JOIN dev.passeport p ON p.id_individu = i.id AND p.is_active = TRUE " +
            "ORDER BY i.nom ASC, i.prenoms ASC",
            nativeQuery = true)
    List<IndividuCompletProjection> findAllComplets();

    @Query(value =
            "SELECT " +
            "  i.id AS individu_id, " +
            "  i.nom AS nom, " +
            "  i.prenoms AS prenom, " +
            "  i.nom_jeune_fille AS nom_jeune_fille, " +
            "  i.date_naissance AS date_naissance, " +
            "  sf.libelle AS situation_familiale, " +
            "  i.nationalite AS nationalite, " +
            "  i.profession AS profession, " +
            "  i.adresse_mada AS adresse_mada, " +
            "  i.contact_mada AS contact, " +
            "  p.numero_pass AS passeport_numero, " +
            "  p.date_delivrance AS date_delivrance, " +
            "  p.date_expiration AS date_expiration " +
            "FROM dev.individu i " +
            "LEFT JOIN dev.situation_familiale sf ON sf.id = i.id_situation_familiale " +
            "LEFT JOIN dev.passeport p ON p.id_individu = i.id AND p.is_active = TRUE " +
            "WHERE i.id = :id",
            nativeQuery = true)
    Optional<IndividuCompletProjection> findCompletById(@Param("id") Integer id);
}
