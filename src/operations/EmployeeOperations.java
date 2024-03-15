package operations;

import java.util.List;
import java.util.Map;

import api.EmployeeAPI;
import api.mysql.MySQLEmployeeAPI;
import cache.CachePool;
import exceptions.AppException;
import exceptions.messages.ActivityExceptionMessages;
import modules.Account;
import modules.Branch;
import modules.CustomerRecord;
import modules.EmployeeRecord;
import modules.Transaction;
import modules.UserRecord;
import utility.ValidatorUtil;
import utility.ConstantsUtil;
import utility.ConstantsUtil.AccountType;
import utility.ConstantsUtil.ModifiableField;
import utility.ConstantsUtil.TransactionHistoryLimit;
import utility.ConstantsUtil.UserType;

public class EmployeeOperations {
	private EmployeeAPI api = new MySQLEmployeeAPI();

	public EmployeeRecord getEmployeeRecord(int employeeId) throws AppException {
		UserRecord user = CachePool.getUserRecordCache().get(employeeId);
		if (!(user.getType() == UserType.EMPLOYEE || user.getType() == UserType.ADMIN)) {
			throw new AppException(ActivityExceptionMessages.INVALID_EMPLOYEE_RECORD);
		}
		return (EmployeeRecord) user;
	}

	public Account getAccountDetails(long accountNumber) throws AppException {
		return CachePool.getAccountCache().get(accountNumber);
	}

	public CustomerRecord getCustomerRecord(int customerId) throws AppException {
		ValidatorUtil.validateId(customerId);
		UserRecord user = CachePool.getUserRecordCache().get(customerId);
		if (user.getType() != UserType.CUSTOMER) {
			throw new AppException(ActivityExceptionMessages.NO_CUSTOMER_RECORD_FOUND);
		}
		return (CustomerRecord) user;
	}

	public Branch getBrachDetails(int branchId) throws AppException {
		return CachePool.getBranchCache().get(branchId);
	}

	public Map<Long, Account> getListOfAccountsInBranch(int employeeId, int pageNumber) throws AppException {
		ValidatorUtil.validateId(pageNumber);
		return api.viewAccountsInBranch(employeeId, pageNumber);
	}

	public Account createNewCustomerAndAccount(CustomerRecord customer, AccountType accountType, double depositAmount,
			int employeeId) throws AppException {
		customer.setType(UserType.CUSTOMER.getUserTypeId());
		ValidatorUtil.validateObject(customer);

		CustomerRecord createdCustomer = getCustomerRecord(api.createCustomer(customer));
		return createAccountForExistingCustomer(createdCustomer.getUserId(), accountType, depositAmount, employeeId);
	}

	public Account createAccountForExistingCustomer(int customerId, AccountType accountType, double depositAmount,
			int employeeId) throws AppException {
		ValidatorUtil.validateId(customerId);
		ValidatorUtil.validateId(employeeId);
		ValidatorUtil.validateObject(accountType);
		ValidatorUtil.validatePositiveNumber((long) depositAmount);

		if (accountType == AccountType.SAVINGS && depositAmount < ConstantsUtil.MINIMUM_DEPOSIT_AMOUNT) {
			throw new AppException(ActivityExceptionMessages.MINIMUM_DEPOSIT_REQUIRED);
		}
		long accountNumber = api.createAccount(customerId, accountType, getEmployeeRecord(employeeId).getBranchId());
		depositAmount(employeeId, accountNumber, depositAmount);
		return CachePool.getAccountCache().get(accountNumber);
	}

	public List<Transaction> getListOfTransactions(long accountNumber, int pageNumber, TransactionHistoryLimit limit)
			throws AppException {
		ValidatorUtil.validatePositiveNumber(accountNumber);
		ValidatorUtil.validatePositiveNumber(pageNumber);
		ValidatorUtil.validateObject(limit);
		return api.getTransactionsOfAccount(accountNumber, pageNumber, limit);
	}

	public long depositAmount(int employeeId, long accountNumber, double amount) throws AppException {
		ValidatorUtil.validateId(accountNumber);
		ValidatorUtil.validatePositiveNumber((long) amount);
		return api.depositAmount(accountNumber, amount, getEmployeeRecord(employeeId));
	}

	public long withdrawAmount(int employeeId, long accountNumber, double amount) throws AppException {
		ValidatorUtil.validateId(accountNumber);
		ValidatorUtil.validatePositiveNumber((long) amount);
		return api.withdrawAmount(accountNumber, amount, getEmployeeRecord(employeeId));
	}

	public boolean updateCustomerDetails(int customerId, ModifiableField field, Object value) throws AppException {
		ValidatorUtil.validatePositiveNumber(customerId);
		ValidatorUtil.validateObject(field);
		if (!ConstantsUtil.EMPLOYEE_MODIFIABLE_FIELDS.contains(field)) {
			throw new AppException(ActivityExceptionMessages.MODIFICATION_ACCESS_DENIED);
		}
		ValidatorUtil.validateObject(value);
		getCustomerRecord(customerId);
		return api.updateProfileDetails(customerId, field, value);
	}

	public boolean updatePassword(int customerId, String oldPassword, String newPassword) throws AppException {
		ValidatorUtil.validateId(customerId);
		ValidatorUtil.validatePassword(oldPassword);
		ValidatorUtil.validatePassword(newPassword);

		return api.updatePassword(customerId, oldPassword, newPassword);
	}
}
