package com.visa.visa_backoffice.service;

import com.visa.visa_backoffice.dto.DemandeForm;
import com.visa.visa_backoffice.model.*;
import com.visa.visa_backoffice.repository.DemandeRepository;
import com.visa.visa_backoffice.repository.DossierCompletRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class DemandeService {

    private final DemandeRepository demandeRepository;
    private final DemandeurService demandeurService;
    private final PasseportService passeportService;
    private final TypeVisaService typeVisaService;
    private final TypeDemandeService typeDemandeService;
    private final StatutService statutService;
    private final NationaliteService nationaliteService;
    private final SituationFamilialeService situationFamilialeService;
    private final DossierCompletRepository dossierCompletRepository;
    private final PieceFournieService pieceFournieService;
    private final PieceJustificativeService pieceJustificativeService;

    public DemandeService(DemandeRepository demandeRepository,
                          DemandeurService demandeurService,
                          PasseportService passeportService,
                          TypeVisaService typeVisaService,
                          TypeDemandeService typeDemandeService,
                          StatutService statutService,
                          NationaliteService nationaliteService,
                          SituationFamilialeService situationFamilialeService,
                          DossierCompletRepository dossierCompletRepository,
                          PieceFournieService pieceFournieService,
                          PieceJustificativeService pieceJustificativeService) {
        this.demandeRepository = demandeRepository;
        this.demandeurService = demandeurService;
        this.passeportService = passeportService;
        this.typeVisaService = typeVisaService;
        this.typeDemandeService = typeDemandeService;
        this.statutService = statutService;
        this.nationaliteService = nationaliteService;
        this.situationFamilialeService = situationFamilialeService;
        this.dossierCompletRepository = dossierCompletRepository;
        this.pieceFournieService = pieceFournieService;
        this.pieceJustificativeService = pieceJustificativeService;
    }

    @Transactional(readOnly = true)
    public List<DossierComplet> rechercherAntecedents(String query) {
        return dossierCompletRepository.searchDossierExistants(query);
    }

    @Transactional(readOnly = true)
    public List<Demande> findAll() {
        return demandeRepository.findAllWithRefs();
    }

    @Transactional(readOnly = true)
    public Demande findByIdOrThrow(Integer id) {
        return demandeRepository.findByIdWithRefs(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable"));
    }

    public Demande creer(DemandeForm form) {
        // 1. Demandeur
        Demandeur demandeur = new Demandeur();
        demandeur.setNom(form.getNom());
        demandeur.setPrenoms(form.getPrenoms());
        demandeur.setNomJeuneFille(form.getNomJeuneFille());
        demandeur.setDateNaissance(form.getDateNaissance());
        demandeur.setProfession(form.getProfession());
        demandeur.setAdresseMada(form.getAdresseMada());
        demandeur.setContactMada(form.getContactMada());
        if (form.getNationaliteId() != null)
            demandeur.setNationalite(nationaliteService.findById(form.getNationaliteId()));
        if (form.getSituationFamilialeId() != null)
            demandeur.setSituationFamiliale(situationFamilialeService.findById(form.getSituationFamilialeId()));
        demandeur = demandeurService.create(demandeur);

        // 2. Passeport
        Passeport passeport = new Passeport();
        passeport.setNumero(form.getNumeroPasseport());
        passeport.setDateDelivrance(form.getDateDelivrance());
        passeport.setDateExpiration(form.getDateExpiration());
        passeport = passeportService.create(demandeur.getId(), passeport);

        // 3. Demande
        Demande demande = new Demande();
        demande.setNumDemande(genererNumeroDossier());
        demande.setDemandeur(demandeur);
        demande.setTypeVisa(typeVisaService.findById(form.getTypeVisaId()));
        demande.setTypeDemande(typeDemandeService.findById(form.getTypeDemandeId()));
        demande.setStatut(statutService.getStatutCree());
        demande.setDateCreation(LocalDateTime.now());
        demande = demandeRepository.save(demande);

        // 4. Pièces justificatives
        pieceFournieService.creerChecklist(
                demande,
                pieceJustificativeService.findForTypeVisa(form.getTypeVisaId()),
                form.getPiecesFourniesIds());

        return demande;
    }

    public Demande modifier(Integer id, DemandeForm form) {
        Demande demande = findByIdOrThrow(id);

        if (demande.getStatut() == null || !"CREE".equalsIgnoreCase(demande.getStatut().getLibelle())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Modification impossible : dossier déjà verrouillé");
        }

        // 1. Update Demandeur
        Demandeur demandeur = demande.getDemandeur();
        demandeur.setNom(form.getNom());
        demandeur.setPrenoms(form.getPrenoms());
        demandeur.setNomJeuneFille(form.getNomJeuneFille());
        demandeur.setDateNaissance(form.getDateNaissance());
        demandeur.setProfession(form.getProfession());
        demandeur.setAdresseMada(form.getAdresseMada());
        demandeur.setContactMada(form.getContactMada());
        if (form.getNationaliteId() != null)
            demandeur.setNationalite(nationaliteService.findById(form.getNationaliteId()));
        if (form.getSituationFamilialeId() != null)
            demandeur.setSituationFamiliale(situationFamilialeService.findById(form.getSituationFamilialeId()));
        demandeurService.update(demandeur.getId(), demandeur);

        // 2. Update Passeport
        Passeport passeport = passeportService.findLastForDemandeur(demandeur.getId()).orElse(null);
        if (passeport != null) {
            passeport.setNumero(form.getNumeroPasseport());
            passeport.setDateDelivrance(form.getDateDelivrance());
            passeport.setDateExpiration(form.getDateExpiration());
            passeportService.update(passeport.getId(), passeport);
        }

        // 3. Update Demande
        demande.setTypeVisa(typeVisaService.findById(form.getTypeVisaId()));
        demande.setTypeDemande(typeDemandeService.findById(form.getTypeDemandeId()));
        demande = demandeRepository.save(demande);

        // 4. Update Pièces
        pieceFournieService.updateChecklist(
                demande,
                pieceJustificativeService.findForTypeVisa(form.getTypeVisaId()),
                form.getPiecesFourniesIds());

        return demande;
    }

    private String genererNumeroDossier() {
        int year = LocalDate.now().getYear();
        String prefix = "MADA-" + year + "-";
        int next = demandeRepository
                .findTopByNumDemandeStartingWithOrderByNumDemandeDesc(prefix)
                .map(d -> {
                    try { return Integer.parseInt(d.getNumDemande().substring(prefix.length())); }
                    catch (NumberFormatException e) { return 0; }
                })
                .orElse(0) + 1;
        return prefix + String.format("%04d", next);
    }
}