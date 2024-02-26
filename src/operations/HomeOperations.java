package operations;

import apis.API;
import apis.mysql.MySQLAPI;
import exceptions.AppException;
import helpers.UserRecord;

public class HomeOperations {

	private API api = new MySQLAPI();

	public UserRecord authenticateUser(int userID, String password) throws AppException {
		if (new MySQLAPI().authenticateUser(userID, password)) {
			return api.getUserDetails(userID);
		}
		return null;
	}
}
