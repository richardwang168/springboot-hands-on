package crs.fcl.eim.sftp.controller;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import crs.fcl.eim.sftp.exception.WrongFileExtensionException;
import crs.fcl.eim.sftp.exception.WrongFileNameException;
import crs.fcl.eim.sftp.service.ExcelPOIHSSFHelper;
import crs.fcl.eim.sftp.service.ExcelPOIXSSFHelper;
import crs.fcl.eim.sftp.service.FilesStorageService;

@Controller
@RequestMapping()
public class FileValidationController {
	private Logger logger = LoggerFactory.getLogger(FileValidationController.class);
    @Value("${upload.path}")
    private String uploadPath;

	@Autowired
	FilesStorageService storageService;
	
	@Autowired
	private ExcelPOIHSSFHelper excelPOIHSSFHelper;

	@Autowired
	private ExcelPOIXSSFHelper excelPOIXSSFHelper;

	@PostMapping(value="/validate")
	public String validate(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
		logger.info("Start validating process......");
		Path newExcelFileFullPath = null;
		try {
			String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
			if (extension.equals("xlsx") || extension.equals("xls")) {
				storageService.save(file);
				/*
				 * pickup only requested columns
				 */
	            Path root = Paths.get(uploadPath);
				Path uploadedFileFullPath = root.resolve(file.getOriginalFilename());
				logger.info("uploadedFileFullPath=" + uploadedFileFullPath.toString());
				if (extension.equals("xlsx")) {
		            String newExcelFileNameWOExtension = removeFileExtension(StringUtils.getFilename(file.getOriginalFilename()), true);
		            String newExcelFileName = newExcelFileNameWOExtension + "_new" + ".xlsx";
		            newExcelFileFullPath = Paths.get(uploadPath + File.separator + newExcelFileName);
					
					excelPOIXSSFHelper.setFileLocationIn(uploadedFileFullPath);
					excelPOIXSSFHelper.setFileLocationOut(newExcelFileFullPath);
					excelPOIHSSFHelper.setFileLocationIn(root);
					excelPOIHSSFHelper.setFileLocationOut(root);
					Map<Integer, List<String>> data = excelPOIXSSFHelper.parseExcel();
					if (StringUtils.getFilename(file.getOriginalFilename()).endsWith("_V1.xlsx")) {
						excelPOIXSSFHelper.writeExcelWithV1(data);
					} else {
						if (StringUtils.getFilename(file.getOriginalFilename()).endsWith("_V2.xlsx")) {
							excelPOIXSSFHelper.writeExcelWithV2(data);
						} else {
							throw new WrongFileNameException("The order Excel file name must one of the following:"
									+ " 'XXXXXXXX_V1.xls' or 'XXXXXXXX_V2.xls' or 'XXXXXXXX_V1.xlsx' or 'XXXXXXXX_V2.xlsx'");
						}
					}
				}
				if (extension.equals("xls")) {
		            String newExcelFileNameWOExtension = removeFileExtension(StringUtils.getFilename(file.getOriginalFilename()), true);
		            String newExcelFileName = newExcelFileNameWOExtension + "_new" + ".xls";
		            newExcelFileFullPath = Paths.get(uploadPath + File.separator + newExcelFileName);
		            
					excelPOIHSSFHelper.setFileLocationIn(uploadedFileFullPath);
					excelPOIHSSFHelper.setFileLocationOut(newExcelFileFullPath);
					excelPOIXSSFHelper.setFileLocationIn(root);
					excelPOIXSSFHelper.setFileLocationOut(root);
					Map<Integer, List<String>> data = excelPOIHSSFHelper.parseExcel();
					if (StringUtils.getFilename(file.getOriginalFilename()).endsWith("_V1.xls")) {
						excelPOIHSSFHelper.writeExcelWithV1(data);
					} else {
						if (StringUtils.getFilename(file.getOriginalFilename()).endsWith("_V2.xls")) {
							excelPOIHSSFHelper.writeExcelWithV2(data);
						} else {
							throw new WrongFileNameException("The order Excel file name must one of the following:"
									+ " 'XXXXXXXX_V1.xls' or 'XXXXXXXX_V2.xls' or 'XXXXXXXX_V1.xlsx' or 'XXXXXXXX_V2.xlsx'");
						}
					}
				}
				redirectAttributes.addFlashAttribute("message",
					"Validated the file successfully: " + file.getOriginalFilename() + "!");
			} else {
				throw new WrongFileExtensionException("Only Excel file with extension '.xlsx' is allowed to be uploaded, but "
						+ "file.getOriginalFilename() + \" is not an Excel file!\"");
			}
		} catch (WrongFileExtensionException e) {
			System.out.println(e.getMessage());
			redirectAttributes.addFlashAttribute("message",	e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
			redirectAttributes.addFlashAttribute("message",
					"Failed to validate the file: " + file.getOriginalFilename() + "!");
		}

		// redirectAttributes.addFlashAttribute("message", "You successfully uploaded "
		// + file.getOriginalFilename() + "!");
		logger.info("End validating process......");
		return "redirect:/download";
	}

    public String removeFileExtension(String filename, boolean removeAllExtensions) {
        if (filename == null || filename.isEmpty()) {
            return filename;
        }

        String extPattern = "(?<!^)[.]" + (removeAllExtensions ? ".*" : "[^.]*$");
        return filename.replaceAll(extPattern, "");
    }
}