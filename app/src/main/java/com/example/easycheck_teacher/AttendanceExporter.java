package com.example.easycheck_teacher;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class AttendanceExporter {
    public static void exportToExcel(List<Attendance> attendanceList, String filePath) throws IOException {
        // Create a new workbook
        Workbook workbook = new XSSFWorkbook();

        // Create a sheet in the workbook
        Sheet sheet = workbook.createSheet("Attendance");

        // Create a header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Roll No", "Name", "Status", "Attendance Code"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Write attendance data to the sheet
        for (int i = 0; i < attendanceList.size(); i++) {
            Attendance attendance = attendanceList.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(attendance.getRoll());
            row.createCell(1).setCellValue(attendance.getName());
            row.createCell(2).setCellValue(attendance.getStatus());
            row.createCell(3).setCellValue(attendance.getCode());

        }

        // Write the workbook content to a file
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }

        // Close the workbook to release resources
        workbook.close();
    }
}
