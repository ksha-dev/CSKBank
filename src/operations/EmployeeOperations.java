package operations;

import java.util.List;
import java.util.Map;

import api.EmployeeAPI;
import api.mysql.MySQLEmployeeAPI;
import api.mysql.MySQLQueryUtil.Fields;
import exceptions.AppException;
import exceptions.messages.APIExceptionMessage;
import exceptions.messages.ActivityExceptionMessages;
import helpers.Account;
import helpers.Branch;
import helpers.CustomerRecord;
import helpers.EmployeeRecord;
import helpers.Transaction;
import helpers.UserRecord;
import utility.ValidatorUtil;
import utility.HelperUtil.UserTypes;

public class EmployeeOperations {
	private EmployeeRecord employee;
	private EmployeeAPI api = new MySQLEmployeeAPI();

	private final double MINIMUM_DEPOSIT_AMOUNT = 2000.0;

	public EmployeeOperations(EmployeeRecord employee) throws AppException {
		ValidatorUtil.validateObject(employee);
		if (employee.getUserID() < 0 && employee.getType().equals(UserTypes.EMPLOYEE)) {
			throw new AppException(ActivityExceptionMessages.INVALID_EMPLOYEE_RECORD);
		}
		this.employee = employee;
	}

	public EmployeeRecord getEmployeeRecord() throws AppException {
		return (EmployeeRecord) api.getUserDetails(employee.getUserID());
	}

	public Map<Long, Account> getListOfAccountsInBranch() throws AppException {
		return api.viewAccountsInBranch(employee.getBranchID());
	}

	public CustomerRecord getCustomerRecord(int customerID) throws AppException {
		UserRecord user = api.getUserDetails(customerID);
		if (!(user instanceof CustomerRecord)) {
			throw new AppException(APIExceptionMessage.NO_RECORDS_FOUND);
		}
		return (CustomerRecord) user;
	}

	public Account createNewCustomerAndAccount(CustomerRecord customer, String accountType, double depositAmount)
			throws AppException {
		ValidatorUtil.validateObject(customer);
		api.createCustomer(customer);
		return createAccountForExistingCustomer(customer.getUserID(), accountType, depositAmount);
	}

	public Account createAccountForExistingCustomer(int customerID, String accountType, double depositAmount)
			throws AppException {
		ValidatorUtil.validateObject(accountType);
		if (accountType.equals("SAVINGS") && depositAmount < MINIMUM_DEPOSIT_AMOUNT) {
			throw new AppException(
					ActivityExceptionMessages.MINIMUM_DEPOSIT_REQUIRED.toString() + MINIMUM_DEPOSIT_AMOUNT);
		}
		long accountNumber = api.createAccount(customerID, accountType, employee.getBranchID());
		depositAmount(accountNumber, depositAmount);
		return api.getAccountDetails(accountNumber);
	}

	public List<Transaction> getListOfTransactions(long accountNumber) throws AppException {
		ValidatorUtil.validatePositiveNumber(accountNumber);
		return api.getTransactionsOfAccount(accountNumber);
	}

	public Branch getBrachDetails(int branchID) throws AppException {
		return api.getBrachDetails(branchID);
	}

	public long depositAmount(long accountNumber, double amount) throws AppException {
		return api.depositAmount(accountNumber, amount);
	}

	public long withdrawAmount(long accountNumber, double amount) throws AppException {
		return api.withdrawAmount(accountNumber, amount);
	}

	public boolean updateCustomerDetails(int customerID, Fields field, Object value) throws AppException {
		ValidatorUtil.validatePositiveNumber(customerID);
		ValidatorUtil.validateObject(value);
		ValidatorUtil.validateObject(field);
		if (field instanceof Fields) {
			return api.updateProfile(customerID, field, value);
		} else {
			throw new AppException("Invalid field obtained for updating record");
		}
	}

	public boolean updatePassword(int customerID, String oldPassword, String newPassword) throws AppException {
		return api.updatePassword(customerID, oldPassword, newPassword);
	}
}
