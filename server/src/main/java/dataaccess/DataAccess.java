package dataaccess;

import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;

import javax.xml.crypto.Data;
import java.util.HashMap;

public interface DataAccess {
    void saveUser(UserData user) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    String getUserFromAuthToken(String authToken);

    HashMap<String, GameData> getGames();

    GameData getOneGame(int gameID);

    int getNextGameId();

    void createGame(GameData game);

    void deleteAuth(String auth);

    void saveAuth(AuthData auth) throws DataAccessException;

    boolean authExists(String auth);

    String getPass(String username) throws DataAccessException;

    boolean userExists(String username) throws DataAccessException;

    void clear() throws DataAccessException;
}
