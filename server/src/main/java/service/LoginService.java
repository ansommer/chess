package service;

import dataaccess.MemoryDataAccess;
import datamodel.LoginResult;
import datamodel.UserData;

public class LoginService {
    private final MemoryDataAccess dataAccess;

    public LoginService(MemoryDataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public LoginResult login(UserData user) throws UnauthorizedException {
        if (user.username() == null || user.password() == null) { //check that it has username and password
            throw new BadRequestException("Error: bad request");
        } else if (!dataAccess.userExists(user.username()) || !dataAccess.getPass(user.username()).equals(user.password())) {
            throw new UnauthorizedException("Error: unauthorized");
        }

        return new LoginResult(user.username(), "whateveritsnotreal");
    }
}
