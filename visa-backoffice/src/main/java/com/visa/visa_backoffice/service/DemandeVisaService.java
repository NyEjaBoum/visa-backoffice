package com.visa.visa_backoffice.service;

import com.visa.visa_backoffice.dto.DemandeForm;
import com.visa.visa_backoffice.model.*;
import com.visa.visa_backoffice.repository.DemandeVisaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class DemandeVisaService {

    private final DemandeVisaRepository demandeVisaRepository;
    private final IndividuService individuService;
    private final PasseportService passeportService;
    private final TypeVisaService typeVisaService;
    private final TypeDemandeService typeDemandeService;
    private final StatutService statutService;
    private final NationaliteService nationaliteService;
    private final SituationFamilialeService situationFamilialeService;
    private final VisaTransformableService visaTransformableService;

    public DemandeVisaService(DemandeVisaRepository demandeVisaRepository,
                              IndividuService individuService,
                              PasseportService passeportService,
                              TypeVisaService typeVisaService,
                              TypeDemandeService typeDemandeService,
                              StatutService statutService,
                              NationaliteService nationaliteService,
                              SituationFamilialeService situationFamilialeService,
                              VisaTransformableService visaTransformableService) {
        this.demandeVisaRepository = demandeVisaRepository;
        this.individuService = individuService;
        this.passeportService = passeportService;
        this.typeVisaService = typeVisaService;
        this.typeDemandeService = typeDemandeService;
        this.statutService = statutService;
        this.nationaliteService = nationaliteService;
        this.situationFamilialeService = situationFamilialeService;
        this.visaTransformableService = visaTransformableService;
    }

    @Transactional(readOnly = true)
    public List<DemandeVisa> findAll() {
        return demandeVisaRepository.findAllWithRefs();
    }

    @Transactional(readOnly = true)
    public DemandeVisa findByIdOrThrow(Integer id) {
        return demandeVisaRepository.findByIdWithRefs(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable"));
    }

    public DemandeVisa creer(DemandeForm form) {
        // 1. Création de l'Individu
        Individu individu = new Individu();
        individu.setNom(form.getNom());
        individu.setPrenoms(form.getPrenoms());
        individu.setNomJeuneFille(form.getNomJeuneFille());
        individu.setDateNaissance(form.getDateNaissance());
        individu.setProfession(form.getProfession());
        individu.setAdresseMada(form.getAdresseMada());
        individu.setContactMada(form.getContactMada());
        if (form.getNationaliteId() != null)
            individu.setNationalite(nationaliteService.findById(form.getNationaliteId()));
        if (form.getSituationFamilialeId() != null)
            individu.setSituationFamiliale(situationFamilialeService.findById(form.getSituationFamilialeId()));
        individu = individuService.create(individu);

        // 2. Création du Passeport
        Passeport passeport = new Passeport();
        passeport.setNumeroPass(form.getNumeroPasseport());
        passeport.setDateDelivrance(form.getDateDelivrance());
        passeport.setDateExpiration(form.getDateExpiration());
        passeport.setActive(true);
        passeport = passeportService.create(individu.getId(), passeport);

        // 3. Création éventuelle du Visa transformable
        VisaTransformable visaTransformable = null;
        String vtRef = form.getVisaTransformableReference();
        if (vtRef != null && !vtRef.isBlank()) {
            visaTransformable = new VisaTransformable();
            visaTransformable.setIndividu(individu);
            visaTransformable.setPasseport(passeport);
            visaTransformable.setReference(vtRef);
            visaTransformable.setDateEntree(form.getVisaTransformableDateEntree());
            visaTransformable.setLieuEntree(form.getVisaTransformableLieuEntree());
            visaTransformable.setDateFinVisa(form.getVisaTransformableDateFinVisa());
            visaTransformable = visaTransformableService.save(visaTransformable);
        }

        // 4. Création de la Demande
        DemandeVisa demande = new DemandeVisa();
        demande.setNumDemande(genererNumeroDossier());
        demande.setIndividu(individu);
        demande.setPasseport(passeport);
        demande.setTypeVisa(typeVisaService.findById(form.getTypeVisaId()));
        demande.setTypeDemande(typeDemandeService.findById(form.getTypeDemandeId()));
        demande.setStatut(statutService.getStatutCree());
        demande.setDateCreation(LocalDateTime.now());
        demande.setVisaTransformable(visaTransformable);

        return demandeVisaRepository.save(demande);
    }

    public DemandeVisa modifier(Integer id, DemandeForm form) {
        DemandeVisa demande = demandeVisaRepository.findByIdWithRefs(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande introuvable"));

        // Règle métier : Seul le statut CREE est modifiable
        if (demande.getStatut() == null || !"CREE".equalsIgnoreCase(demande.getStatut().getLibelle())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Modification impossible : dossier déjà verrouillé");
        }

        // 1. Update Individu
        Individu individu = demande.getIndividu();
        individu.setNom(form.getNom());
        individu.setPrenoms(form.getPrenoms());
        individu.setNomJeuneFille(form.getNomJeuneFille());
        individu.setDateNaissance(form.getDateNaissance());
        individu.setProfession(form.getProfession());
        individu.setAdresseMada(form.getAdresseMada());
        individu.setContactMada(form.getContactMada());
        if (form.getNationaliteId() != null)
            individu.setNationalite(nationaliteService.findById(form.getNationaliteId()));
        else
            individu.setNationalite(null);
        if (form.getSituationFamilialeId() != null)
            individu.setSituationFamiliale(situationFamilialeService.findById(form.getSituationFamilialeId()));
        else
            individu.setSituationFamiliale(null);
        individuService.update(individu.getId(), individu);

        // 2. Update Passeport
        Passeport passeport = demande.getPasseport();
        passeport.setNumeroPass(form.getNumeroPasseport());
        passeport.setDateDelivrance(form.getDateDelivrance());
        passeport.setDateExpiration(form.getDateExpiration());
        passeportService.update(passeport.getId(), passeport);

        // 3. Update / création éventuelle du Visa transformable
        String vtRef = form.getVisaTransformableReference();
        if (vtRef != null) vtRef = vtRef.trim();
        if (vtRef != null && !vtRef.isBlank()) {
            VisaTransformable vt = demande.getVisaTransformable();
            if (vt == null) {
                vt = new VisaTransformable();
                vt.setIndividu(individu);
                vt.setPasseport(passeport);
            }
            vt.setReference(vtRef);
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

        return demandeVisaRepository.save(demande);
    }

    private String genererNumeroDossier() {
        int year = LocalDate.now().getYear();
        String prefix = "MADA-" + year + "-";
        int next = demandeVisaRepository
                .findTopByNumDemandeStartingWithOrderByNumDemandeDesc(prefix)
                .map(d -> {
                    try { return Integer.parseInt(d.getNumDemande().substring(prefix.length())); }
                    catch (NumberFormatException e) { return 0; }
                })
                .orElse(0) + 1;
        return prefix + String.format("%04d", next);
    }
}