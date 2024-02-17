package com.example.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.objects.User;
import com.example.usersdb.GroupsDB;
import com.example.usersdb.UsersDB;

/**
 * In this class all the messages will be accepted
 * and the kind of message thats gotten will be handeled
 */
public class TransactionServer extends Thread {

    private Socket socket;
    private ServerSocket serverSocket;
    private BufferedReader bufferedReader;

    private UsersDB usersDB = UsersDB.getInstanceOf();
    private GroupsDB groupsDB = GroupsDB.getInstanceOf();

    private static final String TYPE_OF_MESSAGE = "kind";

    Logger logger = Logger.getLogger(TransactionServer.class.getName());

    public TransactionServer(Integer serverPort) {
        try {
            this.serverSocket = new ServerSocket(serverPort);
            logger.info("Socket with port: " + serverPort + " opened.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeEverything(Socket socket, ServerSocket serverSocket, BufferedReader bufferedReader) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }

            if (socket != null) {
                socket.close();
            }

            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String clientMessage;

        try {
            while (true) {
                if (this.serverSocket != (null)) {
                    socket = this.serverSocket.accept();
                    this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    clientMessage = bufferedReader.readLine();
                    JSONObject obj = new JSONObject(clientMessage);

                    String typeOfMessage = obj.get(TYPE_OF_MESSAGE).toString();

                    switch (typeOfMessage) {
                        case "registration":
                            handleRegistration(obj);
                            break;
                        case "message":
                            handleMessage(obj);
                            break;
                        case "group":
                            handleGroups(obj);
                            break;
                        case "groupMessage":
                            handleGroupMessages(obj);
                            break;
                        case "leaveGroup":
                            handleGroupLeave(obj);
                            break;
                        default:
                            System.out.println("wrong message type");
                    }

                }
            }
        } catch (

        IOException e) {
            closeEverything(socket, serverSocket, bufferedReader);
        }
    }

    private void handleGroupLeave(JSONObject obj) {
        String sender = obj.get("sender").toString();
        Integer port = Integer.parseInt(sender);
        String groupName = obj.get("name").toString();

        groupsDB.leaveGroup(usersDB.getSingleUserWithPortr(port), groupName);
    }

    private void handleGroups(JSONObject obj) {
        String groupName = obj.get("name").toString();
        JSONArray groupPorts = obj.getJSONArray("group");
        List<Integer> portsOfGroup = new ArrayList<>();
        List<User> usersList = new ArrayList<>();

        for (Object currentObject : groupPorts) {
            portsOfGroup.add((Integer) currentObject);
        }

        for (Integer eachPort : portsOfGroup) {
            usersList.add(new User(usersDB.getUserNameWithPort(eachPort), eachPort));
        }

        groupsDB.addNewGroup(groupName, usersList);
        System.out.println(getCurrentTime());
        System.out.println("****************************************************************");
        System.out.println("You have been added to a new group with name: " + groupName + "!");

        System.out.print("users in group are: ");
        for (Integer eachPort : portsOfGroup) {
            System.out.print(usersDB.getUserNameWithPort(eachPort) + " ");
        }
        System.out.println();
        System.out.println("****************************************************************");
    }

    private String getCurrentTime() {
        LocalTime time = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String currentTime = time.format(formatter);
        return currentTime;
    }

    private void handleRegistration(JSONObject obj) throws JSONException, UnknownHostException, IOException {
        String port = obj.get("port").toString();
        String userName = obj.get("username").toString();
        Integer newRegisteredPort = Integer.parseInt(port);
        Integer addingValue = usersDB.addUserToDB(new User(userName, newRegisteredPort));

        if (addingValue == 1) {
            logger.info("New user connected with username: " + userName + "!");
        } else
            logger.info("Peer with username: " + userName + " is already connected!");

    }

    private void handleMessage(JSONObject obj) throws JSONException, UnknownHostException, IOException {
        String message = obj.get("message").toString();
        String senderPort = obj.get("sender").toString();
        Integer newRegisteredPort = Integer.parseInt(senderPort);

        String userName = usersDB.getUserNameWithPort(newRegisteredPort);

        String stars = "";

        for (int i = 0; i < message.length() + userName.length() + 2; i++) {
            stars += "*";
        }

        System.out.println(getCurrentTime());
        System.out.println(stars);
        System.out.println(userName + ": " + message);
        System.out.println(stars);

    }

    private void handleGroupMessages(JSONObject obj) {
        String message = obj.get("message").toString();
        String senderPort = obj.get("sender").toString();
        String groupName = obj.get("name").toString();
        Integer newRegisteredPort = Integer.parseInt(senderPort);

        String userName = usersDB.getUserNameWithPort(newRegisteredPort);

        String stars = "";

        for (int i = 0; i < message.length() + userName.length() + groupName.length() + 9; i++) {
            stars += "*";
        }

        logger.info("A new message from User: " + userName + "in the group " + groupName + "!");
        System.out.println(getCurrentTime());
        System.out.println(stars);
        System.out.println("***" + groupName + "*** " + userName + ": " + message);
        System.out.println(stars);
    }

}