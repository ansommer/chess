package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import datamodel.User;
import datamodel.RegistrationResult;

public class RegisterService {
    private final MemoryDataAccess dataAccess;

    public RegisterService(MemoryDataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public RegistrationResult register(User user) throws DataAccessException {

        if (dataAccess.userExists(user.username())) { //check that it's not already taken
            throw new DataAccessException("Error: already taken");
        } else if (user.username() == null || user.password() == null) { //check that it has username and password
            throw new BadRequestException("Error: bad request");
        }

        dataAccess.saveUser(user);
        return new RegistrationResult(user.username(), "whateveritsnotreal");
    }
}
