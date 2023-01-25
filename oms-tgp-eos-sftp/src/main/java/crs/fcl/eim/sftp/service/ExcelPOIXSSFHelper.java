package crs.fcl.eim.sftp.service;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ExcelPOIXSSFHelper {
	private Logger logger = LoggerFactory.getLogger(ExcelPOIXSSFHelper.class);

	private Path fileLocationIn;
	private Path fileLocationOut;
	private String firstSheetName = "Sheet1";
	
	public Path getFileLocationIn() {
		return fileLocationIn;
	}

	public Path getFileLocationOut() {
		return fileLocationOut;
	}

	public void setFileLocationOut(Path fileLocationOut) {
		this.fileLocationOut = fileLocationOut;
	}

	public void setFileLocationIn(Path fileLocationIn) {
		this.fileLocationIn = fileLocationIn;
	}

	public Map<Integer, List<String>> parseExcel() throws IOException {
		logger.info("Start parsing the uploaded Excel file......");
		Map<Integer, List<String>> data = new HashMap<Integer, List<String>>();
		FileInputStream file = new FileInputStream(fileLocationIn.toFile());
		Workbook workbook = new XSSFWorkbook(file);
		Sheet sheet = workbook.getSheetAt(0);
		firstSheetName = workbook.getSheetName(0);
		int i = 0;
		for (Row row : sheet) {
			if (isRowEmpty(row)) {
				continue;
			}

			data.put(i, new ArrayList<String>());
			for (Cell cell : row) {
				switch (cell.getCellType()) {
				case STRING:
					data.get(i).add(cell.getRichStringCellValue().getString());
					break;
				case NUMERIC:
					if (DateUtil.isCellDateFormatted(cell)) {
						data.get(i).add(cell.getDateCellValue() + "");
					} else {
						data.get(i).add((int) cell.getNumericCellValue() + "");
					}
					break;
				case BOOLEAN:
					data.get(i).add(cell.getBooleanCellValue() + "");
					break;
				case FORMULA:
					data.get(i).add(cell.getCellFormula() + "");
					break;
				default:
					data.get(i).add(" ");
				}
			}
			i++;
		}
		if (workbook != null) {
			workbook.close();
		}
		logger.info("End parsing the uploaded Excel file");
		return data;
	}

	public void writeExcelWithV2(Map<Integer, List<String>> data) throws IOException {
		Workbook workbook = new XSSFWorkbook();
		logger.info("Start creating order[V2] Excel file......");
		try {

			Sheet sheet = workbook.createSheet(firstSheetName);
			/*
			 * Get Columns' titles and number of columns from source xlsx data
			 */
			List<String> titleRow = data.get(0);
			/*
			 * Set sheet's column default width
			 */
			for (int i = 0; i < titleRow.size(); i++) {
				if (i == 2) {
					sheet.setColumnWidth(i, 8000); // about 23 characters
					
				} else {
					sheet.setColumnWidth(i, 4000); // about 23 characters
				}
			}
			Row header = sheet.createRow(0);

			CellStyle headerStyle = workbook.createCellStyle();

			headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
			headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			XSSFFont font = ((XSSFWorkbook) workbook).createFont();
			font.setFontName("Arial");
			font.setFontHeightInPoints((short) 16);
			font.setBold(true);
			headerStyle.setFont(font);

			/*
			 * Set sheet's column title
			 */
			/**
			for (int i = 0; i < 2; i++) {
				Cell headerCell = header.createCell(i);
				headerCell.setCellValue(titleRow.get(i));
				headerCell.setCellStyle(headerStyle);
			}
			**/
			Cell headerCell0 = header.createCell(0);
			headerCell0.setCellValue(titleRow.get(0));
			headerCell0.setCellStyle(headerStyle);
			
			Cell headerCell1 = header.createCell(1);
			headerCell1.setCellValue(titleRow.get(14));
			headerCell1.setCellStyle(headerStyle);
			
			Cell headerCell2 = header.createCell(2);
			headerCell2.setCellValue(titleRow.get(20));
			headerCell2.setCellStyle(headerStyle);
			
			Cell headerCell3 = header.createCell(3);
			headerCell3.setCellValue(titleRow.get(25));
			headerCell3.setCellStyle(headerStyle);
			
			CellStyle style = workbook.createCellStyle();
			style.setShrinkToFit(true);
			//style.setWrapText(true);

			for (int r = 1; r < data.size(); r++) {
				
				Row row = sheet.createRow(r);
				List<String> rowData = data.get(r);
				Double refno = new Double(rowData.get(0));
				String vprodId = rowData.get(14);
				String descript = rowData.get(20);
				Double ordered = new Double(rowData.get(25));

				Cell cell0 = row.createCell(0);
				cell0.setCellValue(refno);
				cell0.setCellStyle(style);

				Cell cell1 = row.createCell(1);
				cell1.setCellValue(vprodId);
				cell1.setCellStyle(style);

				Cell cell2 = row.createCell(2);
				cell2.setCellValue(descript);
				cell2.setCellStyle(style);

				Cell cell3 = row.createCell(3);
				cell3.setCellValue(ordered);
				cell3.setCellStyle(style);
			}

			//File currDir = new File(".");
			//String path = currDir.getAbsolutePath();
			// String fileLocation = path.substring(0, path.length() - 1) + "temp.xlsx";

			FileOutputStream outputStream = new FileOutputStream(fileLocationOut.toString());
			workbook.write(outputStream);
			logger.info("End creating order[V2] Excel file: " + fileLocationOut.toString());
		} finally {
			if (workbook != null) {
				workbook.close();
			}
		}
	}

	public void writeExcelWithV1(Map<Integer, List<String>> data) throws IOException {
		Workbook workbook = new XSSFWorkbook();
		logger.info("Start creating order[V1] Excel file......");

		try {

			Sheet sheet = workbook.createSheet(firstSheetName);
			/*
			 * Get Columns' titles and number of columns from source xlsx data
			 */
			List<String> titleRow = data.get(0);
			/*
			 * Set sheet's column default width
			 */
			for (int i = 0; i < titleRow.size(); i++) {
				sheet.setColumnWidth(i, 4000); // about 23 characters
			}

			Row header = sheet.createRow(0);

			CellStyle headerStyle = workbook.createCellStyle();

			headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
			headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			XSSFFont font = ((XSSFWorkbook) workbook).createFont();
			font.setFontName("Arial");
			font.setFontHeightInPoints((short) 16);
			font.setBold(true);
			headerStyle.setFont(font);

			/*
			 * Set sheet's column title
			 */
			//for (int i = 0; i < titleRow.size(); i++) {
			for (int i = 0; i < 2; i++) {
				Cell headerCell = header.createCell(i);
				headerCell.setCellValue(titleRow.get(i));
				headerCell.setCellStyle(headerStyle);
			}

			CellStyle style = workbook.createCellStyle();
			style.setWrapText(true);


			for (int r = 1; r < data.size(); r++) {
				Row row = sheet.createRow(r);
				List<String> rowData = data.get(r);
				//for (int c = 0; c < rowData.size(); c++) {
				// column-0 -> column-1
				// column-1 -> column-0
				String item = rowData.get(1);
				Double ordered = new Double(rowData.get(0));
				
				Cell cell0 = row.createCell(0);
				cell0.setCellValue(item);
				cell0.setCellStyle(style);	
				
				Cell cell1 = row.createCell(1);
				cell1.setCellValue(ordered);
				cell1.setCellStyle(style);
			}

			//File currDir = new File(".");
			//String path = currDir.getAbsolutePath();
			// String fileLocation = path.substring(0, path.length() - 1) + "temp.xlsx";

			FileOutputStream outputStream = new FileOutputStream(fileLocationOut.toString());
			workbook.write(outputStream);
			logger.info("End creating order[V1] Excel file: " + fileLocationOut.toString());
		} finally {
			if (workbook != null) {
				workbook.close();
			}
		}
	}

	private static boolean isRowEmpty(Row row) {
		boolean isEmpty = true;
		DataFormatter dataFormatter = new DataFormatter();

		if (row != null) {
			for (Cell cell : row) {
				if (dataFormatter.formatCellValue(cell).trim().length() > 0) {
					isEmpty = false;
					break;
				}
			}
		}

		return isEmpty;
	}

	// Load Data to Excel File
    public ByteArrayInputStream loadFile() throws IOException {
        return new ByteArrayInputStream(FileUtils.readFileToByteArray(fileLocationOut.toFile()));
    }
}