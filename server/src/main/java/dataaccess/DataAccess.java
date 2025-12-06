package dataaccess;

import datamodel.AuthData;
import datamodel.GameData;
import datamodel.UserData;

import java.util.HashMap;

public interface DataAccess {
    void saveUser(UserData user) throws MySQLDataAccessException;

    UserData getUser(String username) throws MySQLDataAccessException;

    String getUserFromAuthToken(String authToken) throws MySQLDataAccessException;

    HashMap<String, GameData> getGames() throws MySQLDataAccessException;

    GameData getOneGame(int gameID) throws MySQLDataAccessException;

    int getNextGameId();

    void createGame(GameData game) throws MySQLDataAccessException;

    void deleteAuth(String auth) throws MySQLDataAccessException;

    void saveAuth(AuthData auth) throws MySQLDataAccessException;

    boolean authExists(String auth) throws MySQLDataAccessException;

    //String getAuth(String username) throws MySQLDataAccessException;

    String getPass(String username) throws MySQLDataAccessException;

    boolean userExists(String username) throws MySQLDataAccessException;

    void clear() throws MySQLDataAccessException;

    void updateGame(GameData game) throws MySQLDataAccessException;
}
