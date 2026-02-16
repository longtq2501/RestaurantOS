package com.restaurantos.modules.table.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

/**
 * Service for generating PDF documents, specifically for QR codes.
 */
@Service
public class PDFService {

    /**
     * Information about a table's QR code to be included in the PDF.
     */
    public record TableQrInfo(String tableName, byte[] qrCodeImage) {
    }

    /**
     * Generates a PDF containing a grid of QR codes for the provided tables.
     *
     * @param restaurantName Name of the restaurant for the header.
     * @param tableInfos     List of table information and their QR images.
     * @return Byte array of the generated PDF.
     * @throws IOException If PDF generation fails.
     */
    public byte[] generateQrCodeGridPdf(String restaurantName, List<TableQrInfo> tableInfos) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // Header
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 18);
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("QR Codes for " + restaurantName);
                contentStream.endText();

                float startX = 50;
                float startY = 700;
                float qrSize = 150;
                float padding = 40;
                int cols = 3;

                for (int i = 0; i < tableInfos.size(); i++) {
                    TableQrInfo info = tableInfos.get(i);
                    int col = i % cols;
                    int row = i / cols;

                    // If we exceed the page height, we'd need to add a new page
                    // (Simplified for this version, fits ~9-12 QR codes per page)

                    float x = startX + (col * (qrSize + padding));
                    float y = startY - (row * (qrSize + padding + 20));

                    // Embed QR code image
                    PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, info.qrCodeImage(),
                            info.tableName());
                    contentStream.drawImage(pdImage, x, y, qrSize, qrSize);

                    // Table Label
                    contentStream.beginText();
                    contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                    contentStream.newLineAtOffset(x + (qrSize / 2) - 20, y - 15);
                    contentStream.showText("Table " + info.tableName());
                    contentStream.endText();
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        }
    }
}
