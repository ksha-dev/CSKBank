package modules;

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
}
