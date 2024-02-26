package operations;

import java.util.List;

import apis.API;
import apis.mysql.MySQLAPI;
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
	private API api = new MySQLAPI();

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
		if (user instanceof CustomerRecord) {
			return (CustomerRecord) user;
		} else {
			throw new AppException(APIExceptionMessage.NO_RECORDS_FOUND);
		}
	}

	public Account createNewCustomerAndAccount(CustomerRecord customer, String accountType, double depositAmount)
			throws AppException {
		int customerID = api.createUser(customer);
		int accountNumber = api.createAccount(customerID, accountType, employee.getBranchID(), depositAmount);
		return api.getAccountDetails(accountNumber);
	}

//	public boolean createNewCustomer(CustomerRecord customer) throws AppException {
//		if (api.createNewUserRecord(customer)) {
//			if (api.createNewCustomerRecord(customer)) {
//				return true;
//			}
//		}
//		return false;
//	}
}
