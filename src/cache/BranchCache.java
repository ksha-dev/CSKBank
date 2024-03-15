package cache;

import api.UserAPI;
import exceptions.AppException;
import modules.Account;
import modules.Branch;

public class BranchCache extends LRUCache<Integer, Branch> {

	private UserAPI userAPIObject;

	public BranchCache(UserAPI userAPIObject, int capacity) {
		super(capacity);
		this.userAPIObject = userAPIObject;
	}

	@Override
	protected Branch fetchData(Integer key) throws AppException {
		return userAPIObject.getBrachDetails(key);
	}
}