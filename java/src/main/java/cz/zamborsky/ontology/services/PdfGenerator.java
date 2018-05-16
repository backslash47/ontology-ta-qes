/*
 * Copyright (C) 2018 Matus Zamborsky
 * This file is part of The ONT Detective.
 *
 * The ONT Detective is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ONT Detective is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with The ONT Detective.  If not, see <http://www.gnu.org/licenses/>.
 */

package cz.zamborsky.ontology.services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Challenge pdf generator.
 */
@Service
public class PdfGenerator {
    /**
     * Generates challenge pdf.
     *
     * @param jwt JWT challenge to embed
     */
    public byte[] generate(String jwt) throws IOException {

        PDDocument doc = new PDDocument();
        PDPage page = new PDPage();

        generateContent(doc, page);

        doc.addPage(page);
        doc.setDocumentInformation(generateMetadata(jwt));

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        doc.save(output);
        doc.close();

        return output.toByteArray();
    }

    private PDDocumentInformation generateMetadata(String jwt) {
        PDDocumentInformation info = new PDDocumentInformation();
        info.setCustomMetadataValue("challenge", jwt);
        return info;
    }

    /**
     * Generates content of challenge pdf
     * @param doc Document
     * @param page Page
     */
    private void generateContent(PDDocument doc, PDPage page) throws IOException {
        // sets visible text
        PDFont courierBoldFont = PDType1Font.COURIER_BOLD;
        PDPageContentStream contentStream = new PDPageContentStream(doc, page);
        contentStream.beginText();
        contentStream.setFont(courierBoldFont, 11);
        contentStream.newLineAtOffset(10, 750);
        contentStream.showText("Sign this document with your European Qualification certificate to declare your identity.");
        contentStream.endText();
        contentStream.close();  // Stream must be closed before saving document.
    }
}
