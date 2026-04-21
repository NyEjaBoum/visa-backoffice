package com.visa.visa_backoffice.service;

import com.visa.visa_backoffice.dto.CreateDossierRequest;
import com.visa.visa_backoffice.dto.CreateDossierResponse;
import com.visa.visa_backoffice.dto.DossierDetailDTO;
import com.visa.visa_backoffice.dto.DossierListItemDTO;
import com.visa.visa_backoffice.dto.PieceFournieItem;
import com.visa.visa_backoffice.dto.PieceFournieDTO;
import com.visa.visa_backoffice.dto.ValiderDossierResponse;
import com.visa.visa_backoffice.model.*;
import com.visa.visa_backoffice.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DossierService {

    private static final int STATUT_CREE_ID    = 1;
    private static final int STATUT_VALIDE_ID  = 10;
    private static final int STATUT_ANNULE_ID  = 20;

    private final IndividuRepository individuRepository;
    private final PasseportRepository passeportRepository;
    private final DemandeVisaRepository demandeVisaRepository;
    private final PieceFournieRepository pieceFournieRepository;
    private final TypeDemandeRepository typeDemandeRepository;
    private final TypeVisaRepository typeVisaRepository;
    private final StatutRepository statutRepository;
    private final SituationFamilialeRepository situationFamilialeRepository;
    private final VisaTransformableRepository visaTransformableRepository;
    private final VisaRepository visaRepository;
    private final CarteResidentRepository carteResidentRepository;

    public DossierService(IndividuRepository individuRepository,
                          PasseportRepository passeportRepository,
                          DemandeVisaRepository demandeVisaRepository,
                          PieceFournieRepository pieceFournieRepository,
                          TypeDemandeRepository typeDemandeRepository,
                          TypeVisaRepository typeVisaRepository,
                          StatutRepository statutRepository,
                          SituationFamilialeRepository situationFamilialeRepository,
                          VisaTransformableRepository visaTransformableRepository,
                          VisaRepository visaRepository,
                          CarteResidentRepository carteResidentRepository) {
        this.individuRepository = individuRepository;
        this.passeportRepository = passeportRepository;
        this.demandeVisaRepository = demandeVisaRepository;
        this.pieceFournieRepository = pieceFournieRepository;
        this.typeDemandeRepository = typeDemandeRepository;
        this.typeVisaRepository = typeVisaRepository;
        this.statutRepository = statutRepository;
        this.situationFamilialeRepository = situationFamilialeRepository;
        this.visaTransformableRepository = visaTransformableRepository;
        this.visaRepository = visaRepository;
        this.carteResidentRepository = carteResidentRepository;
    }

    @Transactional
    public CreateDossierResponse createDossier(CreateDossierRequest request) {
        validateRequest(request);

        TypeDemande typeDemande = typeDemandeRepository.findById(request.typeDemandeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Type de demande introuvable"));

        TypeVisa typeVisa = typeVisaRepository.findById(request.typeVisaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Type de visa introuvable"));

        Statut statutCree = statutRepository.findById(STATUT_CREE_ID)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Statut CREE non configure"));

        SituationFamiliale situationFamiliale = null;
        if (request.situationFamilialeId() != null) {
            situationFamiliale = situationFamilialeRepository.findById(request.situationFamilialeId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Situation familiale introuvable"));
        }

        Individu individu = new Individu();
        individu.setNom(request.nom().trim());
        individu.setPrenoms(safeTrim(request.prenoms()));
        individu.setNomJeuneFille(safeTrim(request.nomJeuneFille()));
        individu.setDateNaissance(request.dateNaissance());
        individu.setSituationFamiliale(situationFamiliale);
        individu.setNationalite(safeTrim(request.nationalite()));
        individu.setProfession(safeTrim(request.profession()));
        individu.setAdresseMada(safeTrim(request.adresseMada()));
        individu.setContactMada(safeTrim(request.contactMada()));
        Individu savedIndividu = individuRepository.save(individu);

        Passeport passeport = new Passeport();
        passeport.setIndividu(savedIndividu);
        passeport.setNumeroPass(request.numeroPasseport().trim());
        passeport.setDateDelivrance(request.dateDelivrancePasseport());
        passeport.setDateExpiration(request.dateExpirationPasseport());
        passeport.setActive(true);
        Passeport savedPasseport = passeportRepository.save(passeport);

        VisaTransformable visaTransformable = null;
        if (!isBlank(request.numeroVisaTransformable())) {
            String ref = request.numeroVisaTransformable().trim();
            visaTransformable = visaTransformableRepository.findByNumeroReference(ref)
                    .orElseGet(() -> {
                        VisaTransformable vt = new VisaTransformable();
                        vt.setIndividu(savedIndividu);
                        vt.setPasseport(savedPasseport);
                        vt.setNumeroReference(ref);
                        vt.setDateEntree(request.dateEntree());
                        vt.setLieuEntree(request.lieuEntree());
                        vt.setDateFinVisa(request.dateFinVisa());
                        return visaTransformableRepository.save(vt);
                    });

            if (request.dateEntree() != null) {
                visaTransformable.setDateEntree(request.dateEntree());
            }
            if (!isBlank(request.lieuEntree())) {
                visaTransformable.setLieuEntree(request.lieuEntree().trim());
            }
            if (request.dateFinVisa() != null) {
                visaTransformable.setDateFinVisa(request.dateFinVisa());
            }

            visaTransformable = visaTransformableRepository.save(visaTransformable);
        }

        DemandeVisa dossier = new DemandeVisa();
        dossier.setNumDemande(generateNumeroDossier());
        dossier.setIndividu(savedIndividu);
        dossier.setPasseport(savedPasseport);
        dossier.setTypeDemande(typeDemande);
        dossier.setTypeVisa(typeVisa);
        dossier.setStatut(statutCree);
        dossier.setVisaTransformable(visaTransformable);
        dossier.setDateCreation(LocalDateTime.now());
        DemandeVisa savedDossier = demandeVisaRepository.save(dossier);

        if (request.piecesFournies() != null) {
            for (PieceFournieItem item : request.piecesFournies()) {
                if (item == null || isBlank(item.libellePiece())) continue;
                PieceFournie pf = new PieceFournie();
                pf.setDemande(savedDossier);
                pf.setLibellePiece(item.libellePiece().trim());
                pf.setPresent(Boolean.TRUE.equals(item.isPresent()));
                pieceFournieRepository.save(pf);
            }
        }

        return new CreateDossierResponse(savedDossier.getId(), savedDossier.getNumDemande(), statutCree.getId());
    }

    /**
     * Valide une demande et emet automatiquement une carte de resident.
     * La carte est valable 1 an a partir de la date de validation.
     */
    @Transactional
    public ValiderDossierResponse validerDossier(Integer dossierId) {
        DemandeVisa dossier = demandeVisaRepository.findById(dossierId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dossier introuvable"));

        if (dossier.getStatut() != null && dossier.getStatut().getId() == STATUT_VALIDE_ID) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ce dossier est deja valide");
        }
        if (dossier.getStatut() != null && dossier.getStatut().getId() == STATUT_ANNULE_ID) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Un dossier annule ne peut pas etre valide");
        }

        Statut statutValide = statutRepository.findById(STATUT_VALIDE_ID)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Statut VALIDE non configure"));

        dossier.setStatut(statutValide);
        demandeVisaRepository.save(dossier);

        LocalDate dateDebut = LocalDate.now();
        LocalDate dateFin = dateDebut.plusYears(1);

        Visa visa = new Visa();
        visa.setDemande(dossier);
        visa.setReference(generateReferenceVisa());
        visa.setDateDebut(dateDebut);
        visa.setDateFin(dateFin);
        visa.setPasseport(dossier.getPasseport());
        visaRepository.save(visa);

        CarteResident carte = new CarteResident();
        carte.setDemande(dossier);
        carte.setReference(generateReferenceCarte());
        carte.setDateDebut(dateDebut);
        carte.setDateFin(dateFin);
        carte.setPasseport(dossier.getPasseport());
        CarteResident savedCarte = carteResidentRepository.save(carte);

        return new ValiderDossierResponse(
                dossier.getId(),
                dossier.getNumDemande(),
                savedCarte.getId(),
                savedCarte.getReference(),
                savedCarte.getDateDebut(),
                savedCarte.getDateFin()
        );
    }

    private String generateNumeroDossier() {
        int year = LocalDate.now().getYear();
        String prefix = "MADA-" + year + "-";
        int next = demandeVisaRepository.findTopByNumDemandeStartingWithOrderByNumDemandeDesc(prefix)
                .map(d -> d.getNumDemande().substring(prefix.length()))
                .map(this::parseSafely)
                .orElse(0) + 1;
        return prefix + String.format("%04d", next);
    }

    private String generateReferenceCarte() {
        int year = LocalDate.now().getYear();
        String prefix = "CR-" + year + "-";
        int next = carteResidentRepository.findTopByReferenceStartingWithOrderByReferenceDesc(prefix)
                .map(c -> c.getReference().substring(prefix.length()))
                .map(this::parseSafely)
                .orElse(0) + 1;
        return prefix + String.format("%04d", next);
    }

    private String generateReferenceVisa() {
        int year = LocalDate.now().getYear();
        String prefix = "V-" + year + "-";
        int next = visaRepository.findTopByReferenceStartingWithOrderByReferenceDesc(prefix)
                .map(v -> v.getReference().substring(prefix.length()))
                .map(this::parseSafely)
                .orElse(0) + 1;
        return prefix + String.format("%04d", next);
    }

    public int countDossiersByStatut(int statutId) {
        return (int) demandeVisaRepository.findAll().stream()
                .filter(d -> d.getStatut() != null && d.getStatut().getId() == statutId)
                .count();
    }

        @Transactional(readOnly = true)
        public List<DossierListItemDTO> listDossiers() {
        return demandeVisaRepository.findAllWithRefs().stream()
            .map(d -> new DossierListItemDTO(
                d.getId(),
                d.getNumDemande(),
                d.getIndividu() != null ? d.getIndividu().getNom() : null,
                d.getIndividu() != null ? d.getIndividu().getPrenoms() : null,
                d.getTypeVisa() != null ? d.getTypeVisa().getLibelle() : null,
                d.getTypeDemande() != null ? d.getTypeDemande().getLibelle() : null,
                d.getStatut() != null ? d.getStatut().getLibelle() : null,
                d.getDateCreation()
            ))
            .toList();
        }

        @Transactional(readOnly = true)
        public DossierDetailDTO getDossier(Integer id) {
        DemandeVisa d = demandeVisaRepository.findByIdWithRefs(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dossier introuvable"));

        List<PieceFournieDTO> pieces = pieceFournieRepository.findByDemandeIdOrderByIdAsc(id).stream()
            .map(p -> new PieceFournieDTO(p.getId(), p.getLibellePiece(), p.getPresent()))
            .toList();

        VisaTransformable vt = d.getVisaTransformable();
        Passeport pass = d.getPasseport();
        Individu ind = d.getIndividu();

        return new DossierDetailDTO(
            d.getId(),
            d.getNumDemande(),
            d.getTypeVisa() != null ? d.getTypeVisa().getLibelle() : null,
            d.getTypeDemande() != null ? d.getTypeDemande().getLibelle() : null,
            d.getStatut() != null ? d.getStatut().getLibelle() : null,
            d.getDateCreation(),
            ind != null ? ind.getId() : null,
            ind != null ? ind.getNom() : null,
            ind != null ? ind.getPrenoms() : null,
            ind != null ? ind.getDateNaissance() : null,
            pass != null ? pass.getNumeroPass() : null,
            pass != null ? pass.getDateDelivrance() : null,
            pass != null ? pass.getDateExpiration() : null,
            vt != null ? vt.getNumeroReference() : null,
            vt != null ? vt.getDateEntree() : null,
            vt != null ? vt.getLieuEntree() : null,
            vt != null ? vt.getDateFinVisa() : null,
            pieces
        );
        }

    private void validateRequest(CreateDossierRequest request) {
        if (request.typeDemandeId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le type de demande est obligatoire");
        if (request.typeVisaId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La categorie est obligatoire");
        if (isBlank(request.nom()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le nom est obligatoire");
        if (request.dateNaissance() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La date de naissance est obligatoire");
        if (isBlank(request.numeroPasseport()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le numero de passeport est obligatoire");
    }

    private int parseSafely(String value) {
        try { return Integer.parseInt(value); } catch (NumberFormatException ignored) { return 0; }
    }

    private boolean isBlank(String value) { return value == null || value.isBlank(); }
    private String safeTrim(String value) { return value == null ? null : value.trim(); }
}
