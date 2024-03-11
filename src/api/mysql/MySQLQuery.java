package api.mysql;

import java.util.List;
import java.util.StringJoiner;

import exceptions.AppException;
import utility.ValidatorUtil;

class MySQLQuery {

	private StringBuilder query;

	public static enum Schemas {
		USERS, EMPLOYEES, CUSTOMERS, ACCOUNTS, TRANSACTIONS, BRANCH, CREDENTIALS;

		public String toString() {
			return super.toString().toLowerCase();
		}
	}

	public static enum Column {
		USER_ID, PASSWORD, FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, GENDER, ADDRESS, PHONE, EMAIL, STATUS, TYPE,
		AADHAAR_NUMBER, PAN_NUMBER, ROLE, BRANCH_ID, ACCOUNT_NUMBER, OPENING_DATE, BALANCE, CLOSING_BALANCE,
		TRANSACTION_ID, REMARKS, VIEWER_ACCOUNT_NUMBER, TRANSACTED_ACCOUNT_NUMBER, TRANSACTED_AMOUNT, TRANSACTION_TYPE,
		TIME_STAMP, ALL, PIN, LAST_TRANSACTED_AT, IFSC_CODE;

		public String toString() {
			return super.toString().toLowerCase();
		}
	}

	public MySQLQuery() {
		query = new StringBuilder();
	}

	public void selectColumn(Column field) throws AppException {
		ValidatorUtil.validateObject(field);
		query.append("select " + (field == Column.ALL ? "*" : field));
	}

	public void fromSchema(Schemas schema) throws AppException {
		ValidatorUtil.validateObject(schema);
		query.append(" from " + schema);
	}

	public void addSchema(Schemas schema) throws AppException {
		ValidatorUtil.validateObject(schema);
		query.append(", " + schema);
	}

	public void addColumn(Column field) throws AppException {
		ValidatorUtil.validateObject(field);
		query.append(", " + field);
	}

	public void where() {
		query.append(" where");
	}

	public void and() {
		query.append(" and");
	}

	public void not() {
		query.append(" not");
	}

	public void combinationStart() {
		query.append(" (");
	}

	public void combinationEnd() {
		query.append(")");
	}

	public void columnEquals(Column field) throws AppException {
		ValidatorUtil.validateObject(field);
		query.append(" " + field + " = ?");
	}

	public void columnGreaterThan(Column field) throws AppException {
		ValidatorUtil.validateObject(field);
		query.append(" " + field + " > ?");
	}

	public void update(Schemas schema) throws AppException {
		ValidatorUtil.validateObject(schema);
		query.append("update " + schema);
	}

	public void end() {
		query.append(";");
	}

	public void setColumn(Column field) throws AppException {
		ValidatorUtil.validateObject(field);
		query.append(" set " + field + " = ?");
	}

	public void sortField(Column field, boolean isDescending) throws AppException {
		ValidatorUtil.validateObject(field);
		query.append(" order by " + field);
		if (isDescending) {
			query.append(" desc");
		}
	}

	public void limit(int limit) throws AppException {
		ValidatorUtil.validatePositiveNumber(limit);
		query.append(" limit " + limit);
	}

	public void offset(int offset) throws AppException {
		ValidatorUtil.validatePositiveNumber(offset);
		query.append(" offset " + offset);
	}

	public void insertInto(Schemas schema) throws AppException {
		ValidatorUtil.validateObject(schema);
		query.append("insert into " + schema);
	}

	public void insertColumns(List<Column> fields) throws AppException {
		ValidatorUtil.validateCollection(fields);
		StringJoiner joinedFields = new StringJoiner(", ");
		for (Column field : fields) {
			joinedFields.add(field.toString());
		}
		query.append(" (" + joinedFields + ")");
		insertValuePlaceholders(fields.size());
	}

	public void insertValuePlaceholders(int valueCount) throws AppException {
		ValidatorUtil.validatePositiveNumber(valueCount);
		StringJoiner joinedValues = new StringJoiner(", ");
		for (int i = 0; i < valueCount; i++) {
			joinedValues.add("?");
		}
		query.append(" value (" + joinedValues + ")");
	}

	public String getQuery() {
		return query.toString();
	}
}
