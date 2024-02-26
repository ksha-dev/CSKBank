package apis.mysql;

public enum MySQLQuery {
	USE_DATABASE("USE CSKBank;"), USER_DETAILS_PS("SELECT * FROM users WHERE user_id = ?;"),
	CREDENTIAL_CHECK_PS("SELECT * FROM credentials WHERE user_id = ?;"),
	CUSTOMER_DETAILS_PS("SELECT * FROM customers WHERE user_id = ?;"),
	EMPLOYEE_DETAILS_PS("SELECT * FROM employees WHERE user_id = ?;"),
	USER_ACCOUNT_DETAILS_PS("SELECT * FROM accounts WHERE user_id = ?"),
	ACCOUNT_TRANSACTION_DETAILS_PS("SELECT * FROM transactions WHERE viewer_account_number = ? LIMIT 10"),
	ACCOUNT_DETAILS_PS("SELECT * FROM accounts WHERE account_number = ?"),
	ACCOUNTS_IN_BRANCH_PS("SELECT * FROM accounts WHERE branch_id = ?");

	private String query;

	private MySQLQuery(String query) {
		this.query = query;
	}

	public String getQuery() {
		return this.query;
	}
}
