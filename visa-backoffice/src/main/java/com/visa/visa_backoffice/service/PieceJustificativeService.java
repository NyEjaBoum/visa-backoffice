package com.visa.visa_backoffice.service;

import com.visa.visa_backoffice.model.PieceJustificative;
import com.visa.visa_backoffice.repository.PieceJustificativeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PieceJustificativeService {

    private final PieceJustificativeRepository pieceJustificativeRepository;

    public PieceJustificativeService(PieceJustificativeRepository pieceJustificativeRepository) {
        this.pieceJustificativeRepository = pieceJustificativeRepository;
    }

    @Transactional(readOnly = true)
    public List<PieceJustificative> findAll() {
        return pieceJustificativeRepository.findAllByOrderByLibelleAsc();
    }

    @Transactional(readOnly = true)
    public List<PieceJustificative> findForTypeVisa(Integer typeVisaId) {
        if (typeVisaId == null) {
            return findAll();
        }
        return pieceJustificativeRepository.findForTypeVisa(typeVisaId);
    }
}
