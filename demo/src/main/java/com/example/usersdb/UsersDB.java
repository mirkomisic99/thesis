package com.example.usersdb;

import java.util.LinkedList;
import java.util.List;

import com.example.objects.User;

//Singleton pattern
public class UsersDB {
    private static UsersDB usersDB = new UsersDB();

    private List<User> connectedUsersList = new LinkedList<>();

    private UsersDB() {
    }

    public static UsersDB getInstanceOf() {
        return usersDB;
    }

    public Integer addUserToDB(User user) {
        Integer returnValue = 0;
        if (user != null && !checkUserAlreadyThere(user.getUsername())) {
            this.connectedUsersList.add(user);
            returnValue = 1;
        }
        return returnValue;
    }

    private boolean checkUserAlreadyThere(String username) {
        boolean returnBoolean = false;

        for (User eachUser : connectedUsersList) {
            if (eachUser.getUsername().equals(username))
                returnBoolean = true;
        }
        return returnBoolean;
    }

    public List<User> getConnectedUsersList() {
        return connectedUsersList;
    }

    public void addAllConnections(List<User> wholeList) {
        this.connectedUsersList = wholeList;
    }

    public String getUserNameWithPort(Integer newRegisteredPort) {
        String returnUserName = "User not found!";

        for (User eachUser : connectedUsersList) {
            if (eachUser.getPort() == newRegisteredPort)
                returnUserName = eachUser.getUsername();
        }
        return returnUserName;
    }

    public Integer getPortFromName(String username) {
        Integer portToReturn = 0;

        for (User eachUser : connectedUsersList) {
            if (eachUser.getUsername().equals(username))
                portToReturn = eachUser.getPort();
        }
        return portToReturn;
    }

    public User getSingleUserWithPortr(Integer port) {
        User returnUser = null;
        for (User eachUser : connectedUsersList) {
            if (eachUser.getPort() == port)
                returnUser = eachUser;
        }
        return returnUser;
    }

}
