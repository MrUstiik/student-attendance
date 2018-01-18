package root.utils;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.*;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import root.models.Student;
import root.models.Subject;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class PdfUtils {
    private static String fontFile = "./ttf/AdonisC.ttf";
    private static String encoding = "Cp1251";

    //todo bold font
    public static void saveReport(File file, List<Student> students, Subject subject) throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(file));
        Document    doc    = new Document(pdfDoc);
        PdfFont     font   = PdfFontFactory.createFont(fontFile, encoding, true);
        doc.setFont(font);
        doc.add(new Paragraph("Час створення: " + DTF.formatDateTime(LocalDateTime.now())));
        doc.add(new Paragraph("Група: " + subject.getGroup()));
        doc.add(new Paragraph("Назва предмету: " + subject.getTitle()));
        doc.add(new Paragraph("Викладач: " + subject.getLecturer()));
        {
            String practicalLecturer = subject.getPracticalLecturer();
            if(!practicalLecturer.equals("")){
                doc.add(new Paragraph("Викладач практики: " + practicalLecturer));
            }
        }

        if (students.size() != 0) {
            //columns widths
            float num            = 1;
            float name           = 5;
            float attendance     = 3;
            float absent         = 1;
            float late           = 1;
            float attend         = 1;
            float total          = 2;
            float ratio          = 4;
            float lecturesRatio  = 2;
            float practicalRatio = 2;

            Table table = new Table(new float[]{num, name, absent, late, attend, total, lecturesRatio, practicalRatio});

            table.addCell(new Cell(2, 1).add("№").setFont(font));
            table.addCell(new Cell(2, 1).add("Студент"));
            table.addCell(new Cell(1, 3).add("Відвідуваність"));
            table.addCell(new Cell(2, 1).add("Всього"));
            table.addCell(new Cell(1, 2).add("Відношення"));
            table.addCell("Відсутній");
            table.addCell("Запізнився");
            table.addCell("Присутній");
            table.addCell("Лекції");
            table.addCell("Практики");

            int counter = 0;
            for (Student student : students) {
                table.addCell(++counter + "");

                table.addCell(student.getName());

                table.addCell(String.valueOf(student.getAbsentCount()));
                table.addCell(String.valueOf(student.getLateCount()));
                table.addCell(String.valueOf(student.getAttendCount()));

                int    classesCount = student.getClassesCount();
                int    onLectures   = student.getAttendAndLateCountLecture();
                int    onPracticals = student.getAttendAndLateCountPractical();
                double percentageLect;
                double percentagePract;
                table.addCell(Integer.toString(classesCount));
                if (classesCount != 0) {
                    percentageLect = percentage(onLectures, classesCount);
                    percentagePract = percentage(onPracticals, classesCount);
                } else {
                    percentageLect = percentagePract = 0;
                }
                table.addCell(percentageLect + "%");
                table.addCell(percentagePract + "%");
            }
            doc.add(table);
        }
        doc.close();
    }

    private static double percentage(int on, int all) {
        return on * 100.0 / all;
    }

}
