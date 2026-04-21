package com.visa.visa_backoffice.service;

import com.visa.visa_backoffice.dto.LoginRequest;
import com.visa.visa_backoffice.dto.LoginResponse;
import com.visa.visa_backoffice.dto.NomenclatureItem;
import com.visa.visa_backoffice.model.Utilisateur;
import com.visa.visa_backoffice.repository.SituationFamilialeRepository;
import com.visa.visa_backoffice.repository.StatutRepository;
import com.visa.visa_backoffice.repository.TypeDemandeRepository;
import com.visa.visa_backoffice.repository.TypeVisaRepository;
import com.visa.visa_backoffice.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final StatutRepository statutRepository;
    private final TypeVisaRepository typeVisaRepository;
    private final TypeDemandeRepository typeDemandeRepository;
    private final SituationFamilialeRepository situationFamilialeRepository;
    private final JwtService jwtService;

    public AuthService(UtilisateurRepository utilisateurRepository,
                       StatutRepository statutRepository,
                       TypeVisaRepository typeVisaRepository,
                       TypeDemandeRepository typeDemandeRepository,
                       SituationFamilialeRepository situationFamilialeRepository,
                       JwtService jwtService) {
        this.utilisateurRepository = utilisateurRepository;
        this.statutRepository = statutRepository;
        this.typeVisaRepository = typeVisaRepository;
        this.typeDemandeRepository = typeDemandeRepository;
        this.situationFamilialeRepository = situationFamilialeRepository;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        Utilisateur user = utilisateurRepository.findByIdentifiant(request.identifiant())
                .orElseThrow(() -> new RuntimeException("Identifiant ou mot de passe incorrect"));

        if (!user.getMdp().equals(request.mdp())) {
            throw new RuntimeException("Identifiant ou mot de passe incorrect");
        }

        List<String> permissions = user.getRole().getActions().stream()
                .map(a -> a.getNomAction())
                .toList();

        String token = jwtService.generateToken(user, permissions);

        List<NomenclatureItem> statuts = statutRepository.findAll().stream()
                .map(s -> new NomenclatureItem(s.getId(), s.getLibelle()))
                .toList();

        List<NomenclatureItem> typeVisa = typeVisaRepository.findAll().stream()
                .map(t -> new NomenclatureItem(t.getId(), t.getLibelle()))
                .toList();

        List<NomenclatureItem> typeDemande = typeDemandeRepository.findAll().stream()
                .map(t -> new NomenclatureItem(t.getId(), t.getLibelle()))
                .toList();

        List<NomenclatureItem> situationsFamiliales = situationFamilialeRepository.findAll().stream()
                .map(s -> new NomenclatureItem(s.getId(), s.getLibelle()))
                .toList();

        return new LoginResponse(
                token,
                user.getId(),
                user.getIdentifiant(),
                user.getRole().getNomRole(),
                permissions,
                statuts,
                typeVisa,
                typeDemande,
                situationsFamiliales
        );
    }
}
