package com.visa.visa_backoffice.service;

// 1. GARDE UNIQUEMENT LES IMPORTS LOWAGIE
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator; // Import correct pour OpenPDF/Lowagie
import com.visa.visa_backoffice.model.Demande;
import com.visa.visa_backoffice.model.Passeport;
import com.visa.visa_backoffice.model.PieceFournie;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfService {

    private static final DateTimeFormatter DATE_FR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FR = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final Font FONT_TITLE = new Font(Font.HELVETICA, 16, Font.BOLD, Color.BLACK);
    private static final Font FONT_SECTION = new Font(Font.HELVETICA, 11, Font.BOLD, new Color(30, 64, 175));
    private static final Font FONT_LABEL = new Font(Font.HELVETICA, 9, Font.BOLD, Color.DARK_GRAY);
    private static final Font FONT_VALUE = new Font(Font.HELVETICA, 9, Font.NORMAL, Color.BLACK);
    private static final Font FONT_SMALL = new Font(Font.HELVETICA, 8, Font.ITALIC, Color.GRAY);

    public byte[] genererRecepisse(Demande demande, Passeport passeport, List<PieceFournie> piecesPresentes) {
        if (!"SCAN TERMINÉ".equalsIgnoreCase(demande.getStatut() != null ? demande.getStatut().getLibelle() : "")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Le récépissé ne peut être généré qu'après la finalisation du scan.");
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 40, 40, 50, 50);

        try {
            PdfWriter.getInstance(doc, out);
            doc.open();

            // ── En-tête ─────────────────────────────────────────────────────
            addHeader(doc, demande);

            // ── État civil ───────────────────────────────────────────────────
            addSectionTitle(doc, "1. État civil du demandeur");
            if (demande.getDemandeur() != null) {
                var d = demande.getDemandeur();
                addField(doc, "Nom", d.getNom());
                addField(doc, "Prénoms", d.getPrenoms());
                addField(doc, "Nom de jeune fille", d.getNomJeuneFille());
                addField(doc, "Date de naissance",
                        d.getDateNaissance() != null ? d.getDateNaissance().format(DATE_FR) : "—");
                addField(doc, "Nationalité",
                        d.getNationalite() != null ? d.getNationalite().getLibelle() : "—");
                addField(doc, "Situation familiale",
                        d.getSituationFamiliale() != null ? d.getSituationFamiliale().getLibelle() : "—");
                addField(doc, "Profession", d.getProfession());
                addField(doc, "Adresse à Madagascar", d.getAdresseMada());
                addField(doc, "Contact", d.getContactMada());
            }

            // ── Passeport ───────────────────────────────────────────────────
            addSectionTitle(doc, "2. Passeport");
            if (passeport != null) {
                addField(doc, "Numéro", passeport.getNumero());
                addField(doc, "Date de délivrance",
                        passeport.getDateDelivrance() != null ? passeport.getDateDelivrance().format(DATE_FR) : "—");
                addField(doc, "Date d'expiration",
                        passeport.getDateExpiration() != null ? passeport.getDateExpiration().format(DATE_FR) : "—");
            } else {
                addField(doc, "Passeport", "—");
            }

            // ── Titre actuel ────────────────────────────────────────────────
            addSectionTitle(doc, "3. Titre demandé");
            addField(doc, "Type de demande",
                    demande.getTypeDemande() != null ? demande.getTypeDemande().getLibelle() : "—");
            addField(doc, "Catégorie visa",
                    demande.getTypeVisa() != null ? demande.getTypeVisa().getLibelle() : "—");

            if (demande.getVisaTransformable() != null) {
                var vt = demande.getVisaTransformable();
                addField(doc, "Visa transformable", vt.getNumero());
                addField(doc, "Date d'entrée",
                        vt.getDateEntree() != null ? vt.getDateEntree().format(DATE_FR) : "—");
            }

            // ── Pièces fournies ──────────────────────────────────────────────
            addSectionTitle(doc, "4. Pièces justificatives reçues");
            if (piecesPresentes == null || piecesPresentes.isEmpty()) {
                doc.add(new Paragraph("Aucune pièce enregistrée.", FONT_VALUE));
            } else {
                PdfPTable table = new PdfPTable(2);
                table.setWidthPercentage(100);
                table.setWidths(new float[]{70f, 30f});
                table.setSpacingBefore(6);

                addTableHeader(table, "Pièce justificative");
                addTableHeader(table, "Statut");

                for (PieceFournie pf : piecesPresentes) {
                    String libelle = pf.getPieceJustificative() != null
                            ? pf.getPieceJustificative().getLibelle() : "—";
                    addTableCell(table, libelle);
                    addTableCell(table, "✔ Fournie & scannée");
                }
                doc.add(table);
            }

            // ── Pied de page ────────────────────────────────────────────────
            doc.add(Chunk.NEWLINE);
            Paragraph footer = new Paragraph(
                    "Document généré le " + java.time.LocalDateTime.now().format(DATETIME_FR)
                    + " — Système de gestion des visas Madagascar", FONT_SMALL);
            footer.setAlignment(Element.ALIGN_CENTER);
            doc.add(footer);

        } catch (DocumentException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erreur génération PDF : " + e.getMessage());
        } finally {
            if (doc.isOpen()) doc.close();
        }

        return out.toByteArray();
    }

    private void addHeader(Document doc, Demande demande) throws DocumentException {
        Paragraph titre = new Paragraph("ATTESTATION RÉCÉPISSÉ DE DOSSIER", FONT_TITLE);
        titre.setAlignment(Element.ALIGN_CENTER);
        titre.setSpacingAfter(4);
        doc.add(titre);

        Paragraph num = new Paragraph("Numéro de dossier : " + demande.getNumDemande(), FONT_LABEL);
        num.setAlignment(Element.ALIGN_CENTER);
        num.setSpacingAfter(16);
        doc.add(num);

        // Correction pour OpenPDF / Lowagie :
        // On crée un séparateur horizontal
        LineSeparator ls = new LineSeparator();
        ls.setLineWidth(1f);
        ls.setPercentage(100f);
        ls.setOffset(-5f); // Ajustement vertical
        
        doc.add(new Chunk(ls));
        doc.add(Chunk.NEWLINE);
    }

    private void addSectionTitle(Document doc, String titre) throws DocumentException {
        Paragraph p = new Paragraph(titre, FONT_SECTION);
        p.setSpacingBefore(14);
        p.setSpacingAfter(4);
        doc.add(p);
    }

    private void addField(Document doc, String label, String value) throws DocumentException {
        Paragraph p = new Paragraph();
        p.add(new Chunk(label + " : ", FONT_LABEL));
        p.add(new Chunk(value != null && !value.isBlank() ? value : "—", FONT_VALUE));
        p.setSpacingAfter(2);
        doc.add(p);
    }

    private void addTableHeader(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FONT_LABEL));
        cell.setBackgroundColor(new Color(219, 234, 254));
        cell.setPadding(5);
        table.addCell(cell);
    }

    private void addTableCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FONT_VALUE));
        cell.setPadding(5);
        table.addCell(cell);
    }
}
