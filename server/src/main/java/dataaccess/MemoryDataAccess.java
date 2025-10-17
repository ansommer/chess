package dataaccess;

import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;

import java.util.HashMap;

public class MemoryDataAccess implements DataAccess {
    private HashMap<String, UserData> users = new HashMap<>();
    private HashMap<String, AuthData> authTokens = new HashMap<>();
    private HashMap<String, GameData> games = new HashMap<>();

    @Override
    public void saveUser(UserData user) {
        users.put(user.username(), user);
    }

    @Override
    public void getUser(String username) {
        users.get(username);
    }

    //should this be in the interface and then in that case override
    //also this isn't following the void format of the other ones... could/should I do that?
    public String getPass(String username) {
        UserData user = users.get(username);
        return (user == null) ? null : user.password();
    }

    public boolean userExists(String username) {
        return users.containsKey(username);
    }

    public void clear() {
        users.clear();
        authTokens.clear();
        games.clear();
    }


}