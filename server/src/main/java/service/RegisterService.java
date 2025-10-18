package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import datamodel.AuthData;
import datamodel.UserData;

import java.util.UUID;

public class RegisterService {
    private final MemoryDataAccess dataAccess;

    public RegisterService(MemoryDataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public AuthData register(UserData user) throws DataAccessException {

        if (dataAccess.userExists(user.username())) { //check that it's not already taken
            throw new DataAccessException("Error: already taken");
        } else if (user.username() == null || user.password() == null) { //check that it has username and password
            throw new BadRequestException("Error: bad request");
        }

        String authToken = generateToken();

        AuthData authData = new AuthData(user.username(), authToken);
        dataAccess.saveUser(user);
        dataAccess.saveAuth(authData);
        return authData;
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
