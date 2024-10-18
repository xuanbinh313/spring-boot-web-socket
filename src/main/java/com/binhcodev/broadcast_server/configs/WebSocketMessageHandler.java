package com.binhcodev.broadcast_server.configs;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

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

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("Received message: " + message.getPayload());
        for (WebSocketSession webSocketSession : sessions) {
            if (webSocketSession.isOpen()) {
                webSocketSession.sendMessage(message);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("Client disconnected" + session.getId());
        sessions.remove(session);
    }

    public List<String> getConnectedClients() {
        return sessions.stream().map(WebSocketSession::getId).toList();
    }
}
