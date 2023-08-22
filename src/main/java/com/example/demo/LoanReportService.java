package com.example.demo;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

//@Service
public class LoanReportService {

    private Map<String, String> accessoriesMap = new HashMap<>();
    {
        accessoriesMap.put("accPIUSI", "PIUSI K33 Flowmeter");
        accessoriesMap.put("accManual", "Manual Nozzle");
        accessoriesMap.put("accAutomatic", "Automatic Nozzle");
        accessoriesMap.put("accWired", "Wired-Reinforced Hose");
        accessoriesMap.put("accWater", "Water Pump");
    }

    public static void main(String[] args) throws Exception {
        byte[] pdfByteArray = new LoanReportService().create(new LoanRequest1());
        try (FileOutputStream fos = new FileOutputStream("output.pdf")) {
            fos.write(pdfByteArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void addFooter (PdfDocument pdfDoc, Document document)
    {
        String footer = "Test footer";
        /*String newFile = System.IO.Path.Combine (HttpContext.Current.Server.MapPath ("pdf_repo"), "finalFile.pdf");
        PdfDocument pdfDoc = new PdfDocument (new PdfReader (destination), new PdfWriter (newFile));*/
        //Document doc = new Document (pdfDoc);
        Paragraph foot = new Paragraph (footer);
        //foot.set (8);
        float x = 300; //559
        float y = 0; //806
        int numberOfPages = pdfDoc.getNumberOfPages();
        for (int i = 1; i <= numberOfPages; i++)
        {
            document.showTextAligned (foot, x, y, TextAlignment.CENTER, VerticalAlignment.BOTTOM);
        }

        //delete temporary PDF
        //File.Delete (destination);
    }


    public byte[] create(LoanRequest1 loanRequest) throws Exception {
        //String dest = "iTextHelloWorld.pdf";
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(out);
        //Rectangle rectangle = new Rectangle(600, 1100);
        PdfDocument pdfDoc = new PdfDocument(writer);

        //pdfDoc.addNewPage();
        //pdfDoc.setDefaultPageSize(PageSize.A4);
        //Document document = new Document(pdfDoc, new PageSize(rectangle));
        Document document = new Document(pdfDoc);

        PdfFont font = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
        document.setFont(font);
        document.setFontSize(10);

        //Step 0: Add Image
        document.add(createPanImage());

        // Step 1: Add address
        document.add(createAddress(loanRequest));


        // Step 1.1: EQUIPMENT LOAN AGREEMENT
        Paragraph equipmentLoanAgreement = new Paragraph("EQUIPMENT LOAN AGREEMENT");
        PdfFont HELVETICA_BOLD_FONT = PdfFontFactory.createFont(FontConstants.TIMES_BOLD);
        equipmentLoanAgreement.setFont(HELVETICA_BOLD_FONT);
        equipmentLoanAgreement.setUnderline();

        float[] pointColumnWidths = {180F, 250F};
        Table table = new Table(pointColumnWidths);

        Cell cell_sn = new Cell();
        cell_sn.add(new Cell().add(new Paragraph("")));
        cell_sn.setBorder(Border.NO_BORDER);
        table.addCell(cell_sn);

        Cell cell_Description = new Cell();
        cell_Description.add(new Cell().add(new Paragraph("EQUIPMENT LOAN AGREEMENT")));
        cell_Description.setBorder(Border.NO_BORDER);
        cell_Description.setFont(HELVETICA_BOLD_FONT);
        cell_Description.setUnderline();
        table.addCell(cell_Description);

        document.add(table);

        // Step 2: EQUIPMENT ON LOAN
        Paragraph equipmentTitle = new Paragraph("EQUIPMENT ON LOAN");

        //PdfFont HELVETICA_BOLD_FONT = PdfFontFactory.createFont(FontConstants.TIMES_BOLD);
        equipmentTitle.setFont(HELVETICA_BOLD_FONT);
        equipmentTitle.setUnderline();

        document.add(equipmentTitle);
        document.add(createEquipmentOnLoanTable(loanRequest));


        Paragraph term_para_title = new Paragraph("TERMS OF EQUIPMENT LOAN");
        term_para_title.setFont(HELVETICA_BOLD_FONT);
        term_para_title.setUnderline();
        document.add(term_para_title);

        createTermsOfEquipment(document);

        Paragraph accept_title = new Paragraph("ACCEPTANCE OF EQUIPMENT LOAN AGREEMENT");
        accept_title.setFont(HELVETICA_BOLD_FONT);
        accept_title.setUnderline();
        document.add(accept_title);

        Paragraph accept_param_1 = new Paragraph("This is binding contract. By signing meant that you have read, understood and accepted the stipulated terms.");
        document.add(accept_param_1);

        document.add(createAcceptSign());

        addFooter(pdfDoc, document);
        document.close();

        return out.toByteArray();
    }


    public void createTermsOfEquipment(Document document) throws Exception {

        float[] pointColumnWidths = {10F, 2000F};
        Table table = new Table(pointColumnWidths);

        Paragraph term_para_1 = new Paragraph("The equipment is solely for the storage and dispenser of our product(s), A.D.O. it should not be used for other products or by other contractor/suppliers.");
        document.add(term_para_1);

        Cell cell_one = new Cell();
        cell_one.add(new Cell().add(new Paragraph("1.")));
        cell_one.setBorder(Border.NO_BORDER);
        table.addCell(cell_one);

        Cell cell_one_content = new Cell();
        cell_one_content.add(new Cell().add(new Paragraph("The Equipment is on loan to you solely for the purpose of storing and using Automotive Diesel Oil\n" +
                "(A.D.O) purchased directly from us, and should not be used for other products or by other\n" +
                "contractor/suppliers.")));
        cell_one_content.setBorder(Border.NO_BORDER);
        //cell_one_content.setTextAlignment(TextAlignment.JUSTIFIED_ALL);
        table.addCell(cell_one_content);

        Cell cell_two = new Cell();
        cell_two.add(new Cell().add(new Paragraph("2.")));
        cell_two.setBorder(Border.NO_BORDER);
        table.addCell(cell_two);

        Cell cell_two_content = new Cell();
        cell_two_content.add(new Cell().add(new Paragraph("By entering into this Loan Agreement, you acknowledge receipt of the Equipment from us and you\n" +
                "expressly agree upon request to return the Equipment to us in good operating condition, ordinary wear\n" +
                "and tear accepted. All Equipment on loan here is presumed to be in good operating condition unless\n" +
                "otherwise stated.")));
        cell_two_content.setBorder(Border.NO_BORDER);
        table.addCell(cell_two_content);

        Cell cell_three = new Cell();
        cell_three.add(new Cell().add(new Paragraph("3.")));
        cell_three.setBorder(Border.NO_BORDER);
        table.addCell(cell_three);

        Cell cell_three_content = new Cell();
        cell_three_content.add(new Cell().add(new Paragraph("The term of this Loan Agreement shall be for two years ('Term') + one year at our option, or unless we\n" +
                "give written notice of termination of this Loan Agreement. Loan Agreement Term from 20 Jun 2023 – 19\n" +
                "Jun 2025.")));
        cell_three_content.setBorder(Border.NO_BORDER);
        table.addCell(cell_three_content);

        Cell cell_four = new Cell();
        cell_four.add(new Cell().add(new Paragraph("4.")));
        cell_four.setBorder(Border.NO_BORDER);
        table.addCell(cell_four);

        Cell cell_four_content = new Cell();
        cell_four_content.add(new Cell().add(new Paragraph("In consideration of the Equipment loan, you agree as follow:")));
        cell_four_content.setBorder(Border.NO_BORDER);
        table.addCell(cell_four_content);

        document.add(table);

        /* ============== End of first table ============================== */

        float[] threeColumnWidths = {10F, 10F, 2000F};
        Table table_four = new Table(threeColumnWidths);

        Cell cell_four_one_empty = new Cell();
        cell_four_one_empty.add(new Cell().add(new Paragraph("")));
        cell_four_one_empty.setBorder(Border.NO_BORDER);
        table_four.addCell(cell_four_one_empty);

        Cell cell_four_one = new Cell();
        cell_four_one.add(new Cell().add(new Paragraph("4.1")));
        cell_four_one.setBorder(Border.NO_BORDER);
        table_four.addCell(cell_four_one);

        Cell cell_four_one_content = new Cell();
        cell_four_one_content.add(new Cell().add(new Paragraph("You understand and agree to all the terms in our 'Service Agreement – To Supply, Deliver and\n" +
                "Service of Diesel' as are in effect from time to time and which are incorporated by reference\n" +
                "herein.")));
        cell_four_one_content.setBorder(Border.NO_BORDER);
        table_four.addCell(cell_four_one_content);

        Cell cell_four_two_empty = new Cell();
        cell_four_two_empty.add(new Cell().add(new Paragraph("")));
        cell_four_two_empty.setBorder(Border.NO_BORDER);
        table_four.addCell(cell_four_two_empty);

        Cell cell_four_two = new Cell();
        cell_four_two.add(new Cell().add(new Paragraph("4.2")));
        cell_four_two.setBorder(Border.NO_BORDER);
        table_four.addCell(cell_four_two);

        Cell cell_four_two_content = new Cell();
        cell_four_two_content.add(new Cell().add(new Paragraph("You will keep safely and use carefully the Equipment and will not sell or remove the Equipment or\n" +
                "any part thereof from the place where it was installed.")));
        cell_four_two_content.setBorder(Border.NO_BORDER);
        table_four.addCell(cell_four_two_content);


        Cell cell_four_three_empty = new Cell();
        cell_four_three_empty.add(new Cell().add(new Paragraph("")));
        cell_four_three_empty.setBorder(Border.NO_BORDER);
        table_four.addCell(cell_four_three_empty);

        Cell cell_four_three = new Cell();
        cell_four_three.add(new Cell().add(new Paragraph("4.3")));
        cell_four_three.setBorder(Border.NO_BORDER);
        table_four.addCell(cell_four_three);

        Cell cell_four_three_content = new Cell();
        cell_four_three_content.add(new Cell().add(new Paragraph("You will not remove any labelling or paint from the Equipment.")));
        cell_four_three_content.setBorder(Border.NO_BORDER);
        table_four.addCell(cell_four_three_content);

        Cell cell_four_four_empty = new Cell();
        cell_four_four_empty.add(new Cell().add(new Paragraph("")));
        cell_four_four_empty.setBorder(Border.NO_BORDER);
        table_four.addCell(cell_four_four_empty);

        Cell cell_four_four = new Cell();
        cell_four_four.add(new Cell().add(new Paragraph("4.4")));
        cell_four_four.setBorder(Border.NO_BORDER);
        table_four.addCell(cell_four_four);

        Cell cell_four_four_content = new Cell();
        cell_four_four_content.add(new Cell().add(new Paragraph("You will be responsible for any and all damages to the Equipment during the continuation of the\n" +
                "Term.")));
        cell_four_four_content.setBorder(Border.NO_BORDER);
        table_four.addCell(cell_four_four_content);


        Cell cell_four_five_empty = new Cell();
        cell_four_five_empty.add(new Cell().add(new Paragraph("")));
        cell_four_five_empty.setBorder(Border.NO_BORDER);
        table_four.addCell(cell_four_five_empty);

        Cell cell_four_five = new Cell();
        cell_four_five.add(new Cell().add(new Paragraph("4.5")));
        cell_four_five.setBorder(Border.NO_BORDER);
        table_four.addCell(cell_four_five);

        Cell cell_four_five_content = new Cell();
        cell_four_five_content.add(new Cell().add(new Paragraph("You will allow us, or our agents to enter your premises at any reasonable time to inspect the\n" +
                "Equipment.")));
        cell_four_five_content.setBorder(Border.NO_BORDER);
        table_four.addCell(cell_four_five_content);

        Cell cell_four_six_empty = new Cell();
        cell_four_six_empty.add(new Cell().add(new Paragraph("")));
        cell_four_six_empty.setBorder(Border.NO_BORDER);
        table_four.addCell(cell_four_six_empty);

        Cell cell_four_six = new Cell();
        cell_four_six.add(new Cell().add(new Paragraph("4.6")));
        cell_four_six.setBorder(Border.NO_BORDER);
        table_four.addCell(cell_four_six);

        Cell cell_four_six_content = new Cell();
        cell_four_six_content.add(new Cell().add(new Paragraph("You agree to purchase from us your entire ADO for use where the Equipment is installed, being\n" +
                "understood that no other supplier is to fill or tamper with the Equipment.")));
        cell_four_six_content.setBorder(Border.NO_BORDER);
        table_four.addCell(cell_four_six_content);

        document.add(table_four);


        float[] twoColumnWidths = {10F,2000F};
        Table table_bottom = new Table(twoColumnWidths);

        Cell cell_bottom_one_empty = new Cell();
        cell_bottom_one_empty.add(new Cell().add(new Paragraph("5")));
        cell_bottom_one_empty.setBorder(Border.NO_BORDER);
        table_bottom.addCell(cell_bottom_one_empty);

        Cell cell_bottom_one = new Cell();
        cell_bottom_one.add(new Cell().add(new Paragraph("Should other contractors/suppliers use the Equipment, we reserve the right to retrieve the Equipment\n" +
                "(filled/empty). 48-hours&#39; notice will be given to you to make the necessary arrangement(s) for the\n" +
                "extraction of our Equipment.")));
        cell_bottom_one.setBorder(Border.NO_BORDER);
        table_bottom.addCell(cell_bottom_one);

        Cell cell_bottom_six_empty = new Cell();
        cell_bottom_six_empty.add(new Cell().add(new Paragraph("6")));
        cell_bottom_six_empty.setBorder(Border.NO_BORDER);
        table_bottom.addCell(cell_bottom_six_empty);

        Cell cell_bottom_six = new Cell();
        cell_bottom_six.add(new Cell().add(new Paragraph("Upon project completion/cessation of our service(s), we reserve the rights to take back the Equipment\n" +
                "(filled/empty) by giving written notice. 48 hours after the written notice, if the Equipment is still in use\n" +
                "or unable to be extracted by us, an immediate penalty of S$250.00 / month (excl. GST) will be\n" +
                "chargeable.")));
        cell_bottom_six.setBorder(Border.NO_BORDER);
        table_bottom.addCell(cell_bottom_six);

        Cell cell_bottom_seven_empty = new Cell();
        cell_bottom_seven_empty.add(new Cell().add(new Paragraph("7")));
        cell_bottom_seven_empty.setBorder(Border.NO_BORDER);
        table_bottom.addCell(cell_bottom_seven_empty);

        Cell cell_bottom_seven = new Cell();
        cell_bottom_seven.add(new Cell().add(new Paragraph("Any damage to the Equipment will be charged accordingly.")));
        cell_bottom_seven.setBorder(Border.NO_BORDER);
        table_bottom.addCell(cell_bottom_seven);

        Cell cell_bottom_eight_empty = new Cell();
        cell_bottom_eight_empty.add(new Cell().add(new Paragraph("8")));
        cell_bottom_eight_empty.setBorder(Border.NO_BORDER);
        table_bottom.addCell(cell_bottom_eight_empty);

        Cell cell_bottom_eight = new Cell();
        cell_bottom_eight.add(new Cell().add(new Paragraph("You agree to provide us the right of way for the purpose of ingress and egress to and from the location\n" +
                "of the Equipment in order that deliveries of fuel may be made smoothly and without any charge, and\n" +
                "agree to hold us harmless from any and all damages which may accrue to the right of way, including\n" +
                "driveways and sidewalks, as a result of the delivery of fuel.")));
        cell_bottom_eight.setBorder(Border.NO_BORDER);
        table_bottom.addCell(cell_bottom_eight);

        Cell cell_bottom_nine_empty = new Cell();
        cell_bottom_nine_empty.add(new Cell().add(new Paragraph("9")));
        cell_bottom_nine_empty.setBorder(Border.NO_BORDER);
        table_bottom.addCell(cell_bottom_nine_empty);

        Cell cell_bottom_nine = new Cell();
        cell_bottom_nine.add(new Cell().add(new Paragraph("By accepting the Equipment Loan Agreement, you accept all risk of loss, injury or damages caused by the\n" +
                "Equipment and shall indemnify us from all suits and other liabilities arising from the same. You\n" +
                "acknowledge that you have loaned the Equipment &quot;as is&quot; without any warranties, express or implied,\n" +
                "including warranties of merchantability or fitness for a particular purpose. You accept the equipment 'as\n" +
                "is'. You shall indemnify and hold us, including our officers, employees and other representatives\n" +
                "harmless from, and defend us against all claims, actions, proceedings, injuries, deaths, expenses,\n" +
                "damages and liabilities, including legal fees, arising in connection with the Equipment or this Agreement,\n" +
                "including without limitation, the manufacture, selection, purchase, delivery, possession, use, operation,\n" +
                "maintenance, loan and return of the Equipment and your acts in failing to maintain the Equipment in\n" +
                "good repair. We shall not be liable to you for any loss, damage or expense of any kind or nature\n" +
                "whatsoever and howsoever, directly or indirectly, caused (including without limitation, any loss of\n" +
                "business) by (a) any item of equipment, (b) the use, maintenance, repair, service, or adjustment thereof,\n" +
                "(c) any delay or failure to provide any maintenance, repair, service, or adjustment thereto or (d) any\n" +
                "interruption of service of loss of use thereof.")));
        cell_bottom_nine.setBorder(Border.NO_BORDER);
        table_bottom.addCell(cell_bottom_nine);



        Cell cell_bottom_ten_empty = new Cell();
        cell_bottom_ten_empty.add(new Cell().add(new Paragraph("10")));
        cell_bottom_ten_empty.setBorder(Border.NO_BORDER);
        table_bottom.addCell(cell_bottom_ten_empty);

        Cell cell_bottom_ten = new Cell();
        cell_bottom_ten.add(new Cell().add(new Paragraph("After project completion/cessation of our service(s), this indemnity will continue to apply.")));
        cell_bottom_ten.setBorder(Border.NO_BORDER);
        table_bottom.addCell(cell_bottom_ten);

        document.add(table_bottom);
    }

    public Table createAcceptSign() {
        float[] pointColumnWidths = {200F, 80F, 150F};
        Table table = new Table(pointColumnWidths);

        Cell cell_empty = new Cell();
        cell_empty.add(new Cell().add(new Paragraph("")));
        cell_empty.setBorder(Border.NO_BORDER);
        table.addCell(cell_empty);

        Cell cell_Signature = new Cell();
        cell_Signature.add(new Cell().add(new Paragraph("Signature")));
        cell_Signature.setBorder(Border.NO_BORDER);
        table.addCell(cell_Signature);

        Cell cell_line = new Cell();
        cell_line.add(new Cell().add(new Paragraph(":  _________________________")));
        cell_line.setBorder(Border.NO_BORDER);
        table.addCell(cell_line);

        Cell cell_empty_1 = new Cell();
        cell_empty_1.add(new Cell().add(new Paragraph("")));
        cell_empty_1.setBorder(Border.NO_BORDER);
        table.addCell(cell_empty_1);

        Cell cell_name = new Cell();
        cell_name.add(new Cell().add(new Paragraph("Name")));
        cell_name.setBorder(Border.NO_BORDER);
        table.addCell(cell_name);

        Cell cell_line_1 = new Cell();
        cell_line_1.add(new Cell().add(new Paragraph(":  _________________________")));
        cell_line_1.setBorder(Border.NO_BORDER);
        table.addCell(cell_line_1);

        Cell cell_empty_2 = new Cell();
        cell_empty_2.add(new Cell().add(new Paragraph("")));
        cell_empty_2.setBorder(Border.NO_BORDER);
        table.addCell(cell_empty_2);

        Cell cell_designation = new Cell();
        cell_designation.add(new Cell().add(new Paragraph("Designation")));
        cell_designation.setBorder(Border.NO_BORDER);
        table.addCell(cell_designation);

        Cell cell_line_2 = new Cell();
        cell_line_2.add(new Cell().add(new Paragraph(":  _________________________")));
        cell_line_2.setBorder(Border.NO_BORDER);
        table.addCell(cell_line_2);

        cell_empty.add(new Cell().add(new Paragraph("Please affix Company’s stamp")));
        cell_empty.setBorder(Border.NO_BORDER);
        table.addCell(cell_empty);

        Cell cell_date = new Cell();
        cell_date.add(new Cell().add(new Paragraph("Date")));
        cell_date.setBorder(Border.NO_BORDER);
        table.addCell(cell_date);

        Cell cell_line_3 = new Cell();
        cell_line_3.add(new Cell().add(new Paragraph(":  _________________________")));
        cell_line_3.setBorder(Border.NO_BORDER);
        table.addCell(cell_line_3);

        return table;
    }

    public Table createEquipmentOnLoanTable(LoanRequest1 loanRequest) throws IOException, Exception {
        float[] pointColumnWidths = {10F, 250F, 150F, 50F};
        Table table = new Table(pointColumnWidths);

        Cell cell_sn = new Cell();
        cell_sn.add(new Cell().add(new Paragraph("S/N")));
        table.addCell(cell_sn);

        Cell cell_Description = new Cell();
        cell_Description.add(new Cell().add(new Paragraph("Description")));
        table.addCell(cell_Description);

        Cell cell_Serial = new Cell();
        cell_Serial.add(new Cell().add(new Paragraph("Serial No.")));
        table.addCell(cell_Serial);

        Cell cell_Qty = new Cell();
        cell_Qty.add(new Cell().add(new Paragraph("Qty")));
        table.addCell(cell_Qty);

        Integer  i = 1;

       /* for (LoanRequestDetail loanRequestDetail : loanRequest.getLoanRequestDetails()) {
            if (!loanRequestDetail.getEquipmentType().equals("Tank")) continue;
            Cell cell_sno = new Cell();
            cell_sno.add(new Cell().add(new Paragraph(i.toString())));
            table.addCell(cell_sno);

            Cell cell_description = new Cell();
            cell_description.add(new Cell().add(new Paragraph(loanRequestDetail.getEquipmentName().split(" ")[0] +" DIESEL STORAGE TANK")));
            table.addCell(cell_description);

            Cell cell_serial = new Cell();
            cell_serial.add(new Cell().add(new Paragraph(loanRequestDetail.getSerialNumber() != null ? loanRequestDetail.getSerialNumber(): "")));
            table.addCell(cell_serial);

            Cell cell_qty = new Cell();
            cell_qty.add(new Cell().add(new Paragraph("1 UNIT")));
            table.addCell(cell_qty);

            i++;
        }

        for (LoanRequestDetail loanRequestDetail : loanRequest.getLoanRequestDetails()) {
            if (!loanRequestDetail.getEquipmentType().equals("Tray")) continue;
            Cell cell_sno = new Cell();
            cell_sno.add(new Cell().add(new Paragraph(i.toString())));
            table.addCell(cell_sno);

            Cell cell_description = new Cell();
            cell_description.add(new Cell().add(new Paragraph(loanRequestDetail.getEquipmentName().split(" ")[0] +" TRAY")));
            table.addCell(cell_description);

            Cell cell_serial = new Cell();
            cell_serial.add(new Cell().add(new Paragraph(loanRequestDetail.getSerialNumber() != null ? loanRequestDetail.getSerialNumber() : "")));
            table.addCell(cell_serial);

            Cell cell_qty = new Cell();
            cell_qty.add(new Cell().add(new Paragraph("1 UNIT")));
            table.addCell(cell_qty);

            i++;
        }

        for (LoanRequestDetail loanRequestDetail : loanRequest.getLoanRequestDetails()) {
            if (!loanRequestDetail.getEquipmentType().equals("Accessories")) continue;
            Cell cell_sno = new Cell();
            cell_sno.add(new Cell().add(new Paragraph(i.toString())));
            table.addCell(cell_sno);

            Cell cell_description = new Cell();
            cell_description.add(new Cell().add(new Paragraph(loanRequestDetail.getEquipmentName())));
            table.addCell(cell_description);

            Cell cell_serial = new Cell();
            cell_serial.add(new Cell().add(new Paragraph("-")));
            table.addCell(cell_serial);

            Cell cell_qty = new Cell();
            cell_qty.add(new Cell().add(new Paragraph("1 PC")));
            table.addCell(cell_qty);

            i++;
        }*/

        return table;
    }

    public Table createPanImage() throws IOException, Exception {
        float[] pointColumnWidths = {370F, 150F};
        Table table = new Table(pointColumnWidths);

        ClassLoader classLoader = getClass().getClassLoader();
        //File file = new File(classLoader.getResource("img/pan.jpg").getFile());
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("img/pan.jpg");
        //new File()resourceAsStream.

        // File file = ResourceUtils.getFile("classpath:img/pan.jpg");
        byte[] bytes = IOUtils.toByteArray(resourceAsStream);
        ImageData data = ImageDataFactory.create((bytes));
        Image image = new Image(data);


        Cell cell_empty = new Cell();
        cell_empty.add(new Cell().add(new Paragraph("")));
        cell_empty.setBorder(Border.NO_BORDER);
        table.addCell(cell_empty);

        Cell cell_image = new Cell();
        cell_image.add(new Cell().add(image.setAutoScale(true)));
        cell_image.setBorder(Border.NO_BORDER);
        table.addCell(cell_image);

        return table;
    }
    public Table createAddress(LoanRequest1 loanRequest) throws IOException, Exception {
        String[][] DATA = {
                {loanRequest.getRequisitionNo(), "Inter Terminal Services"},
                {loanRequest.getCustomerName(), "12 Kaki Bukit Crescent,"},
                {loanRequest.getCustomerContactName(), "Kaki Bukit Techpark 1,"},
                {loanRequest.getContactNo(), "Singapore 416243."},
                {loanRequest.getDeliveryLocation(), "Tel: 65817667"},
                {"", "Fax: 65817247"},
        };

        float[] pointColumnWidths = {150F, 300F, 150F};
        Table table = new Table(pointColumnWidths);
        //float height = 20F;

        PdfFont bold = PdfFontFactory.createFont(FontConstants.TIMES_BOLD);

        Cell cell00 = new Cell();
        cell00.add(new Cell().add(new Paragraph("Ref. No")));
        cell00.setBorder(Border.NO_BORDER);
        //cell00.setHeight(height);
        cell00.setPadding(0f);
        table.addCell(cell00).setBorder(Border.NO_BORDER);

        Cell cell01 = new Cell();
        cell01.add(new Cell().add(new Paragraph(": " + DATA[0][0])));
        cell01.setBorder(Border.NO_BORDER);
        cell01.setFont(bold);
        //cell00.setHeight(height);
        cell01.setPadding(0f);
        table.addCell(cell01).setBorder(Border.NO_BORDER);

        Cell cell02 = new Cell();
        cell02.add(new Cell().add(new Paragraph(DATA[0][1])));
        cell02.setBorder(Border.NO_BORDER);
        //cell00.setHeight(height);
        cell02.setPadding(0f);
        table.addCell(cell02).setBorder(Border.NO_BORDER);

        Cell cell_Company = new Cell();
        cell_Company.add(new Cell().add(new Paragraph("Company")));
        cell_Company.setBorder(Border.NO_BORDER);
        cell_Company.setPadding(0);
        table.addCell(cell_Company).setBorder(Border.NO_BORDER);

        Cell cell10 = new Cell();
        cell10.add(new Cell().add(new Paragraph(": " + DATA[1][0])));
        cell10.setBorder(Border.NO_BORDER);
        cell10.setFont(bold);
        cell10.setPadding(0f);
        table.addCell(cell10).setBorder(Border.NO_BORDER);

        Cell cell11 = new Cell();
        cell11.add(new Cell().add(new Paragraph(DATA[1][1])));
        cell11.setBorder(Border.NO_BORDER);
        cell11.setPadding(0f);
        table.addCell(cell11).setBorder(Border.NO_BORDER);

        Cell cell_Contact = new Cell();
        cell_Contact.add(new Cell().add(new Paragraph("Contact Person")));
        cell_Contact.setBorder(Border.NO_BORDER);
        cell_Contact.setPadding(0f);
        table.addCell(cell_Contact).setBorder(Border.NO_BORDER);

        Cell cell20 = new Cell();
        cell20.add(new Cell().add(new Paragraph(": " + DATA[2][0])));
        cell20.setFont(bold);
        cell20.setPadding(0f);
        cell20.setBorder(Border.NO_BORDER);
        table.addCell(cell20).setBorder(Border.NO_BORDER);

        Cell cell21 = new Cell();
        cell21.add(new Cell().add(new Paragraph(DATA[2][1])));
        cell21.setBorder(Border.NO_BORDER);
        cell21.setPadding(0f);
        table.addCell(cell21).setBorder(Border.NO_BORDER);

        Cell cell_Number = new Cell();
        cell_Number.add(new Cell().add(new Paragraph("Contact Number")));
        cell_Number.setBorder(Border.NO_BORDER);
        cell_Number.setPadding(0f);
        table.addCell(cell_Number).setBorder(Border.NO_BORDER);

        Cell cell30 = new Cell();
        cell30.add(new Cell().add(new Paragraph(": " + DATA[3][0])));
        cell30.setBorder(Border.NO_BORDER);
        cell30.setPadding(0f);
        cell30.setFont(bold);
        table.addCell(cell30).setBorder(Border.NO_BORDER);

        Cell cell31 = new Cell();
        cell31.add(new Cell().add(new Paragraph(DATA[3][1])));
        cell31.setBorder(Border.NO_BORDER);
        cell31.setPadding(0f);
        table.addCell(cell31).setBorder(Border.NO_BORDER);

        Cell cell_Location = new Cell();
        cell_Location.add(new Cell().add(new Paragraph("Location")));
        cell_Location.setBorder(Border.NO_BORDER);
        cell_Location.setPadding(0f);
        table.addCell(cell_Location).setBorder(Border.NO_BORDER);

        Cell cell40 = new Cell();
        cell40.add(new Cell().add(new Paragraph(": " + DATA[4][0])));
        cell40.setFont(bold);
        cell40.setPadding(0f);
        cell40.setBorder(Border.NO_BORDER);
        table.addCell(cell40).setBorder(Border.NO_BORDER);

        Cell cell41 = new Cell();
        cell41.add(new Cell().add(new Paragraph(DATA[4][1])));
        cell41.setBorder(Border.NO_BORDER);
        cell41.setPadding(0f);
        table.addCell(cell41).setBorder(Border.NO_BORDER);

        Cell cell_fax = new Cell();
        cell_fax.add(new Cell().add(new Paragraph("Date of this Agreement")));
        cell_fax.setBorder(Border.NO_BORDER);
        cell_fax.setPadding(0f);
        table.addCell(cell_fax).setBorder(Border.NO_BORDER);

        Cell cell50 = new Cell();
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        String formattedDate = formatter.format(date);
        cell50.add(new Cell().add(new Paragraph(": " +formattedDate)));
        cell50.setBorder(Border.NO_BORDER);
        cell50.setFont(bold);
        cell50.setPadding(0f);
        table.addCell(cell50).setBorder(Border.NO_BORDER);

        Cell cell51 = new Cell();
        cell51.add(new Cell().add(new Paragraph(DATA[5][1])));
        cell51.setBorder(Border.NO_BORDER);
        cell51.setPadding(0f);
        table.addCell(cell51).setBorder(Border.NO_BORDER);

        return table;
    }
}


@Getter
@Setter
 class LoanRequest1   {

    private String requisitionNo;


    private String requestorName;

    private String employeeId;

    //private CustomerEntity customerID;

    private String customerName;

    private String contactNo;

    private String customerEmail;

//    private CustomerSiteEntity locationID;

    private String deliveryLocation;


    private LocalDateTime projectEndDate;

    private byte[] loanReport;

    private byte[] returnReport;

    private String tankSerialNumber;

    private String traySerialNumber;

    private Integer loanStatus;

    private LocalDate submittedDate;

    private LocalDate deliveryDate;

    private String customerContactName;

    private String returnComment;

    private Date returnDate;
}

