package modules;

public class EmployeeRecord extends UserRecord {
	private int branchId;

	public EmployeeRecord() {
	}

	public void setBranchId(int branchId) {
		this.branchId = branchId;
	}

	public int getBranchId() {
		return branchId;
	}
}
