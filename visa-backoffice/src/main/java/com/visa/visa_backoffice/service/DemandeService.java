package com.visa.visa_backoffice.service;

import com.visa.visa_backoffice.dto.DemandeForm;
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
    private final DemandeurService demandeurService;
    private final PasseportService passeportService;
    private final TypeVisaService typeVisaService;
    private final TypeDemandeService typeDemandeService;
    private final StatutService statutService;
    private final NationaliteService nationaliteService;
    private final SituationFamilialeService situationFamilialeService;
    private final DossierCompletRepository dossierCompletRepository;
    private final PieceFournieService pieceFournieService;
    private final PieceFournieRepository pieceFournieRepository;
    private final PieceJustificativeService pieceJustificativeService;
    private final VisaTransformableService visaTransformableService;
    private final HistoriqueStatutDemandeService historiqueDemandeStatutService;
    private final VisaRepository visaRepository;
    private final CarteResidentRepository carteResidentRepository;
    private final PieceFichierService pieceFichierService;

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
                          PieceFournieRepository pieceFournieRepository,
                          PieceJustificativeService pieceJustificativeService,
                          VisaTransformableService visaTransformableService,
                          HistoriqueStatutDemandeService historiqueDemandeStatutService,
                          VisaRepository visaRepository,
                          CarteResidentRepository carteResidentRepository,
                          PieceFichierService pieceFichierService) {
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
        this.pieceFournieRepository = pieceFournieRepository;
        this.pieceJustificativeService = pieceJustificativeService;
        this.visaTransformableService = visaTransformableService;
        this.historiqueDemandeStatutService = historiqueDemandeStatutService;
        this.visaRepository = visaRepository;
        this.carteResidentRepository = carteResidentRepository;
        this.pieceFichierService = pieceFichierService;
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
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public Demande creer(DemandeForm form) {
        form.validateOrThrow();

        Demandeur demandeur = creerOuMettreAJourDemandeur(form);
        Passeport passeport = creerOuMettreAJourPasseport(form, demandeur);
        VisaTransformable vt = creerOuMettreAJourVT(form, passeport, demandeur);

        Demande demande = new Demande();
        demande.setNumDemande(genererNumeroDossier());
        demande.setDemandeur(demandeur);
        demande.setVisaTransformable(vt);
        demande.setTypeVisa(typeVisaService.findById(form.getTypeVisaId()));
        demande.setTypeDemande(typeDemandeService.findById(form.getTypeDemandeId()));
        demande.setStatut(statutService.getStatutCree());
        demande.setDateCreation(LocalDateTime.now());
        demande = demandeRepository.save(demande);

        historiqueDemandeStatutService.creer(demande, demande.getStatut());
        pieceFournieService.creerChecklist(
                demande,
                pieceJustificativeService.findForTypeVisa(form.getTypeVisaId()),
                form.getPiecesFourniesIds());

        return demande;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CAS NORMAL — MODIFICATION
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public Demande modifier(Integer id, DemandeForm form) {
        form.validateOrThrow();
        Demande demande = findByIdOrThrow(id);
        verifierDossierModifiable(demande);

        Demandeur demandeur = demande.getDemandeur();
        Nationalite nat = (form.getNationaliteId() != null) ? nationaliteService.findById(form.getNationaliteId()) : null;
        SituationFamiliale sit = (form.getSituationFamilialeId() != null) ? situationFamilialeService.findById(form.getSituationFamilialeId()) : null;
        demandeur.updateFromForm(form, nat, sit);
        demandeur = demandeurService.create(demandeur);

        Passeport passeport = passeportService.findByDemandeur(demandeur);
        if (passeport == null) {
            passeport = passeportService.findByNumero(form.getNumeroPasseport()).orElse(new Passeport());
            if (passeport.getId() != null && passeport.getDemandeur() != null
                    && !passeport.getDemandeur().getId().equals(demandeur.getId())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ce passeport appartient déjà à un autre usager.");
            }
        }
        passeport.updateFromForm(form, demandeur);
        passeportService.create(passeport);

        String vtNum = form.getVisaTransformableNumero();
        if (vtNum != null && !vtNum.isBlank()) {
            // Priorité : le VT déjà lié à CETTE demande (évite la collision unique sur numero)
            VisaTransformable vt = demande.getVisaTransformable();

            if (vt == null || !vtNum.trim().equals(vt.getNumero())) {
                // Pas de VT lié, ou l'opérateur a saisi un numero différent → cherche par numero
                VisaTransformable vtParNumero = visaTransformableService.findByNumero(vtNum).orElse(null);
                if (vtParNumero != null) {
                    if (vtParNumero.getDemandeur() != null
                            && !vtParNumero.getDemandeur().getId().equals(demandeur.getId())) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT,
                                "Ce numéro de visa appartient déjà à un autre usager.");
                    }
                    vt = vtParNumero;
                } else if (vt == null) {
                    vt = new VisaTransformable();
                }
                // Si vt != null et numero différent : on met à jour l'existant avec le nouveau numero
            }

            vt.updateFromForm(form, passeport, demandeur);
            vt = visaTransformableService.save(vt);
            demande.setVisaTransformable(vt);
        }

        demande.setTypeVisa(typeVisaService.findById(form.getTypeVisaId()));
        demande.setTypeDemande(typeDemandeService.findById(form.getTypeDemandeId()));
        demande = demandeRepository.save(demande);

        pieceFournieService.updateChecklist(
                demande,
                pieceJustificativeService.findForTypeVisa(form.getTypeVisaId()),
                form.getPiecesFourniesIds());

        return demande;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CAS RATTRAPAGE — SANS DONNÉES ANTÉRIEURES
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public Demande creerSansDonneesAnterieur(DemandeForm form) {
        form.validateOrThrow();

        Demandeur demandeur = creerOuMettreAJourDemandeur(form);
        Passeport passeport = creerOuMettreAJourPasseport(form, demandeur);
        VisaTransformable vt = creerOuMettreAJourVT(form, passeport, demandeur);

        // ÉTAPE A : demande d'injection (le passé — statut VISA APPROUVE)
        Demande demandeInj = new Demande();
        demandeInj.setNumDemande(genererNumeroDossier() + "-INJ");
        demandeInj.setDemandeur(demandeur);
        demandeInj.setVisaTransformable(vt);
        demandeInj.setTypeDemande(typeDemandeService.findById(1));
        demandeInj.setTypeVisa(typeVisaService.findById(form.getTypeVisaId()));
        demandeInj.setStatut(statutService.getStatutVisaApprouve());
        demandeInj.setDateCreation(LocalDateTime.now());
        demandeInj = demandeRepository.save(demandeInj);
        historiqueDemandeStatutService.creer(demandeInj, demandeInj.getStatut());

        creerVisaInjecte(demandeInj, passeport, form);
        creerCarteResidentInjectee(demandeInj, passeport, form);

        // ÉTAPE B : demande cible (le présent — statut CREE)
        Demande demandeB = new Demande();
        demandeB.setNumDemande(genererNumeroDossier());
        demandeB.setDemandeur(demandeur);
        demandeB.setVisaTransformable(vt);
        demandeB.setTypeVisa(typeVisaService.findById(form.getTypeVisaId()));
        demandeB.setTypeDemande(typeDemandeService.findById(form.getTypeDemandeId()));
        demandeB.setStatut(statutService.getStatutCree());
        demandeB.setDateCreation(LocalDateTime.now());
        demandeB = demandeRepository.save(demandeB);
        historiqueDemandeStatutService.creer(demandeB, demandeB.getStatut());

        pieceFournieService.creerChecklist(
                demandeB,
                pieceJustificativeService.findForTypeVisa(form.getTypeVisaId()),
                form.getPiecesFourniesIds());

        return demandeB;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SPRINT 3 — FINALISATION DU SCAN
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public Demande finaliserScan(Integer demandeId) {
        Demande demande = findByIdOrThrow(demandeId);
        verifierDossierModifiable(demande);

        List<PieceFournie> piecesPresentes = pieceFournieRepository
                .findPresentesForDemande(demandeId);

        if (piecesPresentes.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Aucune pièce cochée. Cochez au moins une pièce avant de finaliser.");
        }

        if (!pieceFichierService.toutesLesPiecesOntUnFichier(piecesPresentes)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Toutes les pièces cochées doivent avoir au moins un fichier scanné avant la finalisation.");
        }

        demande.setStatut(statutService.getStatutScanTermine());
        demande = demandeRepository.save(demande);
        historiqueDemandeStatutService.creer(demande, demande.getStatut());

        return demande;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HELPERS PRIVÉS
    // ─────────────────────────────────────────────────────────────────────────

    private void verifierDossierModifiable(Demande demande) {
        String statut = demande.getStatut() != null ? demande.getStatut().getLibelle() : "";
        if ("SCAN TERMINÉ".equalsIgnoreCase(statut)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Ce dossier est finalisé (SCAN TERMINÉ) et ne peut plus être modifié.");
        }
    }

    private Demandeur creerOuMettreAJourDemandeur(DemandeForm form) {
        Demandeur demandeur = (form.getDemandeurId() != null)
                ? demandeurService.findByIdOrThrow(form.getDemandeurId())
                : new Demandeur();
        Nationalite nat = (form.getNationaliteId() != null) ? nationaliteService.findById(form.getNationaliteId()) : null;
        SituationFamiliale sit = (form.getSituationFamilialeId() != null) ? situationFamilialeService.findById(form.getSituationFamilialeId()) : null;
        demandeur.updateFromForm(form, nat, sit);
        return demandeurService.create(demandeur);
    }

    private Passeport creerOuMettreAJourPasseport(DemandeForm form, Demandeur demandeur) {
        Passeport passeport = passeportService.findByNumero(form.getNumeroPasseport()).orElse(null);
        if (passeport != null) {
            if (demandeur.getId() != null && passeport.getDemandeur() != null
                    && !passeport.getDemandeur().getId().equals(demandeur.getId())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ce passeport appartient à un autre usager.");
            }
        } else {
            passeport = new Passeport();
        }
        passeport.updateFromForm(form, demandeur);
        return passeportService.create(passeport);
    }

    private VisaTransformable creerOuMettreAJourVT(DemandeForm form, Passeport passeport, Demandeur demandeur) {
        String vtNum = form.getVisaTransformableNumero();
        if (vtNum == null || vtNum.isBlank()) return null;

        VisaTransformable vt = visaTransformableService.findByNumero(vtNum).orElse(null);
        if (vt != null) {
            if (demandeur.getId() != null && vt.getDemandeur() != null
                    && !vt.getDemandeur().getId().equals(demandeur.getId())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ce visa transformable appartient à un autre usager.");
            }
        } else {
            vt = new VisaTransformable();
        }
        vt.updateFromForm(form, passeport, demandeur);
        return visaTransformableService.save(vt);
    }

    private void creerVisaInjecte(Demande demande, Passeport passeport, DemandeForm form) {
        if (form.getNumeroVisa() == null || form.getDateDebutVisa() == null || form.getDateFinVisa() == null) return;
        Visa visa = new Visa();
        visa.setDemande(demande);
        visa.setNumero(form.getNumeroVisa());
        visa.setDateDebut(form.getDateDebutVisa());
        visa.setDateFin(form.getDateFinVisa());
        visa.setPasseport(passeport);
        visaRepository.save(visa);
    }

    private void creerCarteResidentInjectee(Demande demande, Passeport passeport, DemandeForm form) {
        if (form.getNumeroCarteResident() == null || form.getDateDebutCarte() == null || form.getDateFinCarte() == null) return;
        CarteResident carte = new CarteResident();
        carte.setDemande(demande);
        carte.setNumero(form.getNumeroCarteResident());
        carte.setDateDebut(form.getDateDebutCarte());
        carte.setDateFin(form.getDateFinCarte());
        carte.setPasseport(passeport);
        carteResidentRepository.save(carte);
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
