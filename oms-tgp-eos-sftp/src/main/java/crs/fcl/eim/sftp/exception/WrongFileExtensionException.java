package crs.fcl.eim.sftp.exception;

public class WrongFileExtensionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;	
	public WrongFileExtensionException(String errorMessage) {
        super(errorMessage);
    }

	
}
