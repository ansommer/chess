package dataaccess;

public class MySQLDataAccessException extends RuntimeException {
    public MySQLDataAccessException(String message) {
        super(message);
    }

    public MySQLDataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
