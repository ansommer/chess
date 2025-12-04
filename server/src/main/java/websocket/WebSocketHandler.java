package websocket;

import com.google.gson.Gson;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.eclipse.jetty.websocket.api.Session;


public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("websocket Connected");
        ctx.enableAutomaticPings();
    }

    public void handleMessage(WsMessageContext ctx) {
        System.out.print(ctx.message());
    }


    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("websocket Closed");
    }
}
