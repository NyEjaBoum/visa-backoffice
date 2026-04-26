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
    private final VisaTransformableService visaTransformableService;
    private final HistoriqueStatutDemandeService historiqueDemandeStatutService;

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
                          HistoriqueStatutDemandeService historiqueDemandeStatutService) {
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

        // 1. Demandeur
        Demandeur demandeur = (form.getDemandeurId() != null)
                ? demandeurService.findByIdOrThrow(form.getDemandeurId())
                : new Demandeur();
        demandeur.updateFromForm(form,
                form.getNationaliteId() != null ? nationaliteService.findById(form.getNationaliteId()) : null,
                form.getSituationFamilialeId() != null ? situationFamilialeService.findById(form.getSituationFamilialeId()) : null);
        demandeur = (demandeur.getId() == null)
                ? demandeurService.create(demandeur)
                : demandeurService.save(demandeur);

        // 2. Passeport
        Passeport passeport = passeportService.findByNumero(form.getNumeroPasseport())
                .orElse(new Passeport());
        passeport.updateFromForm(form, demandeur);
        passeport = passeportService.create(passeport);

        // 3. VisaTransformable (optionnel)
        VisaTransformable visaTransformable = null;
        String vtNum = form.getVisaTransformableNumero();
        if (vtNum != null && !vtNum.isBlank()) {
            visaTransformable = visaTransformableService.findByNumero(vtNum)
                    .orElse(new VisaTransformable());
            visaTransformable.updateFromForm(form, passeport, demandeur);
            visaTransformable = visaTransformableService.save(visaTransformable);
        }

        // 4. Demande
        Demande demande = new Demande();
        demande.setNumDemande(genererNumeroDossier());
        demande.setDemandeur(demandeur);
        demande.setPasseport(passeport);
        demande.setTypeVisa(typeVisaService.findById(form.getTypeVisaId()));
        demande.setTypeDemande(typeDemandeService.findById(form.getTypeDemandeId()));
        demande.setStatut(statutService.getStatutCree());
        demande.setDateCreation(LocalDateTime.now());
        demande.setVisaTransformable(visaTransformable);
        demande = demandeRepository.save(demande);

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

        // 1. Récupérer la demande existante
        Demande demande = findByIdOrThrow(id);

        // 2. Vérification du verrouillage
        if (demande.getStatut() == null || !"CREE".equalsIgnoreCase(demande.getStatut().getLibelle())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Modification impossible : dossier déjà verrouillé (Statut: " +
                    (demande.getStatut() != null ? demande.getStatut().getLibelle() : "INCONNU") + ")");
        }

        // 3. Mise à jour du Demandeur
        Demandeur demandeur = demande.getDemandeur();
        demandeur.updateFromForm(form,
                form.getNationaliteId() != null ? nationaliteService.findById(form.getNationaliteId()) : null,
                form.getSituationFamilialeId() != null ? situationFamilialeService.findById(form.getSituationFamilialeId()) : null);
        demandeur = demandeurService.save(demandeur);

        // 4. Mise à jour du Passeport
        Passeport passeport = demande.getPasseport() != null
                ? demande.getPasseport()
                : passeportService.findByNumero(form.getNumeroPasseport()).orElse(new Passeport());
        passeport.updateFromForm(form, demandeur);
        passeport = passeportService.create(passeport);
        demande.setPasseport(passeport);

        // 5. Mise à jour ou Création du Visa Transformable (optionnel)
        String vtNum = form.getVisaTransformableNumero();
        if (vtNum != null && !vtNum.isBlank()) {
            VisaTransformable vt = (demande.getVisaTransformable() != null)
                    ? demande.getVisaTransformable()
                    : visaTransformableService.findByNumero(vtNum).orElse(new VisaTransformable());
            vt.updateFromForm(form, passeport, demandeur);
            vt = visaTransformableService.save(vt);
            demande.setVisaTransformable(vt);
        }

        // 6. Mise à jour de la Demande
        demande.setTypeVisa(typeVisaService.findById(form.getTypeVisaId()));
        demande.setTypeDemande(typeDemandeService.findById(form.getTypeDemandeId()));
        demande = demandeRepository.save(demande);

        // 7. Mise à jour de la checklist des pièces
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