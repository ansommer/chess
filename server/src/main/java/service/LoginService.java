package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import datamodel.LoginResult;
import datamodel.RegistrationResult;
import datamodel.User;

public class LoginService {
    private final MemoryDataAccess dataAccess;

    public LoginService(MemoryDataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public LoginResult login(User user) throws UnauthorizedException {
        if (!dataAccess.userExists(user.username())) {
            throw new UnauthorizedException("Error: unauthorized");
        } else if (user.username() == null || user.password() == null) { //check that it has username and password
            throw new BadRequestException("Error: bad request");
        }

        return new LoginResult(user.username(), "whateveritsnotreal");
    }
}
