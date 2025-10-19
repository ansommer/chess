package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import datamodel.*;
import io.javalin.*;
import io.javalin.http.Context;
import service.*;
import chess.ChessGame.TeamColor;


public class Server {

    private final Javalin server;
    private RegisterService registerService;
    private LoginService loginService;
    private MemoryDataAccess dataAccess;
    private ClearService clearService;
    private LogoutService logoutService;
    private CreateGameService createGameService;
    private ListGamesService listGamesService;
    private JoinService joinService;

    public Server() {
        dataAccess = new MemoryDataAccess();
        registerService = new RegisterService(dataAccess);
        loginService = new LoginService(dataAccess);
        clearService = new ClearService(dataAccess);
        logoutService = new LogoutService(dataAccess);
        createGameService = new CreateGameService(dataAccess);
        listGamesService = new ListGamesService(dataAccess);
        joinService = new JoinService(dataAccess);

        server = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        server.delete("db", this::clearHandler);
        server.post("user", this::registerHandler);
        server.post("session", this::loginHandler);
        server.delete("session", this::logoutHandler);
        server.post("game", this::createGameHandler);
        server.get("game", this::listGamesHandler);
        server.put("game", this::joinGameHandler);
    }

    private void clearHandler(Context ctx) {
        var serializer = new Gson();
        try {
            var res = clearService.clear();
            ctx.status(200);
            ctx.result(res);
        } catch (Exception e) {
            ctx.status(500);
            ctx.result("{\"message\": \"Error clearing database\"}");
        }

    }

    private void registerHandler(Context ctx) {
        var serializer = new Gson();

        try {
            String reqJson = ctx.body();
            var req = serializer.fromJson(reqJson, UserData.class);
            var res = registerService.register(req);
            ctx.status(200);
            ctx.result(serializer.toJson(res));

        } catch (DataAccessException e) { // Duplicate user
            ctx.status(403);
            //chat was suggesting to make a helper class for the error message, so maybe do that at some point
            String errorMessage = "{\"message\": \"" + e.getMessage() + "\"}";
            ctx.result(errorMessage);
        } catch (BadRequestException e) {
            ctx.status(400);
            String errorMessage = "{\"message\": \"" + e.getMessage() + "\"}";
            ctx.result(errorMessage);
        }
    }

    private void loginHandler(Context ctx) {
        var serializer = new Gson();
        try {
            String reqJson = ctx.body();
            var req = serializer.fromJson(reqJson, UserData.class);
            var res = loginService.login(req);
            ctx.status(200);
            ctx.result(serializer.toJson(res));
        } catch (UnauthorizedException e) {
            ctx.status(401);
            String errorMessage = "{\"message\": \"" + e.getMessage() + "\"}";
            ctx.result(errorMessage);
        } catch (BadRequestException e) {
            ctx.status(400);
            String errorMessage = "{\"message\": \"" + e.getMessage() + "\"}";
            ctx.result(errorMessage);
        }
    }

    private void logoutHandler(Context ctx) {
        var serializer = new Gson();

        try {
            String authToken = ctx.header("Authorization");
            var res = logoutService.logout(authToken);
            ctx.status(200);
            ctx.result(res);
        } catch (UnauthorizedException e) {
            ctx.status(401);
            String errorMessage = "{\"message\": \"" + e.getMessage() + "\"}";
            ctx.result(errorMessage);
        }
    }

    private void createGameHandler(Context ctx) {
        var serializer = new Gson();
        try {
            String reqJson = ctx.body();
            var req = serializer.fromJson(reqJson, GameRequest.class);
            String gameName = req.gameName();
            String authToken = ctx.header("Authorization");
            var gameId = createGameService.createGame(authToken, gameName);
            var res = new GameResponse(gameId);
            ctx.status(200);
            ctx.result(serializer.toJson(res));
        } catch (UnauthorizedException e) {
            ctx.status(401);
            String errorMessage = "{\"message\": \"" + e.getMessage() + "\"}";
            ctx.result(errorMessage);
        } catch (BadRequestException e) {
            ctx.status(400);
            String errorMessage = "{\"message\": \"" + e.getMessage() + "\"}";
            ctx.result(errorMessage);
        }
    }

    private void listGamesHandler(Context ctx) {
        var serializer = new Gson();
        try {
            String authToken = ctx.header("Authorization");
            GameListResponse res = listGamesService.listGames(authToken);
            ctx.status(200);
            ctx.result(serializer.toJson(res));
        } catch (UnauthorizedException e) {
            ctx.status(401);
            String errorMessage = "{\"message\": \"" + e.getMessage() + "\"}";
            ctx.result(errorMessage);
        }
    }

    private void joinGameHandler(Context ctx) {
        var serializer = new Gson();
        try {
            String reqJson = ctx.body();
            var req = serializer.fromJson(reqJson, JoinRequest.class);
            int gameID = req.gameID();
            TeamColor playerColor = req.playerColor();
            String authToken = ctx.header("Authorization");
            var res = joinService.join(authToken, gameID, playerColor);
            ctx.status(200);
            ctx.result(res);
        } catch (UnauthorizedException e) {
            ctx.status(401);
            String errorMessage = "{\"message\": \"" + e.getMessage() + "\"}";
            ctx.result(errorMessage);
        } catch (BadRequestException e) {
            ctx.status(400);
            String errorMessage = "{\"message\": \"" + e.getMessage() + "\"}";
            ctx.result(errorMessage);
        } catch (TakenException e) {
            ctx.status(403);
            String errorMessage = "{\"message\": \"" + e.getMessage() + "\"}";
            ctx.result(errorMessage);
        }
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
