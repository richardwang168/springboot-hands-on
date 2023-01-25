package crs.fcl.eim.sftp.service;

public interface FileTransferService {
	
	boolean uploadFile(String localFilePath, String remoteFilePath);
	
	boolean downloadFile(String localFilePath, String remoteFilePath);

}
