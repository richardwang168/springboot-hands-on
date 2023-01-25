package crs.fcl.eim.sftp.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import com.jcraft.jsch.SftpException;


public interface FileTransferService {
	
	boolean uploadFile(String localFilePath, String remoteFilePath);
	
	boolean downloadFile(String localFilePath, String remoteFilePath);

}
