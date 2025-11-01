package dataaccess;

import datamodel.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import service.RegisterService;

import static org.junit.jupiter.api.Assertions.*;

public class GetPassTest {

    private DataAccess dataAccess;

    @BeforeEach
    void setUp() throws DataAccessException {
        dataAccess = new MySQLDataAccess();
        dataAccess.clear();
    }

    @Test
    public void getPassPositiveTest() throws Exception {
        var registerService = new RegisterService(dataAccess);
        registerService.register(new UserData("validUsername", "validPassword", "email@gmail.com"));
        assertDoesNotThrow(() -> dataAccess.getPass("validUsername"));
        String res = dataAccess.getPass("validUsername");
        assertTrue(BCrypt.checkpw("validPassword", res));
    }

    @Test
    public void getPassNegativeTest() throws Exception {
        var registerService = new RegisterService(dataAccess);
        registerService.register(new UserData("validUsername", "validPassword", "email@gmail.com"));
        assertThrows(MySQLDataAccessException.class, () -> dataAccess.getPass("invalidPass"));
    }
}
