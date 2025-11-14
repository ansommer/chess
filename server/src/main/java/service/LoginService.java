package service;

import commonMisconceptions.BadRequestException;
import dataaccess.DataAccess;
import dataaccess.MySQLDataAccessException;
import datamodel.AuthData;
import datamodel.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

public class LoginService {
    private final DataAccess dataAccess;

    public LoginService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public AuthData login(UserData user) throws UnauthorizedException, MySQLDataAccessException {
        if (user.username() == null || user.password() == null) { //check that it has username and password
            throw new BadRequestException("Error: bad request");
        } else if (!dataAccess.userExists(user.username()) || !BCrypt.checkpw(user.password(), dataAccess.getPass(user.username()))) {
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
