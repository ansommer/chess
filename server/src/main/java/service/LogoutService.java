package service;

import dataaccess.DataAccess;
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

    public static class BadRequestException extends RuntimeException {
        public BadRequestException(String message) {
            super(message);
        }
    }
}
