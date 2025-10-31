package dataaccess;

import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;

import javax.xml.crypto.Data;
import java.util.HashMap;

public interface DataAccess {
    void saveUser(UserData user) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    String getUserFromAuthToken(String authToken) throws DataAccessException;

    HashMap<String, GameData> getGames() throws DataAccessException;

    GameData getOneGame(int gameID) throws DataAccessException;

    int getNextGameId();

    void createGame(GameData game) throws DataAccessException;

    void deleteAuth(String auth) throws DataAccessException;

    void saveAuth(AuthData auth) throws DataAccessException;

    boolean authExists(String auth) throws DataAccessException;

    String getAuth(String username) throws DataAccessException;

    String getPass(String username) throws DataAccessException;

    boolean userExists(String username) throws DataAccessException;

    void clear() throws DataAccessException;
}
