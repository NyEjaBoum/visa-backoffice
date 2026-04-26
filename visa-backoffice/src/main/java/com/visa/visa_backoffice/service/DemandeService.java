
package com.visa.visa_backoffice.service;

import com.visa.visa_backoffice.dto.DemandeForm;
import com.visa.visa_backoffice.model.*;
import com.visa.visa_backoffice.repository.CarteResidentRepository;
import com.visa.visa_backoffice.repository.DemandeRepository;
import com.visa.visa_backoffice.repository.DossierCompletRepository;
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
    private final PieceJustificativeService pieceJustificativeService;
    private final VisaTransformableService visaTransformableService;
    private final HistoriqueStatutDemandeService historiqueDemandeStatutService;
    private final VisaRepository visaRepository;
    private final CarteResidentRepository carteResidentRepository;

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
                          PieceJustificativeService pieceJustificativeService,
                          VisaTransformableService visaTransformableService,
                          HistoriqueStatutDemandeService historiqueDemandeStatutService,
                          VisaRepository visaRepository,
                          CarteResidentRepository carteResidentRepository) {
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
        this.visaTransformableService = visaTransformableService;
        this.historiqueDemandeStatutService = historiqueDemandeStatutService;
        this.visaRepository = visaRepository;
        this.carteResidentRepository = carteResidentRepository;
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


    @Transactional
    public Demande creer(DemandeForm form) {
        form.validateOrThrow();

        // --- 1. GESTION DU DEMANDEUR ---
        Demandeur demandeur;
        {
            demandeur = (form.getDemandeurId() != null)
                    ? demandeurService.findByIdOrThrow(form.getDemandeurId())
                    : new Demandeur();

            Nationalite nat = (form.getNationaliteId() != null) ? nationaliteService.findById(form.getNationaliteId()) : null;
            SituationFamiliale sit = (form.getSituationFamilialeId() != null) ? situationFamilialeService.findById(form.getSituationFamilialeId()) : null;

            demandeur.updateFromForm(form, nat, sit);
            demandeur = demandeurService.create(demandeur);
        }

        // --- 2. GESTION DU PASSEPORT ---
        // On le traite pour s'assurer qu'il est à jour en base, rattaché au demandeur
        Passeport passeport = passeportService.findByNumero(form.getNumeroPasseport()).orElse(null);
        {
            if (passeport != null) {
                if (demandeur.getId() != null && passeport.getDemandeur() != null && !passeport.getDemandeur().getId().equals(demandeur.getId())) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Ce passeport appartient à un autre usager.");
                }
            } else {
                passeport = new Passeport();
            }

            passeport.updateFromForm(form, demandeur);
            passeport = passeportService.create(passeport);
        }

        // --- 3. GESTION DU VISA TRANSFORMABLE ---
        VisaTransformable vt = null;
        String vtNum = form.getVisaTransformableNumero();
        if (vtNum != null && !vtNum.isBlank()) {
            vt = visaTransformableService.findByNumero(vtNum).orElse(null);

            if (vt != null) {
                if (demandeur.getId() != null && vt.getDemandeur() != null && !vt.getDemandeur().getId().equals(demandeur.getId())) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Ce visa transformable appartient à un autre usager.");
                }
            } else {
                vt = new VisaTransformable();
            }

            vt.updateFromForm(form, passeport, demandeur);
            vt = visaTransformableService.save(vt); 
        }

        // --- 4. CRÉATION ET SAUVEGARDE DE LA DEMANDE ---
        Demande demande = new Demande();
        {
            demande.setNumDemande(genererNumeroDossier());
            demande.setDemandeur(demandeur);
            // SUPPRIMÉ : demande.setPasseport(passeport); 
            demande.setVisaTransformable(vt);
            demande.setTypeVisa(typeVisaService.findById(form.getTypeVisaId()));
            demande.setTypeDemande(typeDemandeService.findById(form.getTypeDemandeId()));
            demande.setStatut(statutService.getStatutCree());
            demande.setDateCreation(LocalDateTime.now());
            
            demande = demandeRepository.save(demande);
        }

        // --- 5. FINALISATION ---
        historiqueDemandeStatutService.creer(demande, demande.getStatut());
        pieceFournieService.creerChecklist(
                demande,
                pieceJustificativeService.findForTypeVisa(form.getTypeVisaId()),
                form.getPiecesFourniesIds());

        return demande;
    }

@Transactional
public Demande modifier(Integer id, DemandeForm form) {
    form.validateOrThrow();
    Demande demande = findByIdOrThrow(id);
    Demandeur demandeur = demande.getDemandeur();

    // 1. MISE À JOUR DU DEMANDEUR
    Nationalite nat = (form.getNationaliteId() != null) ? nationaliteService.findById(form.getNationaliteId()) : null;
    SituationFamiliale sit = (form.getSituationFamilialeId() != null) ? situationFamilialeService.findById(form.getSituationFamilialeId()) : null;
    demandeur.updateFromForm(form, nat, sit);
    demandeur = demandeurService.create(demandeur);

    // 2. GESTION INTELLIGENTE DU PASSEPORT
    // On cherche si le demandeur en possède déjà un
    Passeport passeport = passeportService.findByDemandeur(demandeur);

    if (passeport == null) {
        // S'il n'en a pas, on regarde si le numéro saisi n'appartient pas déjà à quelqu'un d'autre (rattrapage)
        // Sinon, on instancie un nouveau Passeport
        passeport = passeportService.findByNumero(form.getNumeroPasseport())
                                     .orElse(new Passeport());

        // Sécurité : si on a trouvé un passeport via le numéro, il doit appartenir à NOTRE demandeur
        if (passeport.getId() != null && passeport.getDemandeur() != null 
            && !passeport.getDemandeur().getId().equals(demandeur.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ce passeport appartient déjà à un autre usager.");
        }
    }
    
    // Mise à jour (ou création si c'était un new Passeport)
    passeport.updateFromForm(form, demandeur);
    passeport = passeportService.create(passeport);

    // 3. GESTION INTELLIGENTE DU VISA TRANSFORMABLE
    String vtNum = form.getVisaTransformableNumero();
    if (vtNum != null && !vtNum.isBlank()) {
        // On vérifie d'abord si le demandeur est déjà lié à un VT
        VisaTransformable vt = visaTransformableService.findByDemandeur(demandeur);

        if (vt == null) {
            // Rattrapage par numéro ou création
            vt = visaTransformableService.findByNumero(vtNum).orElse(new VisaTransformable());

            if (vt.getId() != null && vt.getDemandeur() != null 
                && !vt.getDemandeur().getId().equals(demandeur.getId())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ce numéro de visa appartient déjà à un autre usager.");
            }
        }

        vt.updateFromForm(form, passeport, demandeur);
        vt = visaTransformableService.save(vt); 
        demande.setVisaTransformable(vt);
    }

    // 4. MISE À JOUR DE LA DEMANDE
    demande.setTypeVisa(typeVisaService.findById(form.getTypeVisaId()));
    demande.setTypeDemande(typeDemandeService.findById(form.getTypeDemandeId()));
    demande = demandeRepository.save(demande);

    // 5. MISE À JOUR CHECKLIST
    pieceFournieService.updateChecklist(
            demande, 
            pieceJustificativeService.findForTypeVisa(form.getTypeVisaId()), 
            form.getPiecesFourniesIds());

    return demande;
}

    @Transactional
    public Demande creerSansDonneesAnterieur(DemandeForm form) {
        form.validateOrThrow();

        // --- 1. DEMANDEUR, PASSEPORT, VISA TRANSFORMABLE (réutilise la logique de creer) ---
        Demandeur demandeur;
        {
            demandeur = (form.getDemandeurId() != null)
                    ? demandeurService.findByIdOrThrow(form.getDemandeurId())
                    : new Demandeur();
            Nationalite nat = (form.getNationaliteId() != null) ? nationaliteService.findById(form.getNationaliteId()) : null;
            SituationFamiliale sit = (form.getSituationFamilialeId() != null) ? situationFamilialeService.findById(form.getSituationFamilialeId()) : null;
            demandeur.updateFromForm(form, nat, sit);
            demandeur = demandeurService.create(demandeur);
        }

        Passeport passeport = passeportService.findByNumero(form.getNumeroPasseport()).orElse(null);
        {
            if (passeport != null) {
                if (demandeur.getId() != null && passeport.getDemandeur() != null && !passeport.getDemandeur().getId().equals(demandeur.getId())) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Ce passeport appartient à un autre usager.");
                }
            } else {
                passeport = new Passeport();
            }
            passeport.updateFromForm(form, demandeur);
            passeport = passeportService.create(passeport);
        }

        VisaTransformable vt = null;
        String vtNum = form.getVisaTransformableNumero();
        if (vtNum != null && !vtNum.isBlank()) {
            vt = visaTransformableService.findByNumero(vtNum).orElse(null);
            if (vt != null) {
                if (demandeur.getId() != null && vt.getDemandeur() != null && !vt.getDemandeur().getId().equals(demandeur.getId())) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Ce visa transformable appartient à un autre usager.");
                }
            } else {
                vt = new VisaTransformable();
            }
            vt.updateFromForm(form, passeport, demandeur);
            vt = visaTransformableService.save(vt);
        }

        // --- ÉTAPE A : DEMANDE D'INJECTION (le passé) ---
        Demande demandeInj = new Demande();
        demandeInj.setNumDemande(genererNumeroDossier());
        demandeInj.setDemandeur(demandeur);
        demandeInj.setVisaTransformable(vt);
        // Type: Nouveau Titre (ID 1), Statut: VISA APPROUVE (ID 4)
        demandeInj.setTypeDemande(typeDemandeService.findById(1));
        demandeInj.setTypeVisa(typeVisaService.findById(form.getTypeVisaId()));
        demandeInj.setStatut(statutService.getStatutVisaApprouve());
        demandeInj.setDateCreation(LocalDateTime.now());
        demandeInj = demandeRepository.save(demandeInj);

        historiqueDemandeStatutService.creer(demandeInj, demandeInj.getStatut());

        // Créer les lignes visa et carte_resident rattachées à l'injection
        creerVisaInjecte(demandeInj, passeport, form);
        creerCarteResidentInjectee(demandeInj, passeport, form);

        // --- ÉTAPE B : DEMANDE CIBLE (le présent) ---
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
        List<PieceJustificative> piecesB = pieceJustificativeService.findForTypeVisa(form.getTypeVisaId());
        pieceFournieService.creerChecklist(
                demandeB,
                piecesB,
                piecesB.stream().map(PieceJustificative::getId).toList());

        return demandeB;
    }

    private void creerVisaInjecte(Demande demande, Passeport passeport, DemandeForm form) {
        if (form.getNumeroVisa() == null || form.getDateDebutVisa() == null || form.getDateFinVisa() == null) {
            return;
        }
        Visa visa = new Visa();
        visa.setDemande(demande);
        visa.setNumero(form.getNumeroVisa());
        visa.setDateDebut(form.getDateDebutVisa());
        visa.setDateFin(form.getDateFinVisa());
        visa.setPasseport(passeport);
        visaRepository.save(visa);
    }

    private void creerCarteResidentInjectee(Demande demande, Passeport passeport, DemandeForm form) {
        if (form.getNumeroCarteResident() == null || form.getDateDebutCarte() == null || form.getDateFinCarte() == null) {
            return;
        }
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