package exception;

import java.util.HashMap;

import util.FileIO;

public class AutoException extends Exception {
	private static final long serialVersionUID = 4464712018613675439L;
	
	private int errorNumber = 0;
	private String errorMessage = "";
	private HashMap<Integer, String> errorCodes;
	
	// Constructors
	
	public AutoException() {
		super();
	}
	
	public AutoException(int errorNumber, String errorMessage) {
		super(errorMessage);
		retrieveErrorCodesFromFile();
		this.setErrorNumber(errorNumber);
		this.setMessage(errorMessage);
	}
	
	public AutoException(String errorMessage) {
		this(-1, errorMessage);
	}
	
	public AutoException(int errorNumber) {
		this(errorNumber, "");
		
		if (errorCodes.containsKey(errorNumber)) {
			setMessage(errorCodes.get(errorNumber));
		}
	}
	
	// Getters and Setters
	
	public int getErrorNumber() {
		return errorNumber;
	}

	public void setErrorNumber(int errorNumber) {
		this.errorNumber = errorNumber;
	}
	
	@Override
	public String getMessage() {
		return errorMessage;
	}

	public void setMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	// Instance Methods
	
	public void printIssue() {
		System.out.println("AutoException [Error Number = " + errorNumber + ", Error Message = \"" + errorMessage + "\"]"); 
	}
	
	public void fixIssue() {
		switch (errorNumber) {
		case 1:
			FixFileName fileFixer = new FixFileName();
			fileFixer.fix();
			break;
		case 2:
			FixMissingPrice priceFixer = new FixMissingPrice();
			priceFixer.fix();
			break;
		}
	}
	
	// Utility Methods
	
	private void retrieveErrorCodesFromFile() {
		FileIO fileIO = new FileIO();
		errorCodes = fileIO.loadErrorCodesFromFile();
	}
}
