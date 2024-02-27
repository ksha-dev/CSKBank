package utility;

public class SchemaUtil {

//	public static enum Schemas {
//		USERS("users"), EMPLOYEES("employees"), CUSTOMERS("customers"), ACCOUNTS("accounts"),
//		TRANSACTIONS("transactions"), BRANCH("branch");
//
//		private String schemaName;
//
//		private Schemas(String schemaName) {
//			this.schemaName = schemaName;
//		}
//
//		public String toString() {
//			return this.schemaName;
//		}
//	}

//	public static enum ColumnNames {
//		USER_ID("user_id"), PASSWORD("password"), FIRST_NAME("first_name"), LAST_NAME("last_name"),
//		DOB("date_of_birth"), GENDER("gender"), ADDRESS("address"), MOBILE("mobile"), EMAIL("email"), STATUS("status"),
//		TYPE("type"), BRANCH_ID("branch_id");
//
//		private String schemaColumnName;
//
//		private ColumnNames(String columnName) {
//			this.schemaColumnName = columnName;
//		}
//
//		public String toString() {
//			return this.schemaColumnName;
//		}
//	}

	public static enum UserTypes {
		CUSTOMER, EMPLOYEE
	}

	public static enum UserStatus {
		ACTIVE, INACTIVE, BLOCKED
	}

	public static enum Gender {
		MALE, FEMALE, OTHER;

		public String getGendersString() {
			StringBuilder string = new StringBuilder();
			for (Gender gender : Gender.values()) {
				string.append(gender.toString() + " ");
			}
			return string.toString();
		}
	}

	public static String passwordHasher(String password) {
		
		return "";
	}
}
