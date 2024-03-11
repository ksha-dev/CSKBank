package consoleRunner;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import consoleRunner.utility.InputUtil;
import consoleRunner.utility.LoggingUtil;
import exceptions.AppException;
import exceptions.messages.ActivityExceptionMessages;
import helpers.Branch;
import helpers.EmployeeRecord;
import operations.AdminOperations;
import utility.ConstantsUtil;
import utility.ConstantsUtil.EmployeeType;
import utility.ConstantsUtil.ModifiableField;
import utility.ValidatorUtil;

class AdminRunner {

	public Logger log = LoggingUtil.DEFAULT_LOGGER;

	public void run(EmployeeRecord employee) throws AppException {
		AdminOperations operations = new AdminOperations(employee);
		LoggingUtil.logEmployeeRecord(employee);
		boolean isProgramActive = true;
		int runnerOperations = 8;

		while (isProgramActive) {

			if (!AppRunner.serverConnectionActive) {
				isProgramActive = false;
				log.info(ActivityExceptionMessages.SERVER_CONNECTION_LOST.toString());
				break;
			}

			log.info("=".repeat(15) + "ADMIN PORTAL" + "=".repeat(15)
					+ "\nEnter a number to perform the following operation : "
					+ "\n1 - Get all the employees in your branch" + "\n2 - Get all the employees of any branch"
					+ "\n3 - Create an Employee" + "\n\nTo go back, enter 0\n" + "-".repeat(30));

			int choice = -1;
			do {
				try {
					log.info("Enter your choice (0 to " + runnerOperations + "): ");
					choice = InputUtil.getInteger();
				} catch (Exception e) {
					log.info(e.getMessage());
				}
			} while (choice < 0 || choice > runnerOperations);

			try {
				switch (choice) {
				case 0: {
					isProgramActive = false;
					break;
				}

				case 1: {
					int pageNumber = 1;
					boolean isListFull = false;
					do {
						Map<Integer, EmployeeRecord> employees = operations.getEmployeesInBrach(employee.getBranchId(),
								pageNumber);
						LoggingUtil.logEmployeeRecordList(employees);
						isListFull = employees.size() == ConstantsUtil.LIST_LIMIT;
						if (isListFull) {
							log.info("Enter 1 to go to next page (or) 0 to exit : ");
							if (InputUtil.getPositiveInteger() == 1) {
								pageNumber++;
							} else {
								isListFull = false;
							}
						}
					} while (isListFull);
				}
					break;

				case 2: {
					log.info("Enter Brach ID to get Employees : ");
					int branchId = InputUtil.getPositiveInteger();
					int pageNumber = 1;
					boolean isListFull = false;
					do {
						Map<Integer, EmployeeRecord> employees = operations.getEmployeesInBrach(branchId, pageNumber);
						LoggingUtil.logEmployeeRecordList(employees);
						isListFull = employees.size() == ConstantsUtil.LIST_LIMIT;
						if (isListFull) {
							log.info("Enter 1 to go to next page (or) 0 to exit : ");
							if (InputUtil.getPositiveInteger() == 1) {
								pageNumber++;
							} else {
								isListFull = false;
							}
						}
					} while (isListFull);
				}
					break;

				case 3: {
					EmployeeRecord newEmployeeRecord = new EmployeeRecord();
					log.info("Enter Employee First Name : ");
					newEmployeeRecord.setFirstName(InputUtil.getString());

					log.info("Enter Employee Last Name : ");
					newEmployeeRecord.setLastName(InputUtil.getString());

					log.info("Enter Gender (MALE, FEMALE, OTHER): ");
					newEmployeeRecord.setGender(InputUtil.getString());

					log.info("Enter Date of Birth in ddmmyyyy format: ");
					newEmployeeRecord.setDateOfBirth(InputUtil.getDate());

					log.info("Enter Address : ");
					newEmployeeRecord.setAddress(InputUtil.getString());

					log.info("Enter Phone number : ");
					newEmployeeRecord.setPhone(InputUtil.getPositiveLong());

					log.info("Enter email ID : ");
					newEmployeeRecord.setEmail(InputUtil.getString());

					log.info("Enter Brach ID : ");
					newEmployeeRecord.setBranchId(InputUtil.getPositiveInteger());

					if (operations.createEmployee(newEmployeeRecord)) {
						log.info("Employee Created");
						LoggingUtil.logEmployeeRecord(newEmployeeRecord);
					} else {
						log.info("Could not create employee");
					}
				}
					break;

				case 4: {
					log.info("Enter employee id of the employee to modify details : ");
					int employeeID = InputUtil.getPositiveInteger();
					ValidatorUtil.validateId(employeeID);
					List<ModifiableField> fields = List.of(ModifiableField.ROLE, ModifiableField.BRANCH_ID);
					int i = 0;
					for (ModifiableField field : fields) {
						log.info(++i + " -> " + field);
					}
					System.out.println(i);
					log.info("Enter an associated number to modify : ");
					i = InputUtil.getPositiveInteger();
					if (i > fields.size()) {
						throw new AppException("Invalid number");
					}
					ModifiableField selectedField = fields.get(i - 1);
					System.out.println(selectedField);
					Object value = null;
					switch (selectedField) {
					case ROLE: {
						log.info("Enter 1 to set as Employee or 0 for Admin : ");
						int temp = InputUtil.getPositiveInteger();
						if (temp == 1) {
							value = EmployeeType.EMPLOYEE.toString();
						} else if (temp == 0) {
							value = EmployeeType.ADMIN.toString();
						} else {
							throw new AppException("Invalid Selection");
						}
					}
						break;

					case BRANCH_ID: {
						log.info("Enter branch ID : ");
						int temp = InputUtil.getPositiveInteger();
						ValidatorUtil.validateId(temp);
						value = temp;
					}
						break;
					default:
						throw new AppException("Invalid selection");
					}
					if (operations.update(employeeID, selectedField, value)) {
						log.info("Update Successful");
					} else {
						log.info("Update Failed");
					}
				}
					break;

				case 5: {
					Branch newBranch = new Branch();
					log.info("Enter Branch Address : ");
					newBranch.setAddress(InputUtil.getString());

					log.info("Enter Phone : ");
					newBranch.setPhone(InputUtil.getPositiveLong());

					log.info("Enter Email ID : ");
					newBranch.setEmail(InputUtil.getString());

					if (operations.createBranch(newBranch)) {
						log.info("Branch Successfully created");

					}
				}
					break;

				case 6: {
					log.info("Enter branch ID to change its details : ");
					int branchId = InputUtil.getPositiveInteger();
					ValidatorUtil.validateId(branchId);
					List<ModifiableField> fields = List.of(ModifiableField.ADDRESS, ModifiableField.PHONE,
							ModifiableField.PHONE);
					int i = 0;
					for (ModifiableField field : fields) {
						log.info(++i + " : " + field);
					}
					log.info("Enter an associated number : ");
					i = InputUtil.getPositiveInteger();
					if (i > fields.size()) {
						throw new AppException("Invalid Selection");
					}
					ModifiableField selectedField = fields.get(i - 1);
					Object value = null;
					switch (selectedField) {
					case ADDRESS:
						log.info("Enter Address : ");
						value = InputUtil.getString();
						break;

					case PHONE:
						log.info("Enter phone number : ");
						long phone = InputUtil.getPositiveLong();
						ValidatorUtil.validateMobileNumber(phone);
						value = phone;
						break;

					case EMAIL:
						log.info("Enter email ID : ");
						String email = InputUtil.getString();
						ValidatorUtil.validateEmail(email);
						value = email;
						break;

					default:
						throw new AppException("Invalid selection");
					}
					if (operations.updateBranchDetails(branchId, selectedField, value)) {
						log.info("Update successful");
					}
				}
					break;

				default:
					log.info("The choice is invalid");
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info(e.getMessage());
			}
		}
	}
}