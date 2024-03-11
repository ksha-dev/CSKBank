package helpers;

import exceptions.AppException;
import utility.ConstantsUtil.EmployeeType;
import utility.ValidatorUtil;

public class EmployeeRecord extends UserRecord {
	private EmployeeType role;
	private int branchId;

	public EmployeeRecord() {
	}

	public void setRole(String role) throws AppException {
		ValidatorUtil.validateObject(role);
		this.role = EmployeeType.valueOf(role);
	}

	public void setBranchId(int branchId) {
		this.branchId = branchId;
	}

	public EmployeeType getRole() {
		return role;
	}

	public int getBranchId() {
		return branchId;
	}
}
