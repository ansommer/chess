package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import dataaccess.MySQLDataAccessException;


public class LogoutService {
    private final DataAccess dataAccess;

    public LogoutService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public String logout(String authToken) throws UnauthorizedException, MySQLDataAccessException {
        if (authToken == null || !dataAccess.authExists(authToken)) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        dataAccess.deleteAuth(authToken);
        return "{}";
    }
}
