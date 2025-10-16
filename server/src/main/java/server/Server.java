package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import datamodel.User;
import io.javalin.*;
import io.javalin.http.Context;
import service.BadRequestException;
import service.RegisterService;
import service.LoginService;
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
        server.delete("db", ctx -> ctx.result("{}"));
        server.post("user", this::register);
        server.post("session", this::login);
    }

    //we think that this is the handler
    //the service does all the logic
    private void register(Context ctx) {
        var serializer = new Gson();

        try {
            String reqJson = ctx.body();
            var req = serializer.fromJson(reqJson, User.class);
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


    private void login(Context ctx) {
        var serializer = new Gson();

        try {
            String reqJson = ctx.body();
            var req = serializer.fromJson(reqJson, User.class);
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
