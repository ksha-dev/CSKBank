package operations;

import java.util.Map;

import api.AdminAPI;
import api.mysql.MySQLAdminAPI;
import cache.CachePool;
import exceptions.AppException;
import exceptions.messages.ActivityExceptionMessages;
import modules.Account;
import modules.Branch;
import modules.EmployeeRecord;
import utility.ConstantsUtil.ModifiableField;
import utility.ConstantsUtil.UserType;
import utility.ConstantsUtil;
import utility.ValidatorUtil;

public class AdminOperations {

	private AdminAPI api = new MySQLAdminAPI();

	public Map<Integer, EmployeeRecord> getEmployeesInBrach(int branchID, int pageNumber) throws AppException {
		ValidatorUtil.validateId(branchID);
		ValidatorUtil.validateId(pageNumber);

		return api.getEmployeesInBranch(branchID, pageNumber);
	}

	public boolean createEmployee(EmployeeRecord employee) throws AppException {
		ValidatorUtil.validateObject(employee);
		employee.setType(UserType.EMPLOYEE.getUserTypeId());
		return api.createEmployee(employee);
	}

	public boolean update(int employeeId, ModifiableField field, Object value) throws AppException {
		ValidatorUtil.validateId(employeeId);
		ValidatorUtil.validateObject(value);
		ValidatorUtil.validateObject(field);

		return api.updateEmployeeDetails(employeeId, field, value);
	}

	public Branch createBranch(Branch branch) throws AppException {
		ValidatorUtil.validateObject(branch);
		int branchId = api.createBranch(branch);
		return CachePool.getBranchCache().get(branchId);
	}

	public boolean updateBranchDetails(int branchId, ModifiableField field, Object value) throws AppException {
		ValidatorUtil.validateId(branchId);
		ValidatorUtil.validateObject(field);
		if (!ConstantsUtil.ADMIN_MODIFIABLE_FIELDS.contains(field)) {
			throw new AppException(ActivityExceptionMessages.MODIFICATION_ACCESS_DENIED);
		}
		ValidatorUtil.validateObject(value);

		return api.updateBranchDetails(branchId, field, value);
	}

	public Map<Long, Account> viewAccountsInBank(int pageNumber) throws AppException {
		ValidatorUtil.validateId(pageNumber);

		return api.viewAccountsInBank(pageNumber);
	}
}
