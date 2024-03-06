package operations;

import java.util.List;
import java.util.Map;

import api.UserAPI;
import api.mysql.MySQLUserAPI;
import exceptions.AppException;
import exceptions.messages.ActivityExceptionMessages;
import helpers.Account;
import helpers.Branch;
import helpers.CustomerRecord;
import helpers.Transaction;
import utility.ConstantsUtil;
import utility.ConstantsUtil.ModifiableField;
import utility.ConstantsUtil.TransactionHistoryLimit;
import utility.ValidatorUtil;

public class CustomerOperations {

	private CustomerRecord currentCustomer;
	private UserAPI api = new MySQLUserAPI();

	public CustomerOperations(CustomerRecord customer) throws AppException {
		ValidatorUtil.validateObject(customer);
		ValidatorUtil.validateId(customer.getUserId());
		this.currentCustomer = customer;
	}

	public CustomerRecord getCustomerRecord() throws AppException {
		return (CustomerRecord) api.getUserDetails(currentCustomer.getUserId());
	}

	public Map<Long, Account> getAssociatedAccounts() throws AppException {
		return api.getAccountsOfUser(currentCustomer.getUserId());
	}

	public List<Transaction> getTransactionsOfAccount(long accountNumber, int pageNumber, TransactionHistoryLimit limit)
			throws AppException {
		ValidatorUtil.validateId(accountNumber);
		ValidatorUtil.validateId(pageNumber);
		ValidatorUtil.validateObject(limit);
		return api.getTransactionsOfAccount(accountNumber, pageNumber, limit);
	}

	public double getAccountBalance(long accountNumber) throws AppException {
		ValidatorUtil.validateId(accountNumber);
		return api.getBalanceInAccount(accountNumber);
	}

	public long tranferMoney(Transaction helperTransaction, boolean isTransferOutsideBank, String pin)
			throws AppException {
		ValidatorUtil.validateObject(pin);
		ValidatorUtil.validateObject(helperTransaction);

		if (api.userConfimration(helperTransaction.getUserId(), pin)) {
			return api.transferAmount(helperTransaction, isTransferOutsideBank);
		} else {
			throw new AppException(ActivityExceptionMessages.USER_AUTHORIZATION_FAILED);
		}
	}

	public Branch getBranchDetailsOfAccount(int branchId) throws AppException {
		ValidatorUtil.validateId(branchId);
		return api.getBrachDetails(branchId);
	}

	public boolean updateUserDetails(int userId, ModifiableField field, Object value, String pin) throws AppException {
		ValidatorUtil.validateId(userId);
		ValidatorUtil.validatePIN(pin);
		ValidatorUtil.validateObject(value);
		ValidatorUtil.validateObject(field);

		if (ConstantsUtil.USER_MODIFIABLE_FIELDS.contains(field)) {
			if (api.userConfimration(userId, pin)) {
				return api.updateProfile(userId, field, value);
			} else {
				throw new AppException(ActivityExceptionMessages.USER_AUTHORIZATION_FAILED);
			}
		} else {
			throw new AppException("Access denied. Cannot update this field by the user.");
		}
	}

	public boolean updatePassword(int customerId, String oldPassword, String newPassword, String pin)
			throws AppException {
		ValidatorUtil.validateId(customerId);
		ValidatorUtil.validatePassword(oldPassword);
		ValidatorUtil.validatePassword(newPassword);
		ValidatorUtil.validatePIN(pin);

		if (api.userConfimration(customerId, pin)) {
			return api.updatePassword(customerId, oldPassword, newPassword);
		} else {
			throw new AppException(ActivityExceptionMessages.USER_AUTHORIZATION_FAILED);
		}
	}
}
