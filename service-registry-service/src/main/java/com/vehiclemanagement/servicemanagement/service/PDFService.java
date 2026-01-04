package com.vehiclemanagement.servicemanagement.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Map;

@Service
@Slf4j
public class PDFService {

    public byte[] generateInvoicePDF(Map<String, Object> invoiceData) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(document, outputStream);

            document.open();

            // Header
            Paragraph header = new Paragraph("VEHICLE SERVICE INVOICE",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, new BaseColor(0, 102, 102)));
            header.setAlignment(Element.ALIGN_CENTER);
            header.setSpacingAfter(20f);
            document.add(header);

            // Service Details
            document.add(createDetailsTable(invoiceData));

            // Bill Summary
            document.add(createBillTable(invoiceData));

            // Parts Table
            if (invoiceData.get("partsUsed") != null) {
                document.add(createPartsTable(invoiceData));
            }

            // Footer
            Paragraph footer = new Paragraph(
                    "\nVehicle Service Management System\n123 Service Road, Service City\nPhone: +91-1234-567890",
                    FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY));
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();

            log.info("PDF generated successfully, size: {} bytes", outputStream.size());
            return outputStream.toByteArray();

        } catch (Exception e) {
            log.error("Error generating PDF", e);
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    private PdfPTable createDetailsTable(Map<String, Object> data) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(20f);

        addRow(table, "Service ID:", getValueOrDash(data.get("serviceRequestId")));
        addRow(table, "Type:", getValueOrDash(data.get("requestType")));
        addRow(table, "Status:", getValueOrDash(data.get("status")));
        addRow(table, "Bay:", getValueOrDash(data.get("bayNumber")));
        addRow(table, "Date:", getValueOrDash(data.get("completedDate")));

        return table;
    }

    private String getValueOrDash(Object val) {
        return val != null ? String.valueOf(val) : "-";
    }

    @SuppressWarnings("unchecked")
    private PdfPTable createBillTable(Map<String, Object> data) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(20f);

        Object billObj = data.get("bill");

        // If bill is null or not a map, show a message instead of crashing
        if (billObj == null || !(billObj instanceof Map)) {
            PdfPCell cell = new PdfPCell(new Phrase("Bill details not available"));
            cell.setColspan(2);
            table.addCell(cell);
            return table;
        }

        Map<String, Object> bill = (Map<String, Object>) billObj;

        if (bill != null) {
            double laborCost = getDouble(bill.get("laborCost"));
            double partsCost = getDouble(bill.get("partsCost"));
            double tax = getDouble(bill.get("tax"));
            double total = getDouble(bill.get("totalAmount"));

            addRow(table, "Labor Cost:", "₹ " + String.format("%.2f", laborCost));
            addRow(table, "Parts Cost:", "₹ " + String.format("%.2f", partsCost));
            addRow(table, "Tax:", "₹ " + String.format("%.2f", tax));

            PdfPCell totalLabel = new PdfPCell(new Phrase("Total Amount:",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            totalLabel.setBackgroundColor(new BaseColor(200, 200, 200));
            table.addCell(totalLabel);

            PdfPCell totalValue = new PdfPCell(new Phrase("₹ " + String.format("%.2f", total),
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new BaseColor(0, 102, 102))));
            totalValue.setBackgroundColor(new BaseColor(200, 200, 200));
            table.addCell(totalValue);
        }

        return table;
    }

    @SuppressWarnings("unchecked")
    private PdfPTable createPartsTable(Map<String, Object> data) throws DocumentException {
        Object partsObj = data.get("partsUsed");
        if (partsObj == null || !(partsObj instanceof java.util.List)) {
            return new PdfPTable(1);
        }

        java.util.List<Object> parts = (java.util.List<Object>) partsObj;

        if (parts.isEmpty()) {
            return new PdfPTable(1);
        }

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingBefore(20f);

        // Header
        String[] headers = { "Part Name", "Qty", "Unit Price", "Total" };
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header,
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.WHITE)));
            cell.setBackgroundColor(new BaseColor(0, 102, 102));
            table.addCell(cell);
        }

        // Rows
        for (Object part : parts) {
            if (part instanceof Map) {
                Map<String, Object> p = (Map<String, Object>) part;
                table.addCell(new PdfPCell(new Phrase(getValueOrDash(p.get("partName")))));
                double qty = getDouble(p.get("quantity"));
                double price = getDouble(p.get("unitPrice"));
                table.addCell(new PdfPCell(new Phrase(String.valueOf((int) qty))));
                table.addCell(new PdfPCell(new Phrase("₹ " + String.format("%.2f", price))));
                table.addCell(new PdfPCell(new Phrase("₹ " + String.format("%.2f", qty * price))));
            }
        }

        return table;
    }

    private void addRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11)));
        labelCell.setBackgroundColor(new BaseColor(240, 240, 240));
        table.addCell(labelCell);

        table.addCell(new PdfPCell(new Phrase(value)));
    }

    private double getDouble(Object value) {
        if (value == null)
            return 0.0;
        if (value instanceof Number)
            return ((Number) value).doubleValue();
        try {
            return Double.parseDouble(value.toString());
        } catch (Exception e) {
            return 0.0;
        }
    }
}
