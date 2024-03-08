package api;

import java.util.Map;

import exceptions.AppException;
import helpers.Account;
import helpers.Branch;
import helpers.EmployeeRecord;
import utility.ConstantsUtil.ModifiableField;

public interface AdminAPI extends EmployeeAPI {

	public Map<Integer, EmployeeRecord> getEmployeesInBranch(int branchID, int pageNumber) throws AppException;

	public boolean createEmployee(EmployeeRecord employee) throws AppException;

	public boolean updateEmployeeDetails(int employeeId, ModifiableField field, Object value) throws AppException;

	public boolean createBranch(Branch branch) throws AppException;

	public boolean updateBranchDetails(int branchId, ModifiableField field, Object value) throws AppException;

	public Map<Long, Account> viewAccountsInBank(int branchID, int pageNumber) throws AppException;

}
