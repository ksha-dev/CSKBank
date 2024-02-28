package operations;

import java.util.List;

import api.EmployeeAPI;
import api.mysql.MySQLEmployeeAPI;
import exceptions.APIExceptionMessage;
import exceptions.ActivityExceptionMessages;
import exceptions.AppException;
import helpers.Account;
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

	public List<Account> getListOfAccountsInBranch() throws AppException {
		return api.viewAccountsInBranch(getEmployeeRecord().getBranchID());
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
		ValidatorUtil.validateObject(accountType);
		ValidatorUtil.validateObject(customer);
		if (depositAmount < MINIMUM_DEPOSIT_AMOUNT) {
			throw new AppException(
					ActivityExceptionMessages.MINIMUM_DEPOSIT_REQUIRED.toString() + MINIMUM_DEPOSIT_AMOUNT);
		}
		api.createCustomer(customer);
		return createAccountForExistingCustomer(customer.getUserID(), accountType, depositAmount);
	}

	public Account createAccountForExistingCustomer(int customerID, String accountType, double depositAmount)
			throws AppException {
		long accountNumber = api.createAccount(customerID, accountType, employee.getBranchID(), depositAmount);
		return api.getAccountDetails(accountNumber);
	}

	public List<Transaction> getListOfTransactions(long accountNumber) throws AppException {
		ValidatorUtil.validatePostiveNumber(accountNumber);
		return api.getTransactionsOfAccount(accountNumber);
	}
}
