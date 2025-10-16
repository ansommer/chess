package dataaccess;

import datamodel.User;

import java.util.HashMap;

public class MemoryDataAccess implements DataAccess {
    private HashMap<String, User> users = new HashMap<>();

    @Override
    public void saveUser(User user) {
        users.put(user.username(), user);
    }

    @Override
    public void getUser(String username) {
        users.get(username);
    }

    //should this be in the interface and then in that case override
    //also this isn't following the void format of the other ones... could/should I do that?
    public String getPass(String username) {
        User user = users.get(username);
        return (user == null) ? null : user.password();
    }

    public boolean userExists(String username) {
        return users.containsKey(username);
    }

}