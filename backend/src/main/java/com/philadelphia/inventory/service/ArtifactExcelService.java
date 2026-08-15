package com.philadelphia.inventory.service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.philadelphia.inventory.entity.Artifact;
import com.philadelphia.inventory.entity.User;

@Service
public class ArtifactExcelService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern(
                    "dd.MM.yyyy HH:mm"
            );


    // --------------------------------------------------
    // TÜM BULUNTULARI XLSX OLARAK OLUŞTUR
    // --------------------------------------------------

    public byte[] generateArtifactsExcel(
            List<Artifact> artifacts
    ) {

        try (
                XSSFWorkbook workbook =
                        new XSSFWorkbook();

                ByteArrayOutputStream outputStream =
                        new ByteArrayOutputStream()
        ) {

            Sheet sheet =
                    workbook.createSheet(
                            "Buluntular"
                    );


            CellStyle headerStyle =
                    createHeaderStyle(
                            workbook
                    );


            CellStyle dataStyle =
                    createDataStyle(
                            workbook
                    );


            String[] headers = {

                    "Buluntu Kodu",

                    "Tür",
                    "Form No",
                    "Envanter No",
                    "Etüt No",
                    "Torba No",
                    "Kutu No",
                    "Derinlik",
                    "Kasa",

                    "Buluntu Yeri",
                    "Mevki",
                    "Sektör",
                    "Buluntu Tarihi",
                    "Buluntu Yılı",
                    "Alan",

                    "Form",
                    "Bezeme Türü",
                    "Hamur Yapısı",
                    "Pişme",
                    "Teknik",
                    "Katkı",
                    "Katkı Miktarı",
                    "Astar Yapısı",
                    "Açı",
                    "Dönem",
                    "Cins",
                    "Munsell",

                    "Çap",
                    "Ağırlık",
                    "Uzunluk",
                    "Genişlik",
                    "Kalınlık",

                    "Çizim No",
                    "Kor. Kısım",
                    "Malzeme",
                    "Üretim Yeri",

                    "Açıklama",
                    "Kaynakça",

                    "Görünürlük",

                    "Oluşturan",
                    "Oluşturulma Tarihi",

                    "Son Düzenleyen",
                    "Son Düzenleme"
            };


            // --------------------------------------------------
            // HEADER
            // --------------------------------------------------

            Row headerRow =
                    sheet.createRow(0);

            for (
                    int columnIndex = 0;
                    columnIndex < headers.length;
                    columnIndex++
            ) {

                Cell cell =
                        headerRow.createCell(
                                columnIndex
                        );

                cell.setCellValue(
                        headers[columnIndex]
                );

                cell.setCellStyle(
                        headerStyle
                );
            }


            // --------------------------------------------------
            // DATA
            // --------------------------------------------------

            int rowIndex = 1;

            for (
                    Artifact artifact
                    : artifacts
            ) {

                Row row =
                        sheet.createRow(
                                rowIndex++
                        );


                int columnIndex = 0;


                setCell(
                        row,
                        columnIndex++,
                        artifact.getArtifactCode(),
                        dataStyle
                );


                setCell(
                        row,
                        columnIndex++,
                        artifact.getType(),
                        dataStyle
                );

                setCell(
                        row,
                        columnIndex++,
                        artifact.getFormNo(),
                        dataStyle
                );

                setCell(
                        row,
                        columnIndex++,
                        artifact.getInventoryNo(),
                        dataStyle
                );

                setCell(
                        row,
                        columnIndex++,
                        artifact.getStudyNo(),
                        dataStyle
                );

                setCell(
                        row,
                        columnIndex++,
                        artifact.getBagNo(),
                        dataStyle
                );

                setCell(
                        row,
                        columnIndex++,
                        artifact.getBoxNo(),
                        dataStyle
                );

                setCell(
                        row,
                        columnIndex++,
                        artifact.getDepth(),
                        dataStyle
                );

                setCell(
                        row,
                        columnIndex++,
                        artifact.getBox(),
                        dataStyle
                );


                setCell(
                        row,
                        columnIndex++,
                        artifact.getFindLocation(),
                        dataStyle
                );

                setCell(
                        row,
                        columnIndex++,
                        artifact.getLocality(),
                        dataStyle
                );

                setCell(
                        row,
                        columnIndex++,
                        artifact.getSector(),
                        dataStyle
                );

                setCell(
                        row,
                        columnIndex++,
                        artifact.getFindDate(),
                        dataStyle
                );

                setCell(
                        row,
                        columnIndex++,
                        artifact.getFindYear(),
                        dataStyle
                );

                setCell(
                        row,
                        columnIndex++,
                        artifact.getArea(),
                        dataStyle
                );


                setCell(
                        row,
                        columnIndex++,
                        artifact.getForm(),
                        dataStyle
                );

                setCell(
                        row,
                        columnIndex++,
                        artifact.getDecorationType(),
                        dataStyle
                );

                setCell(
                        row,
                        columnIndex++,
                        artifact.getPasteStructure(),
                        dataStyle
                );

                setCell(
                        row,
                        columnIndex++,
                        artifact.getFiring(),
                        dataStyle
                );

                setCell(
                        row,
                        columnIndex++,
                        artifact.getTechnique(),
                        dataStyle
                );

                setCell(
                        row,
                        columnIndex++,
                        artifact.getTemper(),
                        dataStyle
                );

                setCell(
                        row,
                        columnIndex++,
                        artifact.getTemperAmount(),
                        dataStyle
                );

                setCell(
                        row,
                        columnIndex++,
                        artifact.getSlipStructure(),
                        dataStyle
                );

                setCell(
                        row,
                        columnIndex++,
                        artifact.getAngle(),
                        dataStyle
                );

                setCell(
                        row,
                        columnIndex++,
                        artifact.getPeriod(),
                        dataStyle
                );

                setCell(
                        row,
                        columnIndex++,
                        artifact.getKind(),
                        dataStyle
                );

                setCell(
                        row,
                        columnIndex++,
                        artifact.getMunsell(),
                        dataStyle
                );


                setCell(
                        row,
                        columnIndex++,
                        artifact.getDiameter(),
                        dataStyle
                );

                setCell(
                        row,
                        columnIndex++,
                        artifact.getWeight(),
                        dataStyle
                );

                setCell(
                        row,
                        columnIndex++,
                        artifact.getLength(),
                        dataStyle
                );

                setCell(
                        row,
                        columnIndex++,
                        artifact.getWidth(),
                        dataStyle
                );

                setCell(
                        row,
                        columnIndex++,
                        artifact.getThickness(),
                        dataStyle
                );


                setCell(
                        row,
                        columnIndex++,
                        artifact.getDrawingNo(),
                        dataStyle
                );

                setCell(
                        row,
                        columnIndex++,
                        artifact.getPreservedPart(),
                        dataStyle
                );

                setCell(
                        row,
                        columnIndex++,
                        artifact.getMaterial(),
                        dataStyle
                );

                setCell(
                        row,
                        columnIndex++,
                        artifact.getProductionPlace(),
                        dataStyle
                );


                setCell(
                        row,
                        columnIndex++,
                        artifact.getDescription(),
                        dataStyle
                );

                setCell(
                        row,
                        columnIndex++,
                        artifact.getBibliography(),
                        dataStyle
                );


                setCell(
                        row,
                        columnIndex++,
                        artifact.getVisibility() != null
                                ? artifact
                                        .getVisibility()
                                        .name()
                                : null,
                        dataStyle
                );


                setCell(
                        row,
                        columnIndex++,
                        getUserName(
                                artifact.getCreatedBy()
                        ),
                        dataStyle
                );

                setCell(
                        row,
                        columnIndex++,
                        formatDateTime(
                                artifact.getCreatedAt()
                        ),
                        dataStyle
                );


                setCell(
                        row,
                        columnIndex++,
                        getUserName(
                                artifact.getUpdatedBy()
                        ),
                        dataStyle
                );

                setCell(
                        row,
                        columnIndex,
                        formatDateTime(
                                artifact.getUpdatedAt()
                        ),
                        dataStyle
                );
            }


            // --------------------------------------------------
            // SHEET AYARLARI
            // --------------------------------------------------

            sheet.createFreezePane(
                    0,
                    1
            );


            sheet.setAutoFilter(
                    new org.apache.poi.ss.util.CellRangeAddress(
                            0,
                            0,
                            0,
                            headers.length - 1
                    )
            );


            for (
                    int columnIndex = 0;
                    columnIndex < headers.length;
                    columnIndex++
            ) {

                sheet.autoSizeColumn(
                        columnIndex
                );


                int currentWidth =
                        sheet.getColumnWidth(
                                columnIndex
                        );


                int maximumWidth =
                        45 * 256;


                if (
                        currentWidth >
                        maximumWidth
                ) {

                    sheet.setColumnWidth(
                            columnIndex,
                            maximumWidth
                    );
                }
            }


            workbook.write(
                    outputStream
            );


            return outputStream
                    .toByteArray();

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Artifact Excel file could not be generated.",
                    exception
            );
        }
    }


    // --------------------------------------------------
    // HEADER STYLE
    // --------------------------------------------------

    private CellStyle createHeaderStyle(
            XSSFWorkbook workbook
    ) {

        CellStyle style =
                workbook.createCellStyle();


        Font font =
                workbook.createFont();

        font.setBold(true);

        font.setColor(
                IndexedColors.WHITE
                        .getIndex()
        );


        style.setFont(
                font
        );


        style.setFillForegroundColor(
                IndexedColors.DARK_BLUE
                        .getIndex()
        );

        style.setFillPattern(
                FillPatternType.SOLID_FOREGROUND
        );


        style.setAlignment(
                HorizontalAlignment.CENTER
        );

        style.setVerticalAlignment(
                VerticalAlignment.CENTER
        );


        style.setWrapText(
                true
        );


        return style;
    }


    // --------------------------------------------------
    // DATA STYLE
    // --------------------------------------------------

    private CellStyle createDataStyle(
            XSSFWorkbook workbook
    ) {

        CellStyle style =
                workbook.createCellStyle();


        style.setVerticalAlignment(
                VerticalAlignment.TOP
        );


        style.setWrapText(
                true
        );


        return style;
    }


    // --------------------------------------------------
    // CELL
    // --------------------------------------------------

    private void setCell(
            Row row,
            int columnIndex,
            Object value,
            CellStyle style
    ) {

        Cell cell =
                row.createCell(
                        columnIndex
                );


        if (value == null) {

            cell.setCellValue("");

        } else {

            cell.setCellValue(
                    String.valueOf(
                            value
                    )
            );
        }


        cell.setCellStyle(
                style
        );
    }


    // --------------------------------------------------
    // USER NAME
    // --------------------------------------------------

    private String getUserName(
            User user
    ) {

        if (user == null) {
            return null;
        }


        String firstName =
                user.getFirstName() != null
                        ? user.getFirstName()
                        : "";


        String lastName =
                user.getLastName() != null
                        ? user.getLastName()
                        : "";


        return (
                firstName
                        + " "
                        + lastName
        ).trim();
    }


    // --------------------------------------------------
    // DATE TIME
    // --------------------------------------------------

    private String formatDateTime(
            java.time.LocalDateTime value
    ) {

        if (value == null) {
            return null;
        }


        return value.format(
                DATE_TIME_FORMATTER
        );
    }
}