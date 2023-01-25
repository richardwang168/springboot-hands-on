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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import crs.fcl.eim.sftp.exception.WrongFileExtensionException;
import crs.fcl.eim.sftp.service.ExcelPOIHSSFHelper;
import crs.fcl.eim.sftp.service.ExcelPOIXSSFHelper;
import crs.fcl.eim.sftp.service.FilesStorageService;
import crs.fcl.eim.sftp.service.impl.FilesStorageServiceImpl;

@Controller
@RequestMapping()
public class FileUploadController {
	private Logger logger = LoggerFactory.getLogger(FileUploadController.class);
    @Value("${upload.path}")
    private String uploadPath;

	@Autowired
	FilesStorageService storageService;
	
	@PostMapping(value="/upload")
	public String upload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
		logger.info("Start Uploading process......");
		try {
			String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
			if (extension.equals("xlsx") || extension.equals("xls")) {
				storageService.saveAndRelay(file);
				redirectAttributes.addFlashAttribute("message",
					"Uploaded the file successfully: " + file.getOriginalFilename() + "!");
			} else {
				throw new WrongFileExtensionException("Only Excel file with extension '.xlsx' or '.xls' are allowed to be uploaded, but "
						+ "file.getOriginalFilename() + \" is not an Excel file!\"");
			}
		} catch (WrongFileExtensionException e) {
			System.out.println(e.getMessage());
			redirectAttributes.addFlashAttribute("message",	e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
			redirectAttributes.addFlashAttribute("message",
					"Could not upload the file: " + file.getOriginalFilename() + "!");
		}


		// redirectAttributes.addFlashAttribute("message", "You successfully uploaded "
		// + file.getOriginalFilename() + "!");
		logger.info("End Uploading process......");

		return "redirect:/upload";
	}
}