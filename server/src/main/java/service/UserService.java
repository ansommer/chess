package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import datamodel.User;
import datamodel.RegistrationResult;

public class UserService {
    private MemoryDataAccess dataAccess;

    public UserService(MemoryDataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public RegistrationResult register(User user) throws DataAccessException {
        //check that it doesn't already exist
        if (dataAccess.userExists(user.username())) {
            throw new DataAccessException("Error: User with username '" + user.username() + "' already exists.");
        }

        //check that it has username and password
        dataAccess.saveUser(user);
        return new RegistrationResult(user.username(), "whateveritsnotreal");
    }
}
