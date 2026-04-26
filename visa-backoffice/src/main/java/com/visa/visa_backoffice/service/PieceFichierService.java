package com.visa.visa_backoffice.service;

import com.visa.visa_backoffice.model.PieceFichier;
import com.visa.visa_backoffice.model.PieceFournie;
import com.visa.visa_backoffice.repository.PieceFichierRepository;
import com.visa.visa_backoffice.repository.PieceFournieRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PieceFichierService {

    private final PieceFichierRepository pieceFichierRepository;
    private final PieceFournieRepository pieceFournieRepository;

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    public PieceFichierService(PieceFichierRepository pieceFichierRepository,
                               PieceFournieRepository pieceFournieRepository) {
        this.pieceFichierRepository = pieceFichierRepository;
        this.pieceFournieRepository = pieceFournieRepository;
    }

    public PieceFichier upload(Integer pieceFournieId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fichier vide ou absent.");
        }

        PieceFournie pf = pieceFournieRepository.findById(pieceFournieId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pièce fournie introuvable."));

        String extension = extractExtension(file.getOriginalFilename());
        String nomFichier = UUID.randomUUID() + "_piece" + pieceFournieId + extension;

        try {
            // toAbsolutePath() garantit un chemin absolu même si uploadDir est relatif
            Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(dir);
            Path destination = dir.resolve(nomFichier);
            // Files.copy + InputStream est fiable dans Tomcat embarqué (contrairement à transferTo)
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, destination, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Échec de l'enregistrement du fichier : " + e.getMessage());
        }

        PieceFichier pieceFichier = new PieceFichier();
        pieceFichier.setPieceFournie(pf);
        pieceFichier.setFilePath(nomFichier);
        pieceFichier.setNomOriginal(file.getOriginalFilename());
        pieceFichier.setDateUpload(LocalDateTime.now());
        return pieceFichierRepository.save(pieceFichier);
    }

    @Transactional(readOnly = true)
    public List<PieceFichier> findByPieceFournieId(Integer pieceFournieId) {
        return pieceFichierRepository.findByPieceFournie_Id(pieceFournieId);
    }

    @Transactional(readOnly = true)
    public PieceFichier findByIdOrThrow(Integer id) {
        return pieceFichierRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fichier introuvable."));
    }

    public void supprimer(Integer fichierId) {
        PieceFichier pf = findByIdOrThrow(fichierId);
        try {
            Path fichier = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(pf.getFilePath());
            Files.deleteIfExists(fichier);
        } catch (IOException ignored) {
        }
        pieceFichierRepository.delete(pf);
    }

    @Transactional(readOnly = true)
    public boolean toutesLesPiecesOntUnFichier(List<PieceFournie> piecesPresentes) {
        return piecesPresentes.stream()
                .allMatch(pf -> pieceFichierRepository.existsByPieceFournie_Id(pf.getId()));
    }

    private String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf('.'));
    }
}
