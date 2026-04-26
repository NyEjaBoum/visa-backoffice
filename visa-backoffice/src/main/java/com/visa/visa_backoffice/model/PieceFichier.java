package com.visa.visa_backoffice.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "piece_fichier")
public class PieceFichier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_piece_fournie", nullable = false)
    private PieceFournie pieceFournie;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "nom_original")
    private String nomOriginal;

    @Column(name = "date_upload")
    private LocalDateTime dateUpload;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public PieceFournie getPieceFournie() { return pieceFournie; }
    public void setPieceFournie(PieceFournie pieceFournie) { this.pieceFournie = pieceFournie; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getNomOriginal() { return nomOriginal; }
    public void setNomOriginal(String nomOriginal) { this.nomOriginal = nomOriginal; }

    public LocalDateTime getDateUpload() { return dateUpload; }
    public void setDateUpload(LocalDateTime dateUpload) { this.dateUpload = dateUpload; }
}
