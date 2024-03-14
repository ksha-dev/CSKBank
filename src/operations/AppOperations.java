package operations;

import api.UserAPI;
import api.mysql.MySQLUserAPI;
import cache.CachePool;
import exceptions.AppException;
import modules.UserRecord;

public class AppOperations {

	private UserAPI api = new MySQLUserAPI();

	public AppOperations() throws AppException {
		CachePool.initializeCache(api);
	}

	public UserRecord getUser(int userID, String password) throws AppException {
		if (api.userAuthentication(userID, password)) {
			return CachePool.getUserRecordCache().get(userID);
		}
		return null;
	}
}
