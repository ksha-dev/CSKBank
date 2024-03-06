package api.mysql;

import java.util.List;
import java.util.StringJoiner;

import api.mysql.MySQLQueryUtil.Fields;
import api.mysql.MySQLQueryUtil.Schemas;
import exceptions.AppException;
import utility.HelperUtil;
import utility.ValidatorUtil;
import utility.HelperUtil.TransactionHistoryLimit;

class MySQLQueryUtil {

	private StringBuilder query;

	public static enum Schemas {
		USERS, EMPLOYEES, CUSTOMERS, ACCOUNTS, TRANSACTIONS, BRANCH, CREDENTIALS;

		public String toString() {
			return super.toString().toLowerCase();
		}
	}

	public static enum Fields {
		USER_ID, PASSWORD, FIRST_NAME, LAST_NAME, DATE_OF_BIRTH, GENDER, ADDRESS, MOBILE, EMAIL, STATUS, TYPE,
		AADHAAR_NUMBER, PAN_NUMBER, ROLE, BRANCH_ID, ACCOUNT_NUMBER, OPENING_DATE, BALANCE, CLOSING_BALANCE,
		TRANSACTION_ID, REMARKS, VIEWER_ACCOUNT_NUMBER, TRANSACTED_ACCOUNT_NUMBER, TRANSACTED_AMOUNT, TRANSACTION_TYPE,
		TIME_STAMP, ALL, PIN;

		public String toString() {
			return super.toString().toLowerCase();
		}
	}

	public MySQLQueryUtil() {
		query = new StringBuilder();
	}

	public void selectField(Fields field) throws AppException {
		ValidatorUtil.validateObject(field);
		query.append("select " + (field == Fields.ALL ? "*" : field));
	}

	public void fromTable(Schemas schema) throws AppException {
		ValidatorUtil.validateObject(schema);
		query.append(" from " + schema);
	}

	public void addTable(Schemas schema) throws AppException {
		ValidatorUtil.validateObject(schema);
		query.append(", " + schema);
	}

	public void addField(Fields field) throws AppException {
		ValidatorUtil.validateObject(field);
		query.append(", " + field);
	}

	public void where() {
		query.append(" where");
	}

	public void and() throws AppException {
		query.append(" and");
	}

	public void fieldEquals(Fields field) throws AppException {
		ValidatorUtil.validateObject(field);
		query.append(" " + field + " = ?");
	}

	public void fieldGreaterThan(Fields field) throws AppException {
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

	public void setField(Fields field) throws AppException {
		ValidatorUtil.validateObject(field);
		query.append(" set " + field + " = ?");
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

	public void insertFields(List<Fields> fields) throws AppException {
		ValidatorUtil.validateCollection(fields);
		StringJoiner joinedFields = new StringJoiner(", ");
		for (Fields field : fields) {
			joinedFields.add(field.toString());
		}
		query.append(" (" + joinedFields + ")");
		insertValuePlaceholders(fields.size());
	}

	public void sortField(Fields field, boolean isDescending) throws AppException {
		ValidatorUtil.validateObject(field);
		query.append(" order by " + field);
		if (isDescending) {
			query.append(" desc");
		}
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

	public static void main(String[] args) throws AppException {
		MySQLQueryUtil queryBuilder = new MySQLQueryUtil();

		queryBuilder.selectField(Fields.ALL);
		queryBuilder.fromTable(Schemas.TRANSACTIONS);
		queryBuilder.where();
		queryBuilder.fieldEquals(Fields.VIEWER_ACCOUNT_NUMBER);

		queryBuilder.and();
		queryBuilder.fieldGreaterThan(Fields.TIME_STAMP);

		queryBuilder.sortField(Fields.TRANSACTION_ID, true);
		queryBuilder.limit(HelperUtil.LIST_LIMIT);
		queryBuilder.offset((2 - 1) * HelperUtil.LIST_LIMIT);
		queryBuilder.end();
		
		System.out.println(queryBuilder.getQuery());
	}
}
