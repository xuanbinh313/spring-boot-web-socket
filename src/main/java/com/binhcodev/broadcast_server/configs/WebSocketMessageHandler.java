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
