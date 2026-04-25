package com.visa.visa_backoffice.service;

import com.visa.visa_backoffice.dto.DemandeAntecedentForm;
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
    private final VisaTransformableService visaTransformableService;
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
                          VisaTransformableService visaTransformableService,
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
        this.visaTransformableService = visaTransformableService;
        this.dossierCompletRepository = dossierCompletRepository;
        this.pieceFournieService = pieceFournieService;
        this.pieceJustificativeService = pieceJustificativeService;
    }

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

    // ─── Sprint 1 : Création standard ────────────────────────────────────────
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

        // 3. Visa transformable (optionnel pour Sprint 1)
        VisaTransformable visaTransformable = null;
        String vtNum = form.getVisaTransformableNumero();
        if (vtNum != null && !vtNum.isBlank()) {
            visaTransformable = new VisaTransformable();
            visaTransformable.setPasseport(passeport);
            visaTransformable.setDemandeur(demandeur);
            visaTransformable.setNumero(vtNum);
            visaTransformable.setDateEntree(form.getVisaTransformableDateEntree());
            visaTransformable.setLieuEntree(form.getVisaTransformableLieuEntree());
            visaTransformable.setDateFinVisa(form.getVisaTransformableDateFinVisa());
            visaTransformable = visaTransformableService.save(visaTransformable);
        }

        // 4. Demande
        Demande demande = new Demande();
        demande.setNumDemande(genererNumeroDossier());
        demande.setDemandeur(demandeur);
        demande.setTypeVisa(typeVisaService.findById(form.getTypeVisaId()));
        demande.setTypeDemande(typeDemandeService.findById(form.getTypeDemandeId()));
        demande.setStatut(statutService.getStatutCree());
        demande.setDateCreation(LocalDateTime.now());
        demande.setVisaTransformable(visaTransformable);
        demande = demandeRepository.save(demande);

        // 5. Pièces justificatives
        pieceFournieService.creerChecklist(
                demande,
                pieceJustificativeService.findForTypeVisa(form.getTypeVisaId()),
                form.getPiecesFourniesIds());

        return demande;
    }

    public Demande modifier(Integer id, DemandeForm form) {
        Demande demande = demandeRepository.findByIdWithRefs(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable"));

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
        else
            demandeur.setNationalite(null);
        if (form.getSituationFamilialeId() != null)
            demandeur.setSituationFamiliale(situationFamilialeService.findById(form.getSituationFamilialeId()));
        else
            demandeur.setSituationFamiliale(null);
        demandeurService.update(demandeur.getId(), demandeur);

        // 2. Update Passeport
        final Demandeur d = demandeur;
        Passeport passeport = passeportService
            .findLastForDemandeur(d.getId())
            .orElseGet(() -> {
                Passeport p = new Passeport();
                p.setNumero(form.getNumeroPasseport());
                return passeportService.create(d.getId(), p);
            });
        passeport.setNumero(form.getNumeroPasseport());
        passeport.setDateDelivrance(form.getDateDelivrance());
        passeport.setDateExpiration(form.getDateExpiration());
        passeportService.update(passeport.getId(), passeport);

        // 3. Update / création éventuelle du Visa transformable
        String vtNum = form.getVisaTransformableNumero();
        if (vtNum != null) vtNum = vtNum.trim();
        if (vtNum != null && !vtNum.isBlank()) {
            VisaTransformable vt = demande.getVisaTransformable();
            if (vt == null) {
                vt = new VisaTransformable();
                vt.setPasseport(passeport);
                vt.setDemandeur(demandeur);
            }
            vt.setNumero(vtNum);
            vt.setDateEntree(form.getVisaTransformableDateEntree());
            vt.setLieuEntree(form.getVisaTransformableLieuEntree());
            vt.setDateFinVisa(form.getVisaTransformableDateFinVisa());
            vt = visaTransformableService.save(vt);
            demande.setVisaTransformable(vt);
        } else {
            demande.setVisaTransformable(null);
        }

        // 4. Update Demande
        demande.setTypeVisa(typeVisaService.findById(form.getTypeVisaId()));
        demande.setTypeDemande(typeDemandeService.findById(form.getTypeDemandeId()));
        demande = demandeRepository.save(demande);

        // 5. Mise à jour pièces justificatives
        pieceFournieService.updateChecklist(
                demande,
                pieceJustificativeService.findForTypeVisa(form.getTypeVisaId()),
                form.getPiecesFourniesIds());

        return demande;
    }

    // ─── Sprint 2 : Cas Normal (demandeur existant) ──────────────────────────
    public Demande creerCasNormal(DemandeAntecedentForm form) {
        Demandeur demandeur = demandeurService.findByIdOrThrow(form.getDemandeurId());

        Passeport passeport = passeportService.findLastForDemandeur(demandeur.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Aucun passeport pour ce demandeur"));

        // Créer ou réutiliser le visa transformable
        VisaTransformable vt = visaTransformableService
                .findByNumero(form.getVisaTransformableNumero())
                .orElseGet(() -> {
                    VisaTransformable newVt = new VisaTransformable();
                    newVt.setPasseport(passeport);
                    newVt.setDemandeur(demandeur);
                    newVt.setNumero(form.getVisaTransformableNumero());
                    newVt.setDateEntree(form.getVisaTransformableDateEntree());
                    newVt.setLieuEntree(form.getVisaTransformableLieuEntree());
                    newVt.setDateFinVisa(form.getVisaTransformableDateFinVisa());
                    return visaTransformableService.save(newVt);
                });

        Demande demande = new Demande();
        demande.setNumDemande(genererNumeroDossier());
        demande.setDemandeur(demandeur);
        demande.setTypeVisa(typeVisaService.findById(form.getTypeVisaId()));
        demande.setTypeDemande(typeDemandeService.findById(form.getTypeDemandeId()));
        demande.setStatut(statutService.getStatutCree());
        demande.setVisaTransformable(vt);
        demande.setDateCreation(LocalDateTime.now());
        return demandeRepository.save(demande);
    }

    // ─── Sprint 2 : Rattrapage (double demande A + B) ────────────────────────
    public List<Demande> creerRattrapage(DemandeAntecedentForm form) {
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

        // 3. Visa transformable (partagé A + B)
        VisaTransformable vt = new VisaTransformable();
        vt.setPasseport(passeport);
        vt.setDemandeur(demandeur);
        vt.setNumero(form.getVisaTransformableNumero());
        vt.setDateEntree(form.getVisaTransformableDateEntree());
        vt.setLieuEntree(form.getVisaTransformableLieuEntree());
        vt.setDateFinVisa(form.getVisaTransformableDateFinVisa());
        vt = visaTransformableService.save(vt);

        // 4. Demande A : NOUVEAU TITRE / VISA APPROUVE
        Demande demandeA = new Demande();
        demandeA.setNumDemande(genererNumeroDossier());
        demandeA.setDemandeur(demandeur);
        demandeA.setTypeVisa(typeVisaService.findById(form.getTypeVisaId()));
        demandeA.setTypeDemande(typeDemandeService.findByLibelleOrThrow("NOUVEAU TITRE"));
        demandeA.setStatut(statutService.getStatutVisaApprouve());
        demandeA.setVisaTransformable(vt);
        demandeA.setDateCreation(LocalDateTime.now());
        demandeA = demandeRepository.save(demandeA);

        // 5. Demande B : DUPLICATA / TRANSFERT, statut CREE
        Demande demandeB = new Demande();
        demandeB.setNumDemande(genererNumeroDossier());
        demandeB.setDemandeur(demandeur);
        demandeB.setTypeVisa(typeVisaService.findById(form.getTypeVisaId()));
        demandeB.setTypeDemande(typeDemandeService.findById(form.getTypeDemandeId()));
        demandeB.setStatut(statutService.getStatutCree());
        demandeB.setVisaTransformable(vt);
        demandeB.setDateCreation(LocalDateTime.now());
        demandeB = demandeRepository.save(demandeB);

        return List.of(demandeA, demandeB);
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
