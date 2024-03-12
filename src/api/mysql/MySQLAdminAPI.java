package api.mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import api.AdminAPI;
import api.mysql.MySQLQuery.Column;
import api.mysql.MySQLQuery.Schemas;
import exceptions.AppException;
import exceptions.messages.APIExceptionMessage;
import modules.Account;
import modules.Branch;
import modules.EmployeeRecord;
import utility.ValidatorUtil;
import utility.ConstantsUtil.ModifiableField;
import utility.ConstantsUtil;
import utility.ConvertorUtil;

public class MySQLAdminAPI extends MySQLEmployeeAPI implements AdminAPI {

	@Override
	public boolean createEmployee(EmployeeRecord employee) throws AppException {
		ValidatorUtil.validateObject(employee);
		try {
			ServerConnection.startTransaction();
			createUserRecord(employee);

			MySQLQuery queryBuilder = new MySQLQuery();
			queryBuilder.insertInto(Schemas.EMPLOYEES);
			queryBuilder.insertValuePlaceholders(2);
			queryBuilder.end();

			PreparedStatement statement = ServerConnection.getServerConnection()
					.prepareStatement(queryBuilder.getQuery());
			statement.setInt(1, employee.getUserId());
			statement.setInt(2, employee.getBranchId());
			int response = statement.executeUpdate();
			statement.close();
			if (response == 1) {
				ServerConnection.endTransaction();
				return true;
			} else {
				throw new AppException(APIExceptionMessage.USER_CREATION_FAILED);
			}
		} catch (SQLException e) {
			ServerConnection.reverseTransaction();
			throw new AppException(e.getMessage());
		}
	}

	@Override
	public boolean updateEmployeeDetails(int employeeId, ModifiableField field, Object value) throws AppException {
		ValidatorUtil.validateId(employeeId);
		ValidatorUtil.validateObject(value);
		ValidatorUtil.validateObject(field);

		MySQLQuery queryBuilder = new MySQLQuery();
		queryBuilder.update(Schemas.EMPLOYEES);
		queryBuilder.setColumn(Column.valueOf(field.toString()));
		queryBuilder.where();
		queryBuilder.columnEquals(Column.USER_ID);
		queryBuilder.end();

		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(queryBuilder.getQuery())) {
			statement.setObject(1, value);
			statement.setInt(2, employeeId);
			int response = statement.executeUpdate();
			if (response == 1) {
				return true;
			} else {
				throw new AppException(APIExceptionMessage.UPDATE_FAILED);
			}
		} catch (SQLException e) {
			throw new AppException(APIExceptionMessage.UNKNOWN_USER_OR_BRANCH);
		}
	}

	private void updateBrachIFSC(int branchId) throws AppException {
		MySQLQuery queryBuilder = new MySQLQuery();
		queryBuilder.update(Schemas.BRANCH);
		queryBuilder.setColumn(Column.IFSC_CODE);
		queryBuilder.where();
		queryBuilder.columnEquals(Column.BRANCH_ID);
		queryBuilder.and();
		queryBuilder.columnEquals(Column.IFSC_CODE);
		queryBuilder.end();

		System.out.println(queryBuilder.getQuery());
		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(queryBuilder.getQuery())) {
			statement.setString(1, ConvertorUtil.ifscGenerator(branchId));
			statement.setInt(2, branchId);
			statement.setNull(3, Types.NULL);
			System.out.println(statement);
			int response = statement.executeUpdate();
			if (response != 1) {
				throw new AppException(APIExceptionMessage.IFSC_CODE_UPDATE_FAILED);
			}
		} catch (SQLException e) {
			throw new AppException(e.getMessage());
		}
	}

	private int createBranchAndGetId(Branch branch) throws AppException {
		ValidatorUtil.validateObject(branch);

		MySQLQuery queryBuilder = new MySQLQuery();
		queryBuilder.insertInto(Schemas.BRANCH);
		queryBuilder.insertColumns(List.of(Column.ADDRESS, Column.PHONE, Column.EMAIL));
		queryBuilder.end();
		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(queryBuilder.getQuery(), Statement.RETURN_GENERATED_KEYS)) {
			statement.setString(1, branch.getAddress());
			statement.setLong(2, branch.getPhone());
			statement.setString(3, branch.getEmail());
			System.out.println(statement);
			try (ResultSet result = statement.getGeneratedKeys()) {
				if (result.next()) {
					System.out.println(result.getObject(1));
					return result.getInt(1);
				} else {
					throw new AppException(APIExceptionMessage.BRANCH_CREATION_FAILED);
				}
			}
		} catch (SQLException e) {
			throw new AppException(e.getMessage());
		}
	}

	@Override
	public boolean createBranch(Branch branch) throws AppException {
		try {
			int branchId = createBranchAndGetId(branch);
			updateBrachIFSC(branchId);
			return true;
		} catch (AppException e) {
			throw new AppException(e.getMessage());
		}

	}

	@Override
	public boolean updateBranchDetails(int branchId, ModifiableField field, Object value) throws AppException {
		ValidatorUtil.validateObject(value);
		ValidatorUtil.validateObject(field);
		ValidatorUtil.validateId(branchId);

		MySQLQuery queryBuilder = new MySQLQuery();
		queryBuilder.update(Schemas.BRANCH);
		queryBuilder.setColumn(Column.valueOf(field.toString()));
		queryBuilder.where();
		queryBuilder.columnEquals(Column.BRANCH_ID);
		queryBuilder.end();

		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(queryBuilder.getQuery())) {
			statement.setObject(1, value);
			statement.setInt(2, branchId);

			int response = statement.executeUpdate();
			if (response != 1) {
				return true;
			} else {
				throw new AppException(APIExceptionMessage.UPDATE_FAILED);
			}
		} catch (SQLException e) {
			throw new AppException(e.getMessage());
		}
	}

	@Override
	public Map<Long, Account> viewAccountsInBank(int pageNumber) throws AppException {

		MySQLQuery queryBuilder = new MySQLQuery();
		queryBuilder.selectColumn(Column.ALL);
		queryBuilder.fromSchema(Schemas.ACCOUNTS);
		queryBuilder.sortField(Column.ACCOUNT_NUMBER, true);
		queryBuilder.limit(ConstantsUtil.LIST_LIMIT);
		queryBuilder.offset(ConvertorUtil.convertPageToOffset(pageNumber));
		queryBuilder.end();

		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(queryBuilder.getQuery())) {
			try (ResultSet accountRS = statement.executeQuery()) {
				Map<Long, Account> accounts = new HashMap<Long, Account>();
				while (accountRS.next()) {
					accounts.put(accountRS.getLong(1), MySQLConversionUtil.convertToAccount(accountRS));
				}
				return accounts;
			}
		} catch (SQLException e) {
			throw new AppException(e.getMessage());
		}
	}

	@Override
	public Map<Integer, EmployeeRecord> getEmployeesInBranch(int branchID, int pageNumber) throws AppException {
		Map<Integer, EmployeeRecord> employees = new HashMap<Integer, EmployeeRecord>();
		ValidatorUtil.validateId(branchID);

		MySQLQuery queryBuilder = new MySQLQuery();
		queryBuilder.selectColumn(Column.ALL);
		queryBuilder.fromSchema(Schemas.EMPLOYEES);
		queryBuilder.where();
		queryBuilder.columnEquals(Column.BRANCH_ID);
		queryBuilder.limit(ConstantsUtil.LIST_LIMIT);
		queryBuilder.offset(ConvertorUtil.convertPageToOffset(pageNumber));
		queryBuilder.end();

		try (PreparedStatement statement = ServerConnection.getServerConnection()
				.prepareStatement(queryBuilder.getQuery())) {
			statement.setInt(1, branchID);
			try (ResultSet result = statement.executeQuery()) {
				while (result.next()) {
					EmployeeRecord employee = MySQLConversionUtil.convertToEmployeeRecord(result);
					MySQLAPIUtil.getAndUpdateUserRecord(employee);
					employees.put(employee.getUserId(), employee);
				}
				return employees;
			}
		} catch (Exception e) {
			throw new AppException();
		}
	}
}
