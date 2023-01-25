package crs.fcl.eim.sftp.controller;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import crs.fcl.eim.sftp.service.ExcelPOIHSSFHelper;
import crs.fcl.eim.sftp.service.ExcelPOIXSSFHelper;

@Controller
public class FileDownloadController {
	private Logger logger = LoggerFactory.getLogger(FileDownloadController.class);
	@Autowired
	private ExcelPOIHSSFHelper excelPOIHSSFHelper;

	@Autowired
	private ExcelPOIXSSFHelper excelPOIXSSFHelper;

    @Value("${upload.path}")
    private String uploadPath;

	/*
	 * Download Files
	 */
	@GetMapping("/downloadme")
	public ResponseEntity<InputStreamResource> downloadFile() {
		String generatedExcelFileName = "";
		Path path = Paths.get(uploadPath);
		String psudoFileName = path.getFileName().toString();		
		generatedExcelFileName = excelPOIHSSFHelper.getFileLocationOut().getFileName().toString();
		
		if (!generatedExcelFileName.equals(psudoFileName)) {
			logger.info("Found generated Excel[.xls] file: " + generatedExcelFileName);
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Disposition", "attachment; filename=" + generatedExcelFileName);
	
			try {
				return ResponseEntity.ok().headers(headers)
						.body(new InputStreamResource(excelPOIHSSFHelper.loadFile()));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return null;
		} else {
			generatedExcelFileName = excelPOIXSSFHelper.getFileLocationOut().getFileName().toString();
			if (!generatedExcelFileName.equals(psudoFileName)) {
				logger.info("Found generated Excel[.xlsx] file: " + generatedExcelFileName);
				HttpHeaders headers = new HttpHeaders();
				headers.add("Content-Disposition", "attachment; filename=" + generatedExcelFileName);
		
				try {
					return ResponseEntity.ok().headers(headers)
							.body(new InputStreamResource(excelPOIXSSFHelper.loadFile()));
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
				
			} else {
				logger.error("Neither excel file in old excel format] nor in new excel file could be found, how can?");
				return null;
				
			}
		}
	}
}
