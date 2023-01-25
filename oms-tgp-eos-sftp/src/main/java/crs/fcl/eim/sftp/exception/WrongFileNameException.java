package crs.fcl.eim.sftp.exception;

public class WrongFileNameException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;	
	public WrongFileNameException(String errorMessage) {
        super(errorMessage);
    }

	
}
