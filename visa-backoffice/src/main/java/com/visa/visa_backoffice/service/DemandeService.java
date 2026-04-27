package com.visa.visa_backoffice.service;

import com.visa.visa_backoffice.model.*;
import com.visa.visa_backoffice.repository.CarteResidentRepository;
import com.visa.visa_backoffice.repository.DemandeRepository;
import com.visa.visa_backoffice.repository.DossierCompletRepository;
import com.visa.visa_backoffice.repository.PieceFournieRepository;
import com.visa.visa_backoffice.repository.VisaRepository;
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
    private final StatutService statutService;
    private final DossierCompletRepository dossierCompletRepository;
    private final PieceFournieService pieceFournieService;
    private final PieceFournieRepository pieceFournieRepository;
    private final PieceJustificativeService pieceJustificativeService;
    private final HistoriqueStatutDemandeService historiqueDemandeStatutService;
    private final VisaRepository visaRepository;
    private final CarteResidentRepository carteResidentRepository;
    private final PieceFichierService pieceFichierService;
    private final DemandeurService demandeurService;
    private final PasseportService passeportService;
    private final VisaTransformableService visaTransformableService;

    public DemandeService(DemandeRepository demandeRepository,
                          StatutService statutService,
                          DossierCompletRepository dossierCompletRepository,
                          PieceFournieService pieceFournieService,
                          PieceFournieRepository pieceFournieRepository,
                          PieceJustificativeService pieceJustificativeService,
                          HistoriqueStatutDemandeService historiqueDemandeStatutService,
                          VisaRepository visaRepository,
                          CarteResidentRepository carteResidentRepository,
                          PieceFichierService pieceFichierService,
                          DemandeurService demandeurService,
                          PasseportService passeportService,
                          VisaTransformableService visaTransformableService) {
        this.demandeRepository = demandeRepository;
        this.statutService = statutService;
        this.dossierCompletRepository = dossierCompletRepository;
        this.pieceFournieService = pieceFournieService;
        this.pieceFournieRepository = pieceFournieRepository;
        this.pieceJustificativeService = pieceJustificativeService;
        this.historiqueDemandeStatutService = historiqueDemandeStatutService;
        this.visaRepository = visaRepository;
        this.carteResidentRepository = carteResidentRepository;
        this.pieceFichierService = pieceFichierService;
        this.demandeurService = demandeurService;
        this.passeportService = passeportService;
        this.visaTransformableService = visaTransformableService;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // LECTURE
    // ─────────────────────────────────────────────────────────────────────────

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

    // ─────────────────────────────────────────────────────────────────────────
    // CAS NORMAL — CRÉATION
    // Le controller construit des stubs (non persistés) et délègue tout ici.
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public Demande createComplet(Demandeur demandeurStub, Passeport passeportStub, VisaTransformable vtStub,
                                  TypeVisa typeVisa, TypeDemande typeDemande,
                                  List<Integer> piecesFourniesIds) {
        Demandeur demandeur = demandeurStub.getId() != null
                ? demandeurService.update(demandeurStub.getId(), demandeurStub)
                : demandeurService.create(demandeurStub);

        passeportStub.setDemandeur(demandeur);
        Passeport passeport = passeportService.create(passeportStub, demandeur);

        VisaTransformable vt = null;
        if (vtStub != null) {
            vtStub.setDemandeur(demandeur);
            vtStub.setPasseport(passeport);
            vt = visaTransformableService.create(vtStub, demandeur);
        }

        Demande demande = new Demande();
        demande.setNumDemande(genererNumeroDossier());
        demande.setDemandeur(demandeur);
        demande.setVisaTransformable(vt);
        demande.setTypeVisa(typeVisa);
        demande.setTypeDemande(typeDemande);
        demande.setStatut(statutService.getStatutCree());
        demande.setDateCreation(LocalDateTime.now());
        demande = demandeRepository.save(demande);

        historiqueDemandeStatutService.creer(demande, demande.getStatut());
        pieceFournieService.creerChecklist(
                demande,
                pieceJustificativeService.findForTypeVisa(typeVisa.getId()),
                piecesFourniesIds);

        return demande;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CAS NORMAL — MODIFICATION
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public Demande updateComplet(Integer demandeId, Demandeur demandeurStub, Passeport passeportStub,
                                  VisaTransformable vtStub, TypeVisa typeVisa, TypeDemande typeDemande,
                                  List<Integer> piecesFourniesIds) {
        Demande demande = findByIdOrThrow(demandeId);
        verifierModifiable(demande);

        Demandeur demandeur = demandeurService.update(demande.getDemandeur().getId(), demandeurStub);

        passeportStub.setDemandeur(demandeur);
        Passeport passeport = passeportService.create(passeportStub, demandeur);

        VisaTransformable vt = null;
        if (vtStub != null) {
            vtStub.setDemandeur(demandeur);
            vtStub.setPasseport(passeport);
            vt = visaTransformableService.create(vtStub, demandeur);
        }

        demande.setVisaTransformable(vt);
        demande.setTypeVisa(typeVisa);
        demande.setTypeDemande(typeDemande);
        demande = demandeRepository.save(demande);

        pieceFournieService.updateChecklist(
                demande,
                pieceJustificativeService.findForTypeVisa(typeVisa.getId()),
                piecesFourniesIds);

        return demande;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CAS RATTRAPAGE — SANS DONNÉES ANTÉRIEURES
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public Demande createRattrapageComplet(Demandeur demandeurStub, Passeport passeportStub, VisaTransformable vtStub,
                                            TypeVisa typeVisa, TypeDemande typeDemande,
                                            TypeDemande typeDemandeInjection,
                                            Visa visaInjecteStub, CarteResident carteInjecteeStub) {
        Demandeur demandeur = demandeurStub.getId() != null
                ? demandeurService.update(demandeurStub.getId(), demandeurStub)
                : demandeurService.create(demandeurStub);

        passeportStub.setDemandeur(demandeur);
        Passeport passeport = passeportService.create(passeportStub, demandeur);

        VisaTransformable vt = null;
        if (vtStub != null) {
            vtStub.setDemandeur(demandeur);
            vtStub.setPasseport(passeport);
            vt = visaTransformableService.create(vtStub, demandeur);
        }

        if (visaInjecteStub != null) visaInjecteStub.setPasseport(passeport);
        if (carteInjecteeStub != null) carteInjecteeStub.setPasseport(passeport);

        // ÉTAPE A : demande d'injection (le passé — statut VISA APPROUVE)
        Demande demandeInj = new Demande();
        demandeInj.setNumDemande(genererNumeroDossier());
        demandeInj.setDemandeur(demandeur);
        demandeInj.setVisaTransformable(vt);
        demandeInj.setTypeDemande(typeDemandeInjection);
        demandeInj.setTypeVisa(typeVisa);
        demandeInj.setStatut(statutService.getStatutVisaApprouve());
        demandeInj.setDateCreation(LocalDateTime.now());
        demandeInj = demandeRepository.save(demandeInj);
        historiqueDemandeStatutService.creer(demandeInj, demandeInj.getStatut());

        if (visaInjecteStub != null) {
            visaInjecteStub.setDemande(demandeInj);
            visaRepository.save(visaInjecteStub);
        }
        if (carteInjecteeStub != null) {
            carteInjecteeStub.setDemande(demandeInj);
            carteResidentRepository.save(carteInjecteeStub);
        }

        // ÉTAPE B : demande cible (le présent — statut CREE)
        Demande demandeB = new Demande();
        demandeB.setNumDemande(genererNumeroDossier());
        demandeB.setDemandeur(demandeur);
        demandeB.setVisaTransformable(vt);
        demandeB.setTypeVisa(typeVisa);
        demandeB.setTypeDemande(typeDemande);
        demandeB.setStatut(statutService.getStatutCree());
        demandeB.setDateCreation(LocalDateTime.now());
        demandeB = demandeRepository.save(demandeB);
        historiqueDemandeStatutService.creer(demandeB, demandeB.getStatut());

        List<PieceJustificative> piecesB = pieceJustificativeService.findForTypeVisa(typeVisa.getId());
        pieceFournieService.creerChecklist(
                demandeB,
                piecesB,
                piecesB.stream().map(PieceJustificative::getId).toList());

        return demandeB;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SPRINT 3 — FINALISATION DU SCAN
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public Demande finaliserScan(Integer demandeId) {
        Demande demande = findByIdOrThrow(demandeId);
        verifierModifiable(demande);

        List<PieceFournie> toutesLesPieces = pieceFournieRepository.findAllForDemande(demandeId);

        if (toutesLesPieces.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Aucune pièce justificative configurée pour ce dossier.");
        }

        boolean toutesPresentes = toutesLesPieces.stream()
                .allMatch(pf -> Boolean.TRUE.equals(pf.getIsPresent()));
        if (!toutesPresentes) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Toutes les pièces justificatives doivent être cochées avant la finalisation.");
        }

        if (!pieceFichierService.toutesLesPiecesOntUnFichier(toutesLesPieces)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Toutes les pièces justificatives doivent avoir au moins un fichier scanné avant la finalisation.");
        }

        demande.setStatut(statutService.getStatutScanTermine());
        demande = demandeRepository.save(demande);
        historiqueDemandeStatutService.creer(demande, demande.getStatut());

        return demande;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HELPERS PRIVÉS
    // ─────────────────────────────────────────────────────────────────────────

    private void verifierModifiable(Demande demande) {
        String statut = demande.getStatut() != null ? demande.getStatut().getLibelle() : "";
        if ("SCAN TERMINÉ".equalsIgnoreCase(statut)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Ce dossier est finalisé (SCAN TERMINÉ) et ne peut plus être modifié.");
        }
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
