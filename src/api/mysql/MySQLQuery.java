package api.mysql;

enum MySQLQuery {
	USE_DATABASE("USE CSKBank;"), USER_DETAILS_PS("SELECT * FROM users WHERE user_id = ?;"),
	CREDENTIAL_CHECK_PS("SELECT * FROM credentials WHERE user_id = ?;"),
	CUSTOMER_DETAILS_PS("SELECT * FROM customers WHERE user_id = ?;"),
	EMPLOYEE_DETAILS_PS("SELECT * FROM employees WHERE user_id = ?;"),
	USER_ACCOUNT_DETAILS_PS("SELECT * FROM accounts WHERE user_id = ?"),
	ACCOUNT_TRANSACTION_DETAILS_PS("SELECT * FROM transactions WHERE viewer_account_number = ? LIMIT 10"),
	ACCOUNT_DETAILS_PS("SELECT * FROM accounts WHERE account_number = ?"),
	ACCOUNTS_IN_BRANCH_PS("SELECT * FROM accounts WHERE branch_id = ?"),
	CREATE_USER_PS(
			"INSERT INTO users(first_name, last_name, date_of_birth, gender, address, mobile, email) VALUES(?,?,?,?,?,?,?);"),
	CREATE_CREDENTIAL_PS("INSERT INTO credentials VALUE(?,?);"),
	CREATE_EMPLOYEE_PS("INSERT INTO employees VALUES(?,?,?);"),
	CREATE_CUSTOMER_PS("INSERT INTO customers VALUES(?,?,?);"),
	CREATE_ACCOUNT_PS("INSERT INTO accounts(user_id, type, branch_id, opening_date, balance) VALUES(?,?,?,?,?);"),

	CREATE_TRANSACTION_PS("INSERT INTO transactions VALUES(?,?,?,?,?,?,?,?,?);"),
	CREATE_NEW_TRANSACTION_PS(
			"INSERT INTO transactions(user_id, viewer_account_number, transacted_account_number, transacted_amount, transaction_type, closing_balance, time_stamp, remarks) VALUE(?,?,?,?,?,?,?,?)");

	private String query;

	private MySQLQuery(String query) {
		this.query = query;
	}

	public String getQuery() {
		return this.query;
	}
}
