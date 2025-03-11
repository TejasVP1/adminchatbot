package com.ailoanchatbot.Ai.Powered.Loan.ChatBot.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

@Service
public class ExcelService {

    private static final String FILE_DIRECTORY = "C:/chatbot_excels/";

    public void generateExcel(List<Map<String, Object>> queryResults, String queryId) throws Exception {
        File directory = new File(FILE_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String filePath = FILE_DIRECTORY + queryId + ".xlsx";
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Query Results");

        if (queryResults.isEmpty()) {
            workbook.close();
            return;
        }

        // Create header row
        Row headerRow = sheet.createRow(0);
        int colNum = 0;
        for (String key : queryResults.get(0).keySet()) {
            Cell cell = headerRow.createCell(colNum++);
            cell.setCellValue(key);
            cell.setCellStyle(getHeaderCellStyle(workbook));
        }

        // Fill data rows
        int rowNum = 1;
        for (Map<String, Object> row : queryResults) {
            Row sheetRow = sheet.createRow(rowNum++);
            colNum = 0;
            for (String key : row.keySet()) {
                sheetRow.createCell(colNum++).setCellValue(row.get(key) != null ? row.get(key).toString() : "");
            }
        }

        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }

        workbook.close();
        System.out.println("Excel file generated: " + filePath);
    }

    private CellStyle getHeaderCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }
}
