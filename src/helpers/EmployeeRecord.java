package helpers;

import exceptions.AppException;
import utility.LoggingUtil;
import utility.ValidatorUtil;

public class EmployeeRecord extends UserRecord {
	private int role;
	private int branchID;

	public EmployeeRecord() {
	}

	public void setRole(int role) throws AppException {
		ValidatorUtil.validatePositiveNumber(role);
		this.role = role;
	}

	public void setBranchID(int branchID) {
		this.branchID = branchID;
	}

	public int getRole() {
		return role;
	}

	public int getBranchID() {
		return branchID;
	}

	@Override
	public void logUserRecord() {
		super.logUserRecord();
		LoggingUtil.DEFAULT_LOGGER.info(String.format("%-20s", "BRANCH ID") + " : " + branchID);

	}
}
