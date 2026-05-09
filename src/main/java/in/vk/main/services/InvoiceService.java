package in.vk.main.services;

import java.awt.Color;
import java.io.IOException;

import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import in.vk.main.entities.Orders;
import in.vk.main.entities.User;
import jakarta.servlet.http.HttpServletResponse;

/**
 * InvoiceService handles the automated generation of purchase receipts in PDF format.
 * This service is critical for providing students with official records of their transactions.
 */
@Service
public class InvoiceService {

	public void generateInvoice(Orders order, User user, HttpServletResponse response) throws DocumentException, IOException {
		Document document = new Document(PageSize.A4);
		PdfWriter.getInstance(document, response.getOutputStream());

		document.open();
        buildInvoiceBody(order, user, document);
		document.close();
	}

    public byte[] generateInvoiceAsByteArray(Orders order, User user) throws DocumentException, IOException {
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, baos);

        document.open();
        buildInvoiceBody(order, user, document);
        document.close();

        return baos.toByteArray();
    }

    private void buildInvoiceBody(Orders order, User user, Document document) throws DocumentException {
        /* 
         * We use the corporate Indigo color palette (#4f46e5) in the PDF 
         * to ensure the brand identity is maintained in all customer documents.
         */
        // VK Academy Elite Indigo Palette
        Color themeColor = new Color(79, 70, 229); // #4f46e5

        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(22);
        fontTitle.setColor(themeColor);

        Paragraph paragraph = new Paragraph("VK ACADEMY - INVOICE", fontTitle);
        paragraph.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(paragraph);

        document.add(new Paragraph("\n"));
        
        Font fontLabel = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.DARK_GRAY);
        Font fontValue = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.BLACK);

        document.add(new Phrase("Customer Name: ", fontLabel));
        document.add(new Phrase(user.getName() + "\n", fontValue));
        
        document.add(new Phrase("Customer Email: ", fontLabel));
        document.add(new Phrase(user.getEmail() + "\n", fontValue));
        
        document.add(new Phrase("Date of Purchase: ", fontLabel));
        document.add(new Phrase(order.getDateOfPurchase() + "\n", fontValue));
        
        document.add(new Phrase("Payment ID: ", fontLabel));
        document.add(new Phrase(order.getPaymentId() + "\n", fontValue));
        
        document.add(new Paragraph("\n"));

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100f);
        table.setSpacingBefore(10);

        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(themeColor);
        cell.setPadding(8);
        cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        headerFont.setColor(Color.WHITE);

        cell.setPhrase(new Phrase("COURSE DESCRIPTION", headerFont));
        table.addCell(cell);

        cell.setPhrase(new Phrase("AMOUNT PAID", headerFont));
        table.addCell(cell);

        PdfPCell c1 = new PdfPCell(new Phrase(order.getCourseName()));
        c1.setPadding(8);
        table.addCell(c1);

        PdfPCell c2 = new PdfPCell(new Phrase("Rs. " + order.getCourseAmount()));
        c2.setPadding(8);
        c2.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        table.addCell(c2);

        document.add(table);

        document.add(new Paragraph("\n\n\n"));
        Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, Color.GRAY);
        Paragraph footer = new Paragraph("Thank you for investing in your future with VK Academy!\nFounder: Vishal Kumar", footerFont);
        footer.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(footer);
    }
}
