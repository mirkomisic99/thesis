package com.example.objects;

public class User {
    private String username;
    private int port;

    public User(String username, int port) {
        this.username = username;
        this.port = port;
    }

    public int getPort() {
        return port;
    }
    public String getUsername() {
        return username;
    }
    public void setPort(int port) {
        this.port = port;
    }
    public void setUsername(String username) {
        this.username = username;
    }
}
