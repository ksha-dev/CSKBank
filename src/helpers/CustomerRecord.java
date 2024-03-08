package helpers;

import java.util.logging.Logger;

import consoleRunner.utility.LoggingUtil;
import exceptions.AppException;
import utility.ValidatorUtil;

public class CustomerRecord extends UserRecord {
	private long aadhaarNumber;
	private String panNumber;

	public CustomerRecord() {
	}

	public void setAadhaarNumber(long aadhaarNumber) throws AppException {
		ValidatorUtil.validateAadhaarNumber(aadhaarNumber);
		this.aadhaarNumber = aadhaarNumber;
	}

	public void setPanNumber(String panNumber) throws AppException {
		ValidatorUtil.validatePANNumber(panNumber);
		this.panNumber = panNumber;
	}

	public long getAadhaarNumber() throws AppException {
		ValidatorUtil.validateAadhaarNumber(aadhaarNumber);
		return aadhaarNumber;
	}

	public String getPanNumber() throws AppException {
		ValidatorUtil.validatePANNumber(panNumber);
		return this.panNumber;
	}
	
}
