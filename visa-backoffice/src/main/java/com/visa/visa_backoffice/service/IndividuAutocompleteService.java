package com.visa.visa_backoffice.service;

import com.visa.visa_backoffice.dto.IndividuAutocompleteDTO;
import com.visa.visa_backoffice.repository.IndividuAutocompleteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class IndividuAutocompleteService {
    @Autowired
    private IndividuAutocompleteRepository individuAutocompleteRepository;

    public List<IndividuAutocompleteDTO> search(String search) {
        List<Object[]> results = individuAutocompleteRepository.searchIndividuAutocomplete(search);
        List<IndividuAutocompleteDTO> dtos = new ArrayList<>();
        for (Object[] row : results) {
            IndividuAutocompleteDTO dto = new IndividuAutocompleteDTO();
            dto.setIndividuId(row[0] != null ? Long.valueOf(row[0].toString()) : null);
            dto.setNom((String) row[1]);
            dto.setPrenom((String) row[2]);
            dto.setDateNaissance(row[3] != null ? row[3].toString() : null);
            dto.setContact((String) row[4]);
            dto.setPasseportNumero((String) row[5]);
            dto.setDateDelivrance(row[6] != null ? row[6].toString() : null);
            dto.setDateExpiration(row[7] != null ? row[7].toString() : null);

            dto.setNomJeuneFille((String) row[8]);
            dto.setSituationFamiliale((String) row[9]);
            dto.setNationalite((String) row[10]);
            dto.setProfession((String) row[11]);
            dto.setAdresseMada((String) row[12]);
            dtos.add(dto);
        }
        return dtos;
    }
}
