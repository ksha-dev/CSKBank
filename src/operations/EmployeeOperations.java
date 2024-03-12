package operations;

import java.util.List;
import java.util.Map;

import api.EmployeeAPI;
import api.mysql.MySQLEmployeeAPI;
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
	private EmployeeRecord employee;
	private EmployeeAPI api = new MySQLEmployeeAPI();

	public EmployeeOperations(EmployeeRecord employee) throws AppException {
		ValidatorUtil.validateObject(employee);
		ValidatorUtil.validateId(employee.getUserId());
		if (!(employee.getType() == UserType.EMPLOYEE || employee.getType() == UserType.ADMIN)) {
			throw new AppException(ActivityExceptionMessages.INVALID_EMPLOYEE_RECORD);
		}
		this.employee = employee;
	}

	public EmployeeRecord getEmployeeRecord() throws AppException {
		return (EmployeeRecord) api.getUserDetails(employee.getUserId());
	}

	public Map<Long, Account> getListOfAccountsInBranch(int pageNumber) throws AppException {
		ValidatorUtil.validateId(pageNumber);
		return api.viewAccountsInBranch(employee.getBranchId(), pageNumber);
	}

	public CustomerRecord getCustomerRecord(int customerId) throws AppException {
		ValidatorUtil.validateId(customerId);
		UserRecord user = api.getUserDetails(customerId);
		if (!(user instanceof CustomerRecord)) {
			throw new AppException(ActivityExceptionMessages.NO_CUSTOMER_RECORD_FOUND);
		}
		return (CustomerRecord) user;
	}

	public Account createNewCustomerAndAccount(CustomerRecord customer, AccountType accountType, double depositAmount)
			throws AppException {
		customer.setType(UserType.CUSTOMER.getUserTypeId());
		ValidatorUtil.validateObject(customer);

		api.createCustomer(customer);
		return createAccountForExistingCustomer(customer.getUserId(), accountType, depositAmount);
	}

	public Account createAccountForExistingCustomer(int customerId, AccountType accountType, double depositAmount)
			throws AppException {
		ValidatorUtil.validateId(customerId);
		ValidatorUtil.validateObject(accountType);
		ValidatorUtil.validatePositiveNumber((long) depositAmount);

		if (accountType == AccountType.SAVINGS && depositAmount < ConstantsUtil.MINIMUM_DEPOSIT_AMOUNT) {
			throw new AppException(ActivityExceptionMessages.MINIMUM_DEPOSIT_REQUIRED);
		}
		long accountNumber = api.createAccount(customerId, accountType, employee.getBranchId());
		depositAmount(accountNumber, depositAmount);
		return api.getAccountDetails(accountNumber);
	}

	public List<Transaction> getListOfTransactions(long accountNumber, int pageNumber, TransactionHistoryLimit limit)
			throws AppException {
		ValidatorUtil.validatePositiveNumber(accountNumber);
		ValidatorUtil.validatePositiveNumber(pageNumber);
		ValidatorUtil.validateObject(limit);
		return api.getTransactionsOfAccount(accountNumber, pageNumber, limit);
	}

	public Branch getBrachDetails(int branchId) throws AppException {
		return api.getBrachDetails(branchId);
	}

	public long depositAmount(long accountNumber, double amount) throws AppException {
		ValidatorUtil.validateId(accountNumber);
		ValidatorUtil.validatePositiveNumber((long) amount);
		return api.depositAmount(accountNumber, amount, employee);
	}

	public long withdrawAmount(long accountNumber, double amount) throws AppException {
		ValidatorUtil.validateId(accountNumber);
		ValidatorUtil.validatePositiveNumber((long) amount);
		return api.withdrawAmount(accountNumber, amount, employee);
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
