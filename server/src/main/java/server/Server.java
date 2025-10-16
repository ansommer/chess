package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import datamodel.User;
import io.javalin.*;
import io.javalin.http.Context;
import service.UserService;

public class Server {

    private final Javalin server;
    private UserService userService;
    private MemoryDataAccess dataAccess;


    public Server() {
        dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);
        server = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        server.delete("db", ctx -> ctx.result("{}"));
        server.post("user", this::register);
    }

    //we think that this is the handler
    //the service does all the logic
    private void register(Context ctx) throws DataAccessException {
        var serializer = new Gson();

        try {
            String reqJson = ctx.body();
            var req = serializer.fromJson(reqJson, User.class);
            var res = userService.register(req);
            ctx.status(200);
            ctx.result(serializer.toJson(res));

        } catch (DataAccessException e) { // Duplicate user
            ctx.status(403);
            //chat was suggesting to make a helper class for the error message, so maybe do that at some point
            String errorMessage = "{ \"error\": \"Forbidden\", \"message\": \"" + e.getMessage() + "\" }";
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
