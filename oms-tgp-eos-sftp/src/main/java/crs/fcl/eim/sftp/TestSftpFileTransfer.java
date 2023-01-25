package crs.fcl.eim.sftp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import crs.fcl.eim.sftp.service.FileTransferService;

@Component
public class TestSftpFileTransfer implements CommandLineRunner {

	@Autowired
	private FileTransferService fileTransferService;

	private Logger logger = LoggerFactory.getLogger(TestSftpFileTransfer.class);

	@Override
	public void run(String... args) throws Exception {
		//logger.info("Start download file");
		//boolean isDownloaded = fileTransferService.downloadFile("C:\\temp\\memo.txt", "/home/rwang/memo.txt");
		//logger.info("Download result: " + String.valueOf(isDownloaded));

		//logger.info("Start upload file");
		//boolean isUploaded = fileTransferService.uploadFile("C:\\temp\\helloworld.txt", "/home/rwang/helloworld.txt");
		//logger.info("Upload result: " + String.valueOf(isUploaded));
	}

}
