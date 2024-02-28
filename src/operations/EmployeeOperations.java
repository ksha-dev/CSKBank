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
import helpers.UserRecord;
import utility.ValidatorUtil;
import utility.SchemaUtil.UserTypes;

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
		EmployeeRecord fetechedRecord = (EmployeeRecord) api.getUserDetails(employee.getUserID());
		employee = fetechedRecord;
		return fetechedRecord;
	}

	public List<Account> getListOfAccountsInBranch() throws AppException {
		List<Account> listOfAccounts = api.viewAccountsInBranch(getEmployeeRecord().getBranchID());
		return listOfAccounts;
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
					"The deposit amount must meet the minimum required amount. The minimum deposit amount is Rs. "
							+ MINIMUM_DEPOSIT_AMOUNT);
		}
		int customerID = api.createUser(customer);
		customer.setUserID(customerID);
		api.createCustomer(customer);
		return createAccountForExistingCustomer(customerID, accountType, depositAmount);
	}

	public Account createAccountForExistingCustomer(int customerID, String accountType, double depositAmount)
			throws AppException {
		long accountNumber = api.createAccount(customerID, accountType, employee.getBranchID(), depositAmount);
		return api.getAccountDetails(accountNumber);
	}
}
