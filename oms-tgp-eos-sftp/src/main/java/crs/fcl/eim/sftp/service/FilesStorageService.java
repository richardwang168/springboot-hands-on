package crs.fcl.eim.sftp.service;

import java.nio.file.Path;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FilesStorageService {
	public void init();

	public void save(MultipartFile file);

	public void saveAndRelay(MultipartFile file);

	public Resource load(String filename);

	public void deleteAll();

	public List<Path> loadAll();
}
