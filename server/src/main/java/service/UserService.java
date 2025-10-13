package service;

import datamodel.User;
import datamodel.RegistrationResult;

public class UserService {
    public RegistrationResult register(User user) {
        return new RegistrationResult(user.username(), "whateveritsnotreal");

    }
}
