package com.binhcodev.broadcast_server.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.binhcodev.broadcast_server.configs.WebSocketMessageHandler;

import lombok.AllArgsConstructor;
@RestController
@AllArgsConstructor
public class WebSocketController {
    private final WebSocketMessageHandler   messageHandler;
    
    @GetMapping("/connected-clients")
    public List<String> getConnectedClients() {
        return messageHandler.getConnectedClients();
    }
}
