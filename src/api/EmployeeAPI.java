package api;

import java.util.Map;

import api.mysql.MySQLQueryUtil.Fields;
import exceptions.AppException;
import helpers.Account;
import helpers.CustomerRecord;

public interface EmployeeAPI extends UserAPI {

	public boolean createCustomer(CustomerRecord customer) throws AppException;

	public long createAccount(int customerID, String type, int branchID) throws AppException;

	public Map<Long, Account> viewAccountsInBranch(int branchID) throws AppException;

	public long depositAmount(long accoutNumber, double amount) throws AppException;

	public long withdrawAmount(long accountNumber, double amount) throws AppException;

	public boolean updateCustomerDetails(int customerID, Fields field, Object value) throws AppException;
}
