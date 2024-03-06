package helpers;

import consoleRunner.utility.LoggingUtil;
import exceptions.AppException;
import utility.ValidatorUtil;

public class EmployeeRecord extends UserRecord {
	private int role;
	private int branchId;

	public EmployeeRecord() {
	}

	public void setRole(int role) throws AppException {
		ValidatorUtil.validatePositiveNumber(role);
		this.role = role;
	}

	public void setBranchId(int branchId) {
		this.branchId = branchId;
	}

	public int getRole() {
		return role;
	}

	public int getBranchId() {
		return branchId;
	}

	@Override
	public void logUserRecord() {
		super.logUserRecord();
		LoggingUtil.DEFAULT_LOGGER.info(String.format("%-20s", "BRANCH ID") + " : " + branchId);

	}
}
