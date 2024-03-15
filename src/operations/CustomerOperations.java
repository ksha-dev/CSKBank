package operations;

import java.util.List;
import java.util.Map;

import api.UserAPI;
import api.mysql.MySQLUserAPI;
import cache.CachePool;
import exceptions.AppException;
import exceptions.messages.ActivityExceptionMessages;
import modules.Account;
import modules.Branch;
import modules.CustomerRecord;
import modules.Transaction;
import modules.UserRecord;
import utility.ConstantsUtil;
import utility.ConstantsUtil.ModifiableField;
import utility.ConstantsUtil.TransactionHistoryLimit;
import utility.ConstantsUtil.UserType;
import utility.ValidatorUtil;

public class CustomerOperations {

	private UserAPI api = new MySQLUserAPI();

	public CustomerRecord getCustomerRecord(int customerId) throws AppException {
		UserRecord user = CachePool.getUserRecordCache().get(customerId);
		if (user.getType() != UserType.CUSTOMER) {
			throw new AppException(ActivityExceptionMessages.NO_CUSTOMER_RECORD_FOUND);
		}
		return (CustomerRecord) user;
	}

	public Account getAccountDetails(long accountNumber, int userId) throws AppException {
		Account account = CachePool.getAccountCache().get(accountNumber);
		if (account.getUserId() != userId) {
			throw new AppException("Access denied!");
		}
		return account;
	}
	
	public Branch getBranchDetailsOfAccount(int branchId) throws AppException {
		ValidatorUtil.validateId(branchId);
		return CachePool.getBranchCache().get(branchId);
	}

	public Map<Long, Account> getAssociatedAccounts(int customerId) throws AppException {
		return api.getAccountsOfUser(customerId);
	}

	public List<Transaction> getTransactionsOfAccount(long accountNumber, int pageNumber, TransactionHistoryLimit limit)
			throws AppException {
		ValidatorUtil.validateId(accountNumber);
		ValidatorUtil.validateId(pageNumber);
		ValidatorUtil.validateObject(limit);
		return api.getTransactionsOfAccount(accountNumber, pageNumber, limit);
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

	
	public boolean updateUserDetails(int userId, ModifiableField field, Object value, String pin) throws AppException {
		ValidatorUtil.validateId(userId);
		ValidatorUtil.validateObject(field);
		if (!ConstantsUtil.USER_MODIFIABLE_FIELDS.contains(field)) {
			throw new AppException(ActivityExceptionMessages.MODIFICATION_ACCESS_DENIED);
		}
		ValidatorUtil.validateObject(value);

		ValidatorUtil.validatePIN(pin);
		if (api.userConfimration(userId, pin)) {
			return api.updateProfileDetails(userId, field, value);
		} else {
			throw new AppException(ActivityExceptionMessages.USER_AUTHORIZATION_FAILED);
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
