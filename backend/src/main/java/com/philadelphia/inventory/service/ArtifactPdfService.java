package com.philadelphia.inventory.service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.philadelphia.inventory.entity.Artifact;
import com.philadelphia.inventory.entity.ArtifactPhoto;

import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.FontFactory;
import org.openpdf.text.Image;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;

@Service
public class ArtifactPdfService {

    private final ArtifactPhotoService artifactPhotoService;
    private final FileStorageService fileStorageService;

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern(
                    "dd.MM.yyyy HH:mm"
            );

    public ArtifactPdfService(
            ArtifactPhotoService artifactPhotoService,
            FileStorageService fileStorageService
    ) {
        this.artifactPhotoService =
                artifactPhotoService;

        this.fileStorageService =
                fileStorageService;
    }


    // --------------------------------------------------
    // TEK BULUNTU PDF
    // --------------------------------------------------

    public byte[] generateArtifactPdf(
            Artifact artifact
    ) {

        try {

            ByteArrayOutputStream outputStream =
                    new ByteArrayOutputStream();

            Document document =
                    new Document(
                            PageSize.A4,
                            40,
                            40,
                            45,
                            45
                    );

            PdfWriter.getInstance(
                    document,
                    outputStream
            );

            document.open();

            addTitle(
                    document,
                    artifact
            );

            addBasicInformation(
                    document,
                    artifact
            );

            addFindInformation(
                    document,
                    artifact
            );

            addTypologyInformation(
                    document,
                    artifact
            );

            addMeasurements(
                    document,
                    artifact
            );

            addDescription(
                    document,
                    artifact
            );

            addPhotos(
                    document,
                    artifact
            );

            addAuditInformation(
                    document,
                    artifact
            );

            document.close();

            return outputStream.toByteArray();

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Artifact PDF could not be generated.",
                    exception
            );
        }
    }


    // --------------------------------------------------
    // BAŞLIK
    // --------------------------------------------------

    private void addTitle(
            Document document,
            Artifact artifact
    ) throws DocumentException {

        Font titleFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        18,
                        new Color(
                                15,
                                39,
                                71
                        )
                );

        Font subtitleFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA,
                        10,
                        new Color(
                                100,
                                116,
                                139
                        )
                );

        Paragraph title =
                new Paragraph(
                        "Philadelphia Inventory",
                        titleFont
                );

        title.setAlignment(
                Element.ALIGN_CENTER
        );

        title.setSpacingAfter(6);

        document.add(title);


        Paragraph subtitle =
                new Paragraph(
                        "Buluntu Envanter Kaydı",
                        subtitleFont
                );

        subtitle.setAlignment(
                Element.ALIGN_CENTER
        );

        subtitle.setSpacingAfter(20);

        document.add(subtitle);


        Font artifactCodeFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        15
                );

        Paragraph artifactCode =
                new Paragraph(
                        value(
                                artifact.getArtifactCode()
                        ),
                        artifactCodeFont
                );

        artifactCode.setAlignment(
                Element.ALIGN_CENTER
        );

        artifactCode.setSpacingAfter(5);

        document.add(artifactCode);


        Paragraph visibility =
                new Paragraph(
                        artifact.getVisibility() != null
                                ? artifact.getVisibility().name()
                                : "-",
                        subtitleFont
                );

        visibility.setAlignment(
                Element.ALIGN_CENTER
        );

        visibility.setSpacingAfter(22);

        document.add(visibility);
    }


    // --------------------------------------------------
    // TEMEL BİLGİLER
    // --------------------------------------------------

    private void addBasicInformation(
            Document document,
            Artifact artifact
    ) throws DocumentException {

        PdfPTable table =
                createSectionTable(
                        "Temel Bilgiler"
                );

        addField(
                table,
                "Tür",
                artifact.getType()
        );

        addField(
                table,
                "Form No",
                artifact.getFormNo()
        );

        addField(
                table,
                "Envanter No",
                artifact.getInventoryNo()
        );

        addField(
                table,
                "Etüt No",
                artifact.getStudyNo()
        );

        addField(
                table,
                "Torba No",
                artifact.getBagNo()
        );

        addField(
                table,
                "Kutu No",
                artifact.getBoxNo()
        );

        addField(
                table,
                "Derinlik",
                artifact.getDepth()
        );

        addField(
                table,
                "Kasa",
                artifact.getBox()
        );

        document.add(table);
    }


    // --------------------------------------------------
    // BULUNTU BİLGİLERİ
    // --------------------------------------------------

    private void addFindInformation(
            Document document,
            Artifact artifact
    ) throws DocumentException {

        PdfPTable table =
                createSectionTable(
                        "Buluntu Bilgileri"
                );

        addField(
                table,
                "Buluntu Yeri",
                artifact.getFindLocation()
        );

        addField(
                table,
                "Mevki",
                artifact.getLocality()
        );

        addField(
                table,
                "Sektör",
                artifact.getSector()
        );

        addField(
                table,
                "Buluntu Tarihi",
                artifact.getFindDate()
        );

        addField(
                table,
                "Buluntu Yılı",
                artifact.getFindYear()
        );

        addField(
                table,
                "Alan",
                artifact.getArea()
        );

        document.add(table);
    }


    // --------------------------------------------------
    // TİPOLOJİ
    // --------------------------------------------------

    private void addTypologyInformation(
            Document document,
            Artifact artifact
    ) throws DocumentException {

        PdfPTable table =
                createSectionTable(
                        "Tipoloji ve Teknik Özellikler"
                );

        addField(
                table,
                "Form",
                artifact.getForm()
        );

        addField(
                table,
                "Bezeme Türü",
                artifact.getDecorationType()
        );

        addField(
                table,
                "Hamur Yapısı",
                artifact.getPasteStructure()
        );

        addField(
                table,
                "Pişme",
                artifact.getFiring()
        );

        addField(
                table,
                "Teknik",
                artifact.getTechnique()
        );

        addField(
                table,
                "Katkı",
                artifact.getTemper()
        );

        addField(
                table,
                "Katkı Miktarı",
                artifact.getTemperAmount()
        );

        addField(
                table,
                "Astar Yapısı",
                artifact.getSlipStructure()
        );

        addField(
                table,
                "Açı",
                artifact.getAngle()
        );

        addField(
                table,
                "Dönem",
                artifact.getPeriod()
        );

        addField(
                table,
                "Cins",
                artifact.getKind()
        );

        addField(
                table,
                "Munsell",
                artifact.getMunsell()
        );

        document.add(table);
    }


    // --------------------------------------------------
    // ÖLÇÜLER
    // --------------------------------------------------

    private void addMeasurements(
            Document document,
            Artifact artifact
    ) throws DocumentException {

        PdfPTable table =
                createSectionTable(
                        "Ölçüler ve Diğer Bilgiler"
                );

        addField(
                table,
                "Çap",
                artifact.getDiameter()
        );

        addField(
                table,
                "Ağırlık",
                artifact.getWeight()
        );

        addField(
                table,
                "Uzunluk",
                artifact.getLength()
        );

        addField(
                table,
                "Genişlik",
                artifact.getWidth()
        );

        addField(
                table,
                "Kalınlık",
                artifact.getThickness()
        );

        addField(
                table,
                "Çizim No",
                artifact.getDrawingNo()
        );

        addField(
                table,
                "Kor. Kısım",
                artifact.getPreservedPart()
        );

        addField(
                table,
                "Malzeme",
                artifact.getMaterial()
        );

        addField(
                table,
                "Üretim Yeri",
                artifact.getProductionPlace()
        );

        document.add(table);
    }


    // --------------------------------------------------
    // AÇIKLAMA / KAYNAKÇA
    // --------------------------------------------------

    private void addDescription(
            Document document,
            Artifact artifact
    ) throws DocumentException {

        addSectionTitle(
                document,
                "Açıklama ve Kaynakça"
        );

        addLongField(
                document,
                "Açıklama",
                artifact.getDescription()
        );

        addLongField(
                document,
                "Kaynakça",
                artifact.getBibliography()
        );
    }


    // --------------------------------------------------
    // FOTOĞRAFLAR
    // --------------------------------------------------

    private void addPhotos(
            Document document,
            Artifact artifact
    ) throws Exception {

        List<ArtifactPhoto> photos =
                artifactPhotoService
                        .getActivePhotos(
                                artifact.getId()
                        );

        addSectionTitle(
                document,
                "Fotoğraflar"
        );

        if (photos.isEmpty()) {

            Paragraph empty =
                    new Paragraph(
                            "Bu buluntuya ait fotoğraf bulunmamaktadır.",
                            bodyFont()
                    );

            empty.setSpacingAfter(14);

            document.add(empty);

            return;
        }

        for (
                ArtifactPhoto photo
                : photos
        ) {

            byte[] photoBytes =
                    fileStorageService.loadFile(
                            photo.getStoragePath()
                    );

            Image image =
                    Image.getInstance(
                            photoBytes
                    );

            image.scaleToFit(
                    440,
                    320
            );

            image.setAlignment(
                    Element.ALIGN_CENTER
            );

            document.add(image);


            String photoLabel =
                    photo.getPhotoNo() != null
                            && !photo.getPhotoNo().isBlank()

                            ? "Fotoğraf No: "
                                    + photo.getPhotoNo()

                            : "Fotoğraf";


            Paragraph photoInfo =
                    new Paragraph(
                            photoLabel
                                    + "  |  "
                                    + value(
                                            photo.getFileName()
                                    ),
                            smallFont()
                    );

            photoInfo.setAlignment(
                    Element.ALIGN_CENTER
            );

            photoInfo.setSpacingAfter(14);

            document.add(photoInfo);
        }
    }


    // --------------------------------------------------
    // KAYIT BİLGİLERİ
    // --------------------------------------------------

    private void addAuditInformation(
            Document document,
            Artifact artifact
    ) throws DocumentException {

        PdfPTable table =
                createSectionTable(
                        "Kayıt Bilgileri"
                );

        addField(
                table,
                "Oluşturan",
                getUserName(
                        artifact.getCreatedBy()
                )
        );

        addField(
                table,
                "Oluşturulma Tarihi",
                formatDateTime(
                        artifact.getCreatedAt()
                )
        );

        addField(
                table,
                "Son Düzenleyen",
                getUserName(
                        artifact.getUpdatedBy()
                )
        );

        addField(
                table,
                "Son Düzenleme",
                formatDateTime(
                        artifact.getUpdatedAt()
                )
        );

        document.add(table);
    }


    // --------------------------------------------------
    // SECTION TABLE
    // --------------------------------------------------

    private PdfPTable createSectionTable(
            String title
    ) throws DocumentException {

        PdfPTable table =
                new PdfPTable(2);

        table.setWidthPercentage(100);

        table.setWidths(
                new float[]{
                        1f,
                        1f
                }
        );

        table.setSpacingBefore(8);
        table.setSpacingAfter(15);


        PdfPCell titleCell =
                new PdfPCell(
                        new Phrase(
                                title,
                                sectionFont()
                        )
                );

        titleCell.setColspan(2);

        titleCell.setPadding(9);

        titleCell.setBackgroundColor(
                new Color(
                        241,
                        245,
                        249
                )
        );

        titleCell.setBorderColor(
                new Color(
                        226,
                        232,
                        240
                )
        );

        table.addCell(
                titleCell
        );

        return table;
    }


    // --------------------------------------------------
    // FIELD
    // --------------------------------------------------

    private void addField(
            PdfPTable table,
            String label,
            Object value
    ) {

        PdfPCell cell =
                new PdfPCell();

        cell.setPadding(9);

        cell.setBorderColor(
                new Color(
                        226,
                        232,
                        240
                )
        );


        Paragraph labelParagraph =
                new Paragraph(
                        label,
                        labelFont()
                );

        labelParagraph.setSpacingAfter(3);

        cell.addElement(
                labelParagraph
        );


        Paragraph valueParagraph =
                new Paragraph(
                        value(value),
                        bodyFont()
                );

        cell.addElement(
                valueParagraph
        );

        table.addCell(
                cell
        );
    }


    // --------------------------------------------------
    // LONG FIELD
    // --------------------------------------------------

    private void addLongField(
            Document document,
            String label,
            Object value
    ) throws DocumentException {

        Paragraph labelParagraph =
                new Paragraph(
                        label,
                        labelFont()
                );

        labelParagraph.setSpacingAfter(5);

        document.add(
                labelParagraph
        );


        Paragraph valueParagraph =
                new Paragraph(
                        value(value),
                        bodyFont()
                );

        valueParagraph.setSpacingAfter(14);

        document.add(
                valueParagraph
        );
    }


    // --------------------------------------------------
    // SECTION TITLE
    // --------------------------------------------------

    private void addSectionTitle(
            Document document,
            String title
    ) throws DocumentException {

        Paragraph paragraph =
                new Paragraph(
                        title,
                        sectionFont()
                );

        paragraph.setSpacingBefore(8);
        paragraph.setSpacingAfter(10);

        document.add(
                paragraph
        );
    }


    // --------------------------------------------------
    // FONTLAR
    // --------------------------------------------------

    private Font sectionFont() {

        return FontFactory.getFont(
                FontFactory.HELVETICA_BOLD,
                11,
                new Color(
                        15,
                        39,
                        71
                )
        );
    }


    private Font labelFont() {

        return FontFactory.getFont(
                FontFactory.HELVETICA_BOLD,
                8,
                new Color(
                        100,
                        116,
                        139
                )
        );
    }


    private Font bodyFont() {

        return FontFactory.getFont(
                FontFactory.HELVETICA,
                9,
                new Color(
                        51,
                        65,
                        85
                )
        );
    }


    private Font smallFont() {

        return FontFactory.getFont(
                FontFactory.HELVETICA,
                8,
                new Color(
                        100,
                        116,
                        139
                )
        );
    }


    // --------------------------------------------------
    // HELPERS
    // --------------------------------------------------

    private String value(
            Object value
    ) {

        if (value == null) {
            return "-";
        }

        String text =
                String.valueOf(
                        value
                );

        if (text.isBlank()) {
            return "-";
        }

        return text;
    }


    private String formatDateTime(
            java.time.LocalDateTime value
    ) {

        if (value == null) {
            return "-";
        }

        return value.format(
                DATE_TIME_FORMATTER
        );
    }


    private String getUserName(
            com.philadelphia.inventory.entity.User user
    ) {

        if (user == null) {
            return "-";
        }

        return user.getFirstName()
                + " "
                + user.getLastName();
    }
}