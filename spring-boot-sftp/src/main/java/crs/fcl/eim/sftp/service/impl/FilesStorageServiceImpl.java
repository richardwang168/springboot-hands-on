package crs.fcl.eim.sftp.service.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import com.jcraft.jsch.ChannelSftp;

import crs.fcl.eim.sftp.service.FileTransferService;
import crs.fcl.eim.sftp.service.FilesStorageService;

@Service
public class FilesStorageServiceImpl implements FilesStorageService {
	private Logger logger = LoggerFactory.getLogger(FilesStorageServiceImpl.class);
	
	//private final Path root = Paths.get("uploads");
	
	@Value("${sftp.destination.path}")
	private String destinationPath;

	@Autowired
	private FileTransferService fileTransferService;
	
	@Autowired
	ServletContext context;

    @Value("${upload.path}")
    private String uploadPath;

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadPath));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload folder!");
        }
    }

	//@Override
	//public void init() {
	//	try {
	//		Files.createDirectory(root);
	//	} catch (IOException e) {
	//		throw new RuntimeException("Could not initialize folder for upload!");
	//	}
	//}

	@Override
	public void save(MultipartFile file) {
        try {
            Path root = Paths.get(uploadPath);
            if (!Files.exists(root)) {
                init();
            }
			Files.copy(file.getInputStream(), root.resolve(file.getOriginalFilename()),StandardCopyOption.REPLACE_EXISTING);

			String srcFullFileName = root.resolve(file.getOriginalFilename()).toString();
			logger.info("Resource......" + srcFullFileName);
			String destFullFileName = destinationPath + '/' + file.getOriginalFilename();
			logger.info("Destination......" + destFullFileName);
						
			logger.info("Start upload file to remote server......");
			boolean isUploaded = fileTransferService.uploadFile(srcFullFileName, destFullFileName);
			logger.info("Upload result: " + String.valueOf(isUploaded));
			if (! isUploaded) {
				throw new RuntimeException("Failed to SFTP the file to remote server");
			}

		} catch (Exception e) {
			throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
		}
	}


	@Override
	public Resource load(String filename) {
		try {
			//Path file = root.resolve(filename);
            Path file = Paths.get(uploadPath)
                    .resolve(filename);
			Resource resource = new UrlResource(file.toUri());

			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new RuntimeException("Could not read the file!");
			}
		} catch (MalformedURLException e) {
			throw new RuntimeException("Error: " + e.getMessage());
		}
	}

	@Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(Paths.get(uploadPath)
                                               .toFile());
    }

	@Override
    public List<Path> loadAll() {
        try {
            Path root = Paths.get(uploadPath);
            if (Files.exists(root)) {
                return Files.walk(root, 1)
                            .filter(path -> !path.equals(root))
                            .collect(Collectors.toList());
            }

            return Collections.emptyList();
        } catch (IOException e) {
            throw new RuntimeException("Could not list the files!");
        }
    }
}