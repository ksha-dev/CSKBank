package api;

import java.util.Map;

import exceptions.AppException;
import helpers.Account;
import helpers.CustomerRecord;
import helpers.EmployeeRecord;
import utility.ConstantsUtil.AccountType;
import utility.ConstantsUtil.Status;

public interface EmployeeAPI extends UserAPI {

	public boolean createCustomer(CustomerRecord customer) throws AppException;

	public long createAccount(int customerID, AccountType type, int branchID) throws AppException;

	public boolean changeAccountStatus(long accountNumber, Status status, int branchId, String pin) throws AppException;

	public Map<Long, Account> viewAccountsInBranch(int branchID, int pageNumber) throws AppException;

	public long depositAmount(long accoutNumber, double amount, EmployeeRecord employee) throws AppException;

	public long withdrawAmount(long accountNumber, double amount, EmployeeRecord employee) throws AppException;
		
}
