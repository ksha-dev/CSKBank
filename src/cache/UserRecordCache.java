package cache;

import api.UserAPI;
import exceptions.AppException;
import modules.UserRecord;

public class UserRecordCache extends LRUCache<Integer, UserRecord> {

	private UserAPI userAPIObject;

	public UserRecordCache(UserAPI userAPIObject, int capacity) {
		super(capacity);
		this.userAPIObject = userAPIObject;
	}

	@Override
	protected UserRecord fetchData(Integer key) throws AppException {
		return userAPIObject.getUserDetails(key);
	}
}