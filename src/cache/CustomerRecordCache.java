package cache;

import api.UserAPI;
import exceptions.AppException;
import exceptions.messages.ActivityExceptionMessages;
import modules.CustomerRecord;
import modules.UserRecord;
import utility.ConstantsUtil.UserType;

public class CustomerRecordCache extends Cache<Integer, CustomerRecord> {

	private UserAPI userAPIObject;

	public CustomerRecordCache(int capacity, UserAPI userAPIObject) {
		super(capacity);
		this.userAPIObject = userAPIObject;
	}

	@Override
	protected CustomerRecord fetchData(Integer key) throws AppException {
		UserRecord user = userAPIObject.getUserDetails(key);
		if (user.getType() != UserType.CUSTOMER) {
			throw new AppException(ActivityExceptionMessages.NO_CUSTOMER_RECORD_FOUND);
		}
		return (CustomerRecord) user;
	}
}