package api.mysql;

enum MySQLQuery {
	USE_DATABASE("use CSKBank;"), USER_DETAILS_PS("select * from users where user_id = ?;"),
	CREDENTIAL_CHECK_PS("select * from credentials where user_id = ?;"),
	CUSTOMER_DETAILS_PS("select * from customers where user_id = ?;"),
	EMPLOYEE_DETAILS_PS("select * from employees where user_id = ?;"),
	USER_ACCOUNT_DETAILS_PS("select * from accounts where user_id = ?"),
	BRANCH_DETAILS_PS("select * from branch where branch_id = ?"),
	ACCOUNT_TRANSACTION_DETAILS_PS("select * from transactions where viewer_account_number = ? LIMIT 10"),
	ACCOUNT_DETAILS_PS("select * from accounts where account_number = ?"),
	ACCOUNTS_IN_BRANCH_PS("select * from accounts where branch_id = ?"),
	ACCOUNT_BALANCE_PS("select balance, status from accounts where account_number = ?;"),
	CREATE_USER_PS(
			"insert into users(first_name, last_name, date_of_birth, gender, address, mobile, email) value(?,?,?,?,?,?,?);"),
	CREATE_CREDENTIAL_PS("insert into credentials value(?,?);"),
	CREATE_EMPLOYEE_PS("insert into employees value(?,?,?);"),
	CREATE_CUSTOMER_PS("insert into customers value(?,?,?);"),
	CREATE_ACCOUNT_PS("insert into accounts(user_id, type, branch_id, opening_date, balance) value(?,?,?,?,?);"),
	
	UPDATE_USER_DETAILS_PS("update users set %s = ? where user_id = ?"),
	UPDATE_CUSTOMER_DETAILS_PS("update customers set %s = ? where user_id = ?"),
	UPDATE_PASSWORD_PS("update credentials set password = ? where user_id = ?"),

	CREATE_TRANSACTION_PS("insert into transactions value(?,?,?,?,?,?,?,?,?);"), CREATE_NEW_TRANSACTION_PS(
			"insert into transactions(user_id, viewer_account_number, transacted_account_number, transacted_amount, transaction_type, closing_balance, time_stamp, remarks) value(?,?,?,?,?,?,?,?)");

	private String query;

	private MySQLQuery(String query) {
		this.query = query;
	}

	public String getQuery() {
		return this.query;
	}
}

//enum MySQLKeywords {
//	SELECT, FROM, INSERT_INTO, WHERE, 
//}
//
//class MySQLQueryGenerator {
//	public static String generateSelectQuery(List<String> getColumns) {
//		String selectQuery = MySQLKeywords.SELECT + " ";
//		return selectQuery; 
//	}
//}
