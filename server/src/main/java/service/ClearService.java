package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import dataaccess.MySQLDataAccessException;

public class ClearService {
    private final DataAccess dataAccess;

    public ClearService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public String clear() throws MySQLDataAccessException {
        //are there any kind of exceptions I need to put here?
        dataAccess.clear();
        return "{}";
    }
}
