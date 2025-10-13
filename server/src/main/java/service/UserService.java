package service;

import dataaccess.DataAccess;
import datamodel.User;
import datamodel.RegistrationResult;

public class UserService {
    private DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public RegistrationResult register(User user) {
        dataAccess.saveUser(user);
        return new RegistrationResult(user.username(), "whateveritsnotreal");

    }
}
