package com.visa.visa_backoffice.service;

import com.visa.visa_backoffice.model.Demande;
import com.visa.visa_backoffice.model.PieceFournie;
import com.visa.visa_backoffice.model.PieceJustificative;
import com.visa.visa_backoffice.repository.PieceFournieRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class PieceFournieService {

    private final PieceFournieRepository pieceFournieRepository;

    public PieceFournieService(PieceFournieRepository pieceFournieRepository) {
        this.pieceFournieRepository = pieceFournieRepository;
    }

    @Transactional(readOnly = true)
    public List<Integer> findPresentPieceIds(Integer demandeId) {
        if (demandeId == null) {
            return List.of();
        }
        return pieceFournieRepository.findPresentPieceIds(demandeId);
    }

    @Transactional(readOnly = true)
    public List<PieceFournie> findAllForDemande(Integer demandeId) {
        if (demandeId == null) {
            return List.of();
        }
        return pieceFournieRepository.findAllForDemande(demandeId);
    }

    @Transactional(readOnly = true)
    public java.util.Optional<PieceFournie> findByDemandeIdAndPieceJustificativeId(Integer demandeId, Integer pieceId) {
        return pieceFournieRepository.findByDemande_IdAndPieceJustificative_Id(demandeId, pieceId);
    }

    public void creerChecklist(Demande demande,
                               List<PieceJustificative> pieces,
                               List<Integer> checkedPieceIds) {
        if (demande == null || pieces == null || pieces.isEmpty()) {
            return;
        }
        List<Integer> checked = checkedPieceIds == null ? Collections.emptyList() : checkedPieceIds;
        for (PieceJustificative pj : pieces) {
            PieceFournie pf = new PieceFournie();
            pf.setDemande(demande);
            pf.setPieceJustificative(pj);
            pf.setIsPresent(checked.contains(pj.getId()));
            pieceFournieRepository.save(pf);
        }
    }

    public void updateChecklist(Demande demande,
                                List<PieceJustificative> pieces,
                                List<Integer> checkedPieceIds) {
        if (demande == null) return;
        pieceFournieRepository.deleteAllByDemande_Id(demande.getId());
        creerChecklist(demande, pieces, checkedPieceIds);
    }
}
