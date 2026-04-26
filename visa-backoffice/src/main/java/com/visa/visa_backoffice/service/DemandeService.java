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
import org.springframework.transaction.annotation.Transactional;
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
        // 1. Demandeur
        Demandeur demandeur = (form.getDemandeurId() != null) 
        ? demandeurService.findByIdOrThrow(form.getDemandeurId())
        : new Demandeur();

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
        Passeport passeport = passeportService.findByNumero(form.getNumeroPasseport())
                          .orElse(new Passeport());
        
        passeport.setNumero(form.getNumeroPasseport());
        passeport.setDateDelivrance(form.getDateDelivrance());
        passeport.setDateExpiration(form.getDateExpiration());
        passeport.setDemandeur(demandeur);
        passeport = passeportService.create(passeport);

        String vtNum = form.getVisaTransformableNumero();
        VisaTransformable visaTransformable = null;

        if (vtNum != null && !vtNum.isBlank()) {
            // On récupère l'existant (avec son ID) ou on crée une instance vide
            visaTransformable = visaTransformableService.findByNumero(vtNum)
                                .orElse(new VisaTransformable());
            
            // 2. On met à jour les champs (qu'il soit nouveau ou ancien)
            visaTransformable.setNumero(vtNum);
            visaTransformable.setPasseport(passeport);
            visaTransformable.setDemandeur(demandeur);
            visaTransformable.setDateEntree(form.getVisaTransformableDateEntree());
            visaTransformable.setLieuEntree(form.getVisaTransformableLieuEntree());
            visaTransformable.setDateFinVisa(form.getVisaTransformableDateFinVisa());
            
            // 3. On sauvegarde
            // Si l'objet avait un ID (trouvé par findByNumero), Hibernate fera un UPDATE.
            // Sinon, il fera un INSERT.
            visaTransformable = visaTransformableService.save(visaTransformable);
        }

        // 3. Demande
        Demande demande = new Demande();
        demande.setNumDemande(genererNumeroDossier());
        demande.setDemandeur(demandeur);
        demande.setTypeVisa(typeVisaService.findById(form.getTypeVisaId()));
        demande.setTypeDemande(typeDemandeService.findById(form.getTypeDemandeId()));
        demande.setStatut(statutService.getStatutCree());
        demande.setDateCreation(LocalDateTime.now());
        demande.setVisaTransformable(visaTransformable);
        demande = demandeRepository.save(demande);

        

        HistoriqueStatutDemande historique = historiqueDemandeStatutService.creer(demande, demande.getStatut());

        // 4. Pièces justificatives
        pieceFournieService.creerChecklist(
                demande,
                pieceJustificativeService.findForTypeVisa(form.getTypeVisaId()),
                form.getPiecesFourniesIds());

        return demande;
    }

    @Transactional
    public Demande modifier(Integer id, DemandeForm form) {
        // 1. Récupérer la demande existante avec ses relations
        Demande demande = findByIdOrThrow(id);

        // 2. Vérification du verrouillage (Sécurité métier)
        if (demande.getStatut() == null || !"CREE".equalsIgnoreCase(demande.getStatut().getLibelle())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Modification impossible : dossier déjà verrouillé (Statut: " + 
                (demande.getStatut() != null ? demande.getStatut().getLibelle() : "INCONNU") + ")");
        }

        // 3. Mise à jour du Demandeur (L'individu reste le même)
        Demandeur demandeur = demande.getDemandeur();
        demandeur.setNom(form.getNom());
        demandeur.setPrenoms(form.getPrenoms());
        demandeur.setNomJeuneFille(form.getNomJeuneFille());
        demandeur.setDateNaissance(form.getDateNaissance());
        demandeur.setProfession(form.getProfession());
        demandeur.setAdresseMada(form.getAdresseMada());
        demandeur.setContactMada(form.getContactMada());
        
        if (form.getNationaliteId() != null) {
            demandeur.setNationalite(nationaliteService.findById(form.getNationaliteId()));
        }
        if (form.getSituationFamilialeId() != null) {
            demandeur.setSituationFamiliale(situationFamilialeService.findById(form.getSituationFamilialeId()));
        }
        demandeurService.update(demandeur.getId(), demandeur);

        // 4. Mise à jour du Passeport lié à cette demande
        Passeport passeport = demande.getPasseport();
        if (passeport != null) {
            passeport.setNumero(form.getNumeroPasseport());
            passeport.setDateDelivrance(form.getDateDelivrance());
            passeport.setDateExpiration(form.getDateExpiration());
            // On s'assure que le lien demandeur est maintenu
            passeport.setDemandeur(demandeur);
            passeportService.update(passeport.getId(), passeport);
        }

        // 5. Mise à jour ou Création du Visa Transformable
        String vtNum = form.getVisaTransformableNumero();
        if (vtNum != null && !vtNum.isBlank()) {
            // On regarde si la demande en a déjà un
            VisaTransformable vt = demande.getVisaTransformable();
            
            if (vt == null) {
                // Si la demande n'en avait pas, on vérifie si ce numéro existe déjà en base
                // pour ne pas créer un doublon qui ferait planter la contrainte UNIQUE
                vt = visaTransformableService.findByNumero(vtNum).orElse(new VisaTransformable());
            }

            // On met à jour les données (que ce soit un existant ou un nouveau)
            vt.setNumero(vtNum);
            vt.setPasseport(passeport);
            vt.setDemandeur(demandeur);
            vt.setDateEntree(form.getVisaTransformableDateEntree());
            vt.setLieuEntree(form.getVisaTransformableLieuEntree());
            vt.setDateFinVisa(form.getVisaTransformableDateFinVisa());
            
            // save() fera un UPDATE si l'ID est présent, sinon un INSERT
            vt = visaTransformableService.save(vt);
            demande.setVisaTransformable(vt);
        }

        // 6. Mise à jour des infos de la Demande elle-même
        demande.setTypeVisa(typeVisaService.findById(form.getTypeVisaId()));
        demande.setTypeDemande(typeDemandeService.findById(form.getTypeDemandeId()));
        
        // On sauvegarde la demande pour enregistrer le lien vers le visa transformable
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