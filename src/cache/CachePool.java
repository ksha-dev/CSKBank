package cache;

import java.util.Objects;

import api.UserAPI;
import exceptions.AppException;
import modules.Account;
import modules.Branch;
import modules.UserRecord;
import utility.ConstantsUtil;
import utility.ValidatorUtil;

public class CachePool {
	private static UserAPI userAPI;
	private static Cache<Integer, UserRecord> userRecordCache;
	private static Cache<Long, Account> accountCache;
	private static Cache<Integer, Branch> branchCache;

	private static void validateAPI() throws AppException {
		if (Objects.isNull(userAPI)) {
			throw new AppException("Cache has not been initialized");
		}
	}


	private CachePool() {
	}

	public static void initializeCache(UserAPI userAPI) throws AppException {
		if (Objects.isNull(CachePool.userAPI)) {
			synchronized (CachePool.class) {
				if (Objects.isNull(CachePool.userAPI)) {
					ValidatorUtil.validateObject(userAPI);
					CachePool.userAPI = userAPI;
					userRecordCache = new UserRecordCache(userAPI, ConstantsUtil.CACHE_SIZE);
					accountCache = new AccountCache(userAPI, ConstantsUtil.CACHE_SIZE);
					branchCache = new BranchCache(userAPI, ConstantsUtil.CACHE_SIZE);
				} 
			}
		}
	}

	public static Cache<Integer, UserRecord> getUserRecordCache() throws AppException {
		validateAPI();
		return userRecordCache;
	}

	public static Cache<Long, Account> getAccountCache() throws AppException {
		validateAPI();
		return accountCache;
	}

	public static Cache<Integer, Branch> getBranchCache() throws AppException {
		validateAPI();
		return branchCache;
	}
}
