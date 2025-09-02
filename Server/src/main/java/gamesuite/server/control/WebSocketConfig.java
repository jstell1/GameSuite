
package gamesuite.server.control;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private SocketConnectionHandler handler;

    public WebSocketConfig(SocketConnectionHandler handler) {
        this.handler = handler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        System.out.println("Registering WebSocket handler at /ingame");
        webSocketHandlerRegistry
            .addHandler(this.handler,"/ingame")
            .setAllowedOrigins("*");
    }
    
}