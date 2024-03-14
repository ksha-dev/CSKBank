package cache;

import api.UserAPI;
import exceptions.AppException;
import modules.Account;

public class AccountCache extends LRUCache<Long, Account> {

	private UserAPI userAPIObject;

	public AccountCache(UserAPI userAPIObject, int capacity) {
		super(capacity);
		this.userAPIObject = userAPIObject;
	}

	@Override
	protected Account fetchData(Long key) throws AppException {
		return userAPIObject.getAccountDetails(key);
	}
}