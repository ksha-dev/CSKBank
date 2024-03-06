package operations;

import api.UserAPI;
import api.mysql.MySQLUserAPI;
import exceptions.AppException;
import helpers.UserRecord;

public class HomeOperations {

	private UserAPI api = new MySQLUserAPI();

	public UserRecord authenticateUser(int userID, String password) throws AppException {
		if (new MySQLUserAPI().userAuthentication(userID, password)) {
			return api.getUserDetails(userID);
		}
		return null;
	}
}
