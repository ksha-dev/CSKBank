package exceptions;

public enum APIExceptionMessage {
	USER_NOT_FOUND("The user record for the given information does not exist."),
	USER_AUNTHENTICATION_FAILED("The credentials entered for the acount is incorrect. Please try again"),
	NO_SERVER_CONNECTION("Unable to connect to server. Please try again after sometime. Sorry for the inconvenience"),

	NO_RECORDS_FOUND("No matching records were found."),
	CANNOT_FETCH_DETAILS("Unable to fetch details at the moment. Please try again later"),

	USER_CREATION_FAILED("Unable to create user record at the moment. Please try again later"),
	ACCOUNT_CREATION_FAILED("Failed to create a new Account. Please try again later."),
	CUSTOMER_CREATION_FAILED("Unable to create customer record at the moment. Pleasy try again later"),
	
	UNKNOWN_ERROR("An unexpected error occured. Please try again after sometime");

	private String message;

	private APIExceptionMessage(String message) {
		this.message = message;
	}

	public String toString() {
		return this.message;
	}
}
