package exceptions.messages;

public enum ActivityExceptionMessages {

	INVALID_EMPLOYEE_RECORD("The employee record obtained is not valid."),
	SERVER_CONNECTION_LOST("The connection to server is lost. Logging out."),
	MINIMUM_DEPOSIT_REQUIRED("The deposit amount must meet the minimum required amount. The minimum deposit amount is Rs. ");

	private String message;

	private ActivityExceptionMessages(String message) {
		this.message = message;
	}

	public String toString() {
		return message;
	}

}
