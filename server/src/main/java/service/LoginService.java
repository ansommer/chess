package service;

import dataaccess.MemoryDataAccess;
import datamodel.AuthData;
import datamodel.UserData;

import java.util.UUID;

public class LoginService {
    private final MemoryDataAccess dataAccess;

    public LoginService(MemoryDataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public AuthData login(UserData user) throws UnauthorizedException {
        if (user.username() == null || user.password() == null) { //check that it has username and password
            throw new BadRequestException("Error: bad request");
        } else if (!dataAccess.userExists(user.username()) || !dataAccess.getPass(user.username()).equals(user.password())) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        String authToken = generateToken();
        dataAccess.saveAuth(new AuthData(user.username(), authToken));
        return new AuthData(user.username(), authToken);
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
