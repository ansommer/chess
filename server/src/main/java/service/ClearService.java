package service;

import dataaccess.MemoryDataAccess;

public class ClearService {
    private final MemoryDataAccess dataAccess;

    public ClearService(MemoryDataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public String clear() {
        //are there any kind of exceptions I need to put here?
        dataAccess.clear();
        return "{}";
    }
}
