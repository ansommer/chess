package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;

public class ClearService {
    private final DataAccess dataAccess;

    public ClearService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public String clear() throws DataAccessException {
        //are there any kind of exceptions I need to put here?
        dataAccess.clear();
        return "{}";
    }
}
