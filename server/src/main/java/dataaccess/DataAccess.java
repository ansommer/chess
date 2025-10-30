package dataaccess;

import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;

import java.util.HashMap;

public interface DataAccess {
    void saveUser(UserData user);

    void getUser(String username);

    String getUserFromAuthToken(String authToken);

    HashMap<String, GameData> getGames();

    GameData getOneGame(int gameID);

    int getNextGameId();

    void createGame(GameData game);

    void deleteAuth(String auth);

    void saveAuth(AuthData auth);

    boolean authExists(String auth);

    String getPass(String username);

    boolean userExists(String username);

    void clear();
}
