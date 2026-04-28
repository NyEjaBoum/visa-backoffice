package com.visa.visa_backoffice.service;

import com.visa.visa_backoffice.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class NomenclatureService {

    private final TypeVisaService typeVisaService;
    private final TypeDemandeService typeDemandeService;
    private final NationaliteService nationaliteService;
    private final SituationFamilialeService situationFamilialeService;
    private final StatutService statutService;

    public NomenclatureService(TypeVisaService typeVisaService,
                               TypeDemandeService typeDemandeService,
                               NationaliteService nationaliteService,
                               SituationFamilialeService situationFamilialeService,
                               StatutService statutService) {
        this.typeVisaService = typeVisaService;
        this.typeDemandeService = typeDemandeService;
        this.nationaliteService = nationaliteService;
        this.situationFamilialeService = situationFamilialeService;
        this.statutService = statutService;
    }

    public List<TypeVisa> getTypesVisa() { return typeVisaService.findAll(); }
    public List<TypeDemande> getTypesDemande() { return typeDemandeService.findAll(); }
    public List<Nationalite> getNationalites() { return nationaliteService.findAll(); }
    public List<SituationFamiliale> getSituationsFamiliales() { return situationFamilialeService.findAll(); }

    public TypeVisa findTypeVisa(Integer id) { return typeVisaService.findById(id); }
    public TypeDemande findTypeDemande(Integer id) { return typeDemandeService.findById(id); }
public Nationalite findNationalite(Integer id) { return nationaliteService.findById(id); }
    public SituationFamiliale findSituationFamiliale(Integer id) { return situationFamilialeService.findById(id); }

    public Statut getStatutCree() { return statutService.getStatutCree(); }
}
