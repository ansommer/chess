package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import datamodel.UserData;
import io.javalin.*;
import io.javalin.http.Context;
import service.*;
import service.UnauthorizedException;

public class Server {

    private final Javalin server;
    private RegisterService registerService;
    private LoginService loginService;
    private MemoryDataAccess dataAccess;


    public Server() {
        dataAccess = new MemoryDataAccess();
        registerService = new RegisterService(dataAccess);
        loginService = new LoginService(dataAccess);
        server = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        server.delete("db", this::clearHandler);
        server.post("user", this::registerHandler);
        server.post("session", this::loginHandler);
    }


    //It's not working, but I think I actually need to make it so it can create a game in order to test if it works...
    private void clearHandler(Context ctx) {
        var serializer = new Gson();
        try {
            //String reqJson = ctx.body();
            //var req = serializer.fromJson(reqJson, User.class);
            //var res = registerService.register(req);
            var clearService = new ClearService(dataAccess);
            var res = clearService.clear();
            //ctx.result(serializer.toJson(res));
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


    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
