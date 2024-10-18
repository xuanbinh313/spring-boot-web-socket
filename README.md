# Web Socket Broadcast server

## Reference doc from [roadmap.sh](https://roadmap.sh/projects/broadcast-server)

## Install

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
```

## Handle Message Web Socket
**Goal**
- Handle when a new client connects
- Handle new message is received
- Handle new message is disconnected
- Get all connected clients

```java
package com.binhcodev.broadcast_server.configs;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class WebSocketMessageHandler extends TextWebSocketHandler {
    // Store active sessions
    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    // Called when a new client connects
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("New client connected");
        sessions.add(session);
    }

    // Called when a new message is received
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("Received message: " + message.getPayload());
        for (WebSocketSession webSocketSession : sessions) {
            if (webSocketSession.isOpen()) {
                webSocketSession.sendMessage(message);
            }
        }
    }

    // Called when a client disconnects
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("Client disconnected" + session.getId());
        sessions.remove(session);
    }

    // Get connected clients
    public List<String> getConnectedClients() {
        return sessions.stream().map(WebSocketSession::getId).toList();
    }
}
```
## Configuration enable WebSocket support and map endpoints.
- Handle Register Web Socket handler
- Enable Cors for client request

`WebSocketConfig.java`

```java
package com.binhcodev.broadcast_server.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import lombok.AllArgsConstructor;

// Create a configuration class to enable WebSocket support and map endpoints.
@Configuration
@EnableWebSocket
@AllArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer, WebMvcConfigurer {

    private final WebSocketMessageHandler messageHandler;

    // Register the WebSocket handler
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(messageHandler, "/broadcast").setAllowedOrigins("*");
    }

    // Enable CORS
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*");
    }
    
}
```
## Create Rest Controller for client get list connected

```java
package com.binhcodev.broadcast_server.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.binhcodev.broadcast_server.configs.WebSocketMessageHandler;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class WebSocketController {
    private final WebSocketMessageHandler messageHandler;

    // Get connected clients
    @GetMapping("/connected-clients")
    public List<String> getConnectedClients() {
        return messageHandler.getConnectedClients();
    }
}
```
