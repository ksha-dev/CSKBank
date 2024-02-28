package operations;

import api.GeneralAPI;
import api.mysql.MySQLGeneralAPI;
import exceptions.AppException;
import helpers.UserRecord;

public class HomeOperations {

	private GeneralAPI api = new MySQLGeneralAPI();

	public UserRecord authenticateUser(int userID, String password) throws AppException {
		if (new MySQLGeneralAPI().authenticateUser(userID, password)) {
			return api.getUserDetails(userID);
		}
		return null;
	}
}
