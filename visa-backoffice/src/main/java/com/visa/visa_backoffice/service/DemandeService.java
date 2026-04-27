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
import java.util.*;
import java.util.stream.Collectors;

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
    // CRÉATION (Moteur universel)
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public Demande createComplet(Demandeur demandeurStub,
                                 Passeport passeportStub,
                                 VisaTransformable vtStub,
                                 TypeVisa typeVisa,
                                 TypeDemande typeDemande,
                                 List<Integer> piecesFourniesIds,
                                 Statut statutInitial) {

        // 1. Gérer le demandeur (création ou récupération si ID présent)
        Demandeur demandeur = (demandeurStub.getId() != null) 
                ? demandeurService.update(demandeurStub.getId(), demandeurStub)
                : demandeurService.create(demandeurStub);

        // 2. Gérer le passeport
        passeportStub.setDemandeur(demandeur);
        Passeport passeport = passeportService.create(passeportStub, demandeur);

        // 3. Gérer le Visa Transformable
        VisaTransformable vt = null;
        if (vtStub != null) {
            vtStub.setDemandeur(demandeur);
            vtStub.setPasseport(passeport);
            vt = visaTransformableService.create(vtStub, demandeur);
        }

        // 4. Créer la demande
        Demande demande = new Demande();
        demande.setNumDemande(genererNumeroDossier());
        if (demande.getQrToken() == null) {
            demande.setQrToken(UUID.randomUUID());
        }
        demande.setDemandeur(demandeur);
        demande.setVisaTransformable(vt);
        demande.setTypeVisa(typeVisa);
        demande.setTypeDemande(typeDemande);
        demande.setStatut(statutInitial);
        demande.setDateCreation(LocalDateTime.now());
        
        demande = demandeRepository.save(demande);

        // 5. Historique & Checklist
        historiqueDemandeStatutService.creer(demande, statutInitial);

        pieceFournieService.creerChecklist(
                demande,
                pieceJustificativeService.findForTypeVisa(typeVisa.getId()),
                piecesFourniesIds
        );

        return demande;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // MODIFICATION (Unique porte d'entrée pour les mises à jour)
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public Demande updateComplet(Demande demande,
                                 Demandeur demandeurStub,
                                 Passeport passeportStub,
                                 VisaTransformable vtStub,
                                 TypeVisa typeVisa,
                                 TypeDemande typeDemande,
                                 List<Integer> piecesFourniesIds,
                                 Statut nouveauStatut) {

        if (demande == null || demande.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Demande obligatoire.");
        }
        verifierModifiable(demande);

        // Mise à jour du demandeur lié
        Demandeur demandeur = demandeurService.update(demande.getDemandeur().getId(), demandeurStub);

        // Mise à jour/Recréation du passeport
        passeportStub.setDemandeur(demandeur);
        Passeport passeport = passeportService.create(passeportStub, demandeur);

        // Mise à jour du VT
        VisaTransformable vt = null;
        if (vtStub != null) {
            vtStub.setDemandeur(demandeur);
            vtStub.setPasseport(passeport);
            vt = visaTransformableService.create(vtStub, demandeur);
        }

        // Update des champs demande
        demande.setDemandeur(demandeur);
        demande.setVisaTransformable(vt);
        demande.setTypeVisa(typeVisa);
        demande.setTypeDemande(typeDemande);

        if (nouveauStatut != null) {
            demande.setStatut(nouveauStatut);
        }

        demande = demandeRepository.save(demande);

        // Sync checklist
        pieceFournieService.updateChecklist(
                demande,
                pieceJustificativeService.findForTypeVisa(typeVisa.getId()),
                piecesFourniesIds
        );

        if (nouveauStatut != null) {
            historiqueDemandeStatutService.creer(demande, nouveauStatut);
        }

        return demande;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CAS RATTRAPAGE (Orchestration utilisant createComplet)
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public Demande createRattrapageComplet(Demandeur demandeurStub, Passeport passeportStub, VisaTransformable vtStub,
                                           TypeVisa typeVisa, TypeDemande typeDemandeCible,
                                           TypeDemande typeDemandeInjection,
                                           Visa visaInjecteStub, CarteResident carteInjecteeStub) {

        // ÉTAPE A : Création du passé (Dossier Injection)
        // On force toutes les pièces comme "fournies" pour l'injection
        List<Integer> allPiecesIds = pieceJustificativeService.findForTypeVisa(typeVisa.getId())
                .stream().map(PieceJustificative::getId).toList();

        Demande demandeInj = createComplet(
                demandeurStub, passeportStub, vtStub, typeVisa, 
                typeDemandeInjection, allPiecesIds, statutService.getStatutVisaApprouve()
        );

        // Liaison des documents physiques à l'injection
        if (visaInjecteStub != null) {
            visaInjecteStub.setDemande(demandeInj);
            visaInjecteStub.setPasseport(demandeInj.getVisaTransformable().getPasseport());
            visaRepository.save(visaInjecteStub);
        }
        if (carteInjecteeStub != null) {
            carteInjecteeStub.setDemande(demandeInj);
            carteInjecteeStub.setPasseport(demandeInj.getVisaTransformable().getPasseport());
            carteResidentRepository.save(carteInjecteeStub);
        }

        // ÉTAPE B : Création du présent (Dossier Cible)
        // On repart du demandeur/passeport/VT créés à l'étape A pour éviter les doublons
        return createComplet(
                demandeInj.getDemandeur(),
                demandeInj.getVisaTransformable().getPasseport(),
                demandeInj.getVisaTransformable(),
                typeVisa,
                typeDemandeCible,
                allPiecesIds, // Par défaut tout coché en rattrapage
                statutService.getStatutCree()
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
    // FINALISATION SCAN
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public Demande finaliserScan(Demande demande, Demandeur demandeurStub, Passeport passeportStub,
                                 VisaTransformable vtStub, TypeVisa typeVisa, TypeDemande typeDemande,
                                 List<Integer> piecesFourniesIds) {
        
        // 1. Validations métier avant délégation
        List<PieceJustificative> piecesAttendues = pieceJustificativeService.findForTypeVisa(typeVisa.getId());
        
        // (Logique de vérification des fichiers scannés...)
        List<PieceFournie> existantes = pieceFournieRepository.findAllForDemande(demande.getId());
        if (!pieceFichierService.toutesLesPiecesOntUnFichier(existantes)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fichiers manquants.");
        }

        // 2. Délégation à l'update unique
        return updateComplet(demande, demandeurStub, passeportStub, vtStub, typeVisa, 
                            typeDemande, piecesFourniesIds, statutService.getStatutScanTermine());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────────────────────

    private void verifierModifiable(Demande demande) {
        if ("SCAN TERMINÉ".equalsIgnoreCase(demande.getStatut().getLibelle())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Dossier verrouillé.");
        }
    }

    private String genererNumeroDossier() {
        int year = LocalDate.now().getYear();
        String prefix = "MADA-" + year + "-";
        int next = demandeRepository
                .findTopByNumDemandeStartingWithOrderByNumDemandeDesc(prefix)
                .map(d -> {
                    try {
                        return Integer.parseInt(d.getNumDemande().substring(prefix.length()));
                    } catch (Exception e) { return 0; }
                }).orElse(0) + 1;
        return prefix + String.format("%04d", next);
    }
}