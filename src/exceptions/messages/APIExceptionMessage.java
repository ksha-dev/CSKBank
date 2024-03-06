package exceptions.messages;

public enum APIExceptionMessage {
	USER_NOT_FOUND("The user record for the given information does not exist."),
	CUSTOMER_RECORD_NOT_FOUND("The valid customer details for the user does not exist."),
	EMPLOYEE_RECORD_NOT_FOUND("The valid employee details for the user does not exist."),
	ACCOUNT_RECORD_NOT_FOUND("No account exists with the given account number."),
	USER_AUNTHENTICATION_FAILED("The credentials entered for the acount is incorrect. Please try again"),
	NO_SERVER_CONNECTION("Unable to connect to server. Please try again after sometime. Sorry for the inconvenience"),

	NO_RECORDS_FOUND("No matching records were found."),
	CANNOT_FETCH_DETAILS("Unable to fetch details at the moment. Please try again later"),

	USER_CREATION_FAILED("Unable to create user record at the moment. Please try again later"),
	EMPLOYEE_CREATION_FAILED("Unable to create employee record at the moment. Please try again later"),

	ACCOUNT_CREATION_FAILED("Failed to create a new Account. Please try again later."),
	CUSTOMER_CREATION_FAILED("Unable to create customer record at the moment. Pleasy try again later"),

	UNKNOWN_ERROR("An unexpected error occured. Please try again after sometime"),
	
	UPDATE_FAILED("Cannot update user details"),
	
	BALANCE_ACQUISITION_FAILED("The balance amount for the given account could not be obtained."),
	INSUFFICIENT_BALANCE("The account selected does not contain sufficient balance for the transaction"),
	TRANSACTION_FAILED("The transaction has failed. Any changes done will be reverted in few minutes."),
	USER_CONFIRMATION_FAILED("Confirmation Failed. Cannot process the request"),
	BRANCH_DETAILS_NOT_FOUND("Cannot find a linked branch details");

	private String message;

	private APIExceptionMessage(String message) {
		this.message = message;
	}

	public String toString() {
		return this.message;
	}
}
