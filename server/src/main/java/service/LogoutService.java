package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import datamodel.AuthData;
import datamodel.UserData;

public class LogoutService {
    private final DataAccess dataAccess;

    public LogoutService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public String logout(String authToken) throws UnauthorizedException, DataAccessException {
        if (authToken == null || !dataAccess.authExists(authToken)) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        dataAccess.deleteAuth(authToken);
        return "{}";
    }
}
