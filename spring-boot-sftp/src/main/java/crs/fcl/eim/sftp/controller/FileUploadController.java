package crs.fcl.eim.sftp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import crs.fcl.eim.sftp.service.FilesStorageService;

@Controller
@RequestMapping("/")
public class FileUploadController {

	@Autowired
	FilesStorageService storageService;

	@GetMapping
	public String main() {
		return "index";
	}

	@PostMapping()
	public String upload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			storageService.save(file);
			redirectAttributes.addFlashAttribute("message",
					"Uploaded the file successfully: " + file.getOriginalFilename() + "!");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			redirectAttributes.addFlashAttribute("message",
					"Could not upload the file: " + file.getOriginalFilename() + "!");
		}

		// redirectAttributes.addFlashAttribute("message", "You successfully uploaded "
		// + file.getOriginalFilename() + "!");

		return "redirect:/";
	}
}