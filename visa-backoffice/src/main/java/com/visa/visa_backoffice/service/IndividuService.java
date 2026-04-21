package com.visa.visa_backoffice.service;

import com.visa.visa_backoffice.model.Individu;
import com.visa.visa_backoffice.repository.IndividuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IndividuService {
    @Autowired
    private IndividuRepository individuRepository;

    public List<Individu> findAll() {
        return individuRepository.findAll();
    }

    public Optional<Individu> findById(Integer id) {
        return individuRepository.findById(id);
    }

    public Individu save(Individu individu) {
        return individuRepository.save(individu);
    }

    public void deleteById(Integer id) {
        individuRepository.deleteById(id);
    }

    public List<Individu> search(String search) {
        return individuRepository.searchByNomOrPrenom(search);
    }
}
