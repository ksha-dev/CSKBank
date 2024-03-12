package operations;

import api.UserAPI;
import api.mysql.MySQLUserAPI;
import exceptions.AppException;
import modules.UserRecord;

public class AppOperations {

	private UserAPI api = new MySQLUserAPI();

	public UserRecord getUser(int userID, String password) throws AppException {
		if (new MySQLUserAPI().userAuthentication(userID, password)) {
			return api.getUserDetails(userID);
		}
		return null;
	}
}
