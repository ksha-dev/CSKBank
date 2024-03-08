package helpers;

import java.util.logging.Logger;

import consoleRunner.utility.LoggingUtil;
import exceptions.AppException;
import utility.ValidatorUtil;

public class Branch {

	private int branchId;
	private String address;
	private long phone;
	private String email;
	private String ifscCode;

	public Branch() {
	}

	// setters

	public void setBrachId(int branchId) throws AppException {
		ValidatorUtil.validatePositiveNumber(branchId);
		this.branchId = branchId;
	}

	public void setAddress(String address) throws AppException {
		ValidatorUtil.validateObject(address);
		this.address = address;
	}

	public void setPhone(long phone) throws AppException {
		ValidatorUtil.validateMobileNumber(phone);
		this.phone = phone;
	}

	public void setEmail(String email) throws AppException {
		ValidatorUtil.validateEmail(email);
		this.email = email;
	}

	public void setIfscCode(String ifscCode) throws AppException {
		ValidatorUtil.validateObject(ifscCode);
		this.ifscCode = ifscCode;
	}

	// getters

	public int getBranchId() {
		return this.branchId;
	}

	public long getPhone() {
		return this.phone;
	}

	public String getAddress() {
		return this.address;
	}

	public String getEmail() {
		return this.email;
	}

	public String getIfscCode() {
		return this.ifscCode;
	}

	public void logBranch() {
		Logger log = LoggingUtil.DEFAULT_LOGGER;
		log.info("-".repeat(40));
		log.info(String.format("%-40s", "BRANCH DETAILS"));
		log.info("-".repeat(40));
		log.info(String.format("%-20s", "ADDRESS") + " : " + getAddress());
		log.info(String.format("%-20s", "PHONE") + " : " + getPhone());
		log.info(String.format("%-20s", "EMAIL") + " : " + getEmail());
		log.info(String.format("%-20s", "IFSC CODE") + " : " + getIfscCode());
	}
}
