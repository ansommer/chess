package service;

import dataaccess.MemoryDataAccess;
import datamodel.AuthData;
import datamodel.UserData;

public class LogoutService {
    private final MemoryDataAccess dataAccess;

    public LogoutService(MemoryDataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public String logout(String authToken) throws UnauthorizedException {
        if (authToken == null || !dataAccess.authExists(authToken)) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        dataAccess.deleteAuth(authToken);
        return "{}";
    }
}
