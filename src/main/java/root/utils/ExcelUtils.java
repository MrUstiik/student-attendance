package root.utils;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ExcelUtils {
    public static List<String> readGroup(File file) throws IOException, InvalidFormatException {
        OPCPackage   fs    = OPCPackage.open(file);
        XSSFWorkbook wb    = new XSSFWorkbook(fs);
        XSSFSheet    sheet = wb.getSheetAt(0);

        int rows = sheet.getPhysicalNumberOfRows();
//        int cols = 0;
//        int tmp = 0;
//
//        // This trick ensures that we get the data properly even if it doesn't start from first few rows
//        for (int i = 0; i < 10 || i < rows; i++) {
//            row = sheet.getRow(i);
//            if (row != null) {
//                tmp = sheet.getRow(i).getPhysicalNumberOfCells();
//                if (tmp > cols)
//                    cols = tmp;
//            }
//        }

        List<String> students = new LinkedList<>();
        for (int r = 0; r < rows; r++) {
            XSSFRow row = sheet.getRow(r);
            if (row != null) {
                    XSSFCell cell = row.getCell(0);
                    if (cell != null) {
                        students.add(cell.getStringCellValue());
                    }
            }
        }

        return students;
    }
}
