package com.example.demo;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.example.network.TransactionSender;
import com.example.objects.User;
import com.example.usersdb.GroupsDB;
import com.example.usersdb.UsersDB;

public class CLIHandler {
    private UsersDB usersDB = UsersDB.getInstanceOf();
    private GroupsDB groupsDB = GroupsDB.getInstanceOf();

    private Integer myPort = 301;
    private Integer registrationCounter = 0;

    private Scanner scanner = new Scanner(System.in);
    private Scanner scannerString = new Scanner(System.in);
    private Scanner scannerMultiple = new Scanner(System.in);

    List<User> users = usersDB.getConnectedUsersList();
    private TransactionSender tSender = new TransactionSender();

    Logger logger = Logger.getLogger(CLIHandler.class.getName());

    public void registerNewUser() throws UnknownHostException, IOException {
        if (this.registrationCounter == 0) {
            System.out.println("What is the prefered username?");
            String userName = scanner.nextLine();

            User newUser = new User(userName, myPort);
            Integer rc = usersDB.addUserToDB(newUser);

            JSONObject jsonObjectToSend = new JSONObject();
            jsonObjectToSend.put("kind", "registration");
            jsonObjectToSend.put("username", userName);
            jsonObjectToSend.put("port", myPort);

            tSender.sendRegistration(jsonObjectToSend, myPort);

            if (rc == 1) {
                logger.info("New User added to the db with name: " + userName);
                registrationCounter++;

            } else if (rc == 0)
                logger.info("User already registered");
        } else
            logger.info("You cant register more then once!!!");
    }

    public void getAllConnectedUsers() {
        this.users = usersDB.getConnectedUsersList();

        for (User eachUser : users) {
            System.out.println(eachUser.getUsername());
        }
    }

    public void sendMessageHandler() throws UnknownHostException, IOException {
        Integer positionCounter = 0;
        Integer userInput;
        String messageToSend;
        Map<Integer, String> myMap = new HashMap<>();

        System.out.println("To whom do you want to send a message?");
        for (User eachUser : users) {
            if (eachUser.getPort() != myPort) {
                positionCounter++;
                myMap.put(positionCounter, eachUser.getUsername());
                System.out.println(positionCounter + ". " + eachUser.getUsername());
            }
        }
        System.out.println("0. Cancel");
        System.out.println("Choose the User with the corresponding number!");
        userInput = scanner.nextInt();

        if(userInput == 0) {
            System.out.println("Canceling....");
        } else{
            System.out.println("Message: ");
            messageToSend = scannerString.nextLine();
            Integer sendTo = findThePort(myMap, userInput);

            JSONObject jsonObjectToSend = new JSONObject();
            jsonObjectToSend.put("kind", "message");
            jsonObjectToSend.put("sender", myPort);
            jsonObjectToSend.put("message", messageToSend);

            tSender.sendMessage(jsonObjectToSend, sendTo);
        }
    }

    public void sendMessageToMoreUsers() throws UnknownHostException, IOException {
        Integer positionCounter = 0;
        Integer userInput = -1;
        String messageToSend;
        Integer addMoreUsers = -1;
        List<Integer> portListToSend = new ArrayList<>();

        Boolean cancel = false;

        Map<Integer, String> myMap = new HashMap<>();

        Integer sendTo = 0;
        while (addMoreUsers != 0) {
            positionCounter = 0;
            System.out.println("To whom do you want to send a message?");

            for (User eachUser : users) {
                if (eachUser.getPort() != myPort) {
                    positionCounter++;
                    myMap.put(positionCounter, eachUser.getUsername());
                    System.out.println(positionCounter + ". " + eachUser.getUsername());
                }
            }
            System.out.println("0. Cancel");
            System.out.println("Choose the User with the corresponding number!");
            userInput = scanner.nextInt();
            if(userInput == 0){
                cancel = true;
                break;
            } 

            sendTo = findThePort(myMap, userInput);
            if (portListToSend.contains(sendTo)) {
                System.out.println("Already added");
            } else {
                portListToSend.add(sendTo);
            }
            System.out.println("Do you want to add more Users?(1/0)");
            addMoreUsers = scannerMultiple.nextInt();
        }

        if(!cancel) {
            System.out.println("Message: ");
            messageToSend = scannerString.nextLine();

            JSONObject jsonObjectToSend = new JSONObject();
            jsonObjectToSend.put("kind", "message");
            jsonObjectToSend.put("sender", myPort);
            jsonObjectToSend.put("message", messageToSend);

            tSender.sendMessageToUsers(myPort, jsonObjectToSend, portListToSend);
        }
        else System.out.println("Canceling...");
    }

    public void sendMessageToAllUsers() throws UnknownHostException, IOException {
        String messageToSend;
        List<Integer> portListToSend = new ArrayList<>();

        for (User eachUser : usersDB.getConnectedUsersList()) {
            portListToSend.add(eachUser.getPort());
        }

        System.out.println("Message: ");
        messageToSend = scannerString.nextLine();

        JSONObject jsonObjectToSend = new JSONObject();
        jsonObjectToSend.put("kind", "message");
        jsonObjectToSend.put("sender", myPort);
        jsonObjectToSend.put("message", messageToSend);

        tSender.sendMessageToUsers(myPort, jsonObjectToSend, portListToSend);
    }

    public void createGroup() throws UnknownHostException, IOException {
        Integer positionCounter = 0;
        Integer userInput = -1;
        Integer addMoreUsers = -1;
        List<User> usersToAddToGroup = new ArrayList<>();
        List<Integer> portListToSend = new ArrayList<>();

        boolean cancel = false;

        Map<Integer, String> myMap = new HashMap<>();

        portListToSend.add(myPort);
        String groupName = "";

        while (addMoreUsers != 0) {
            positionCounter = 0;
            System.out.println("With whom do you want to create a group?");

            for (User eachUser : users) {
                if (eachUser.getPort() != myPort) {
                    positionCounter++;
                    myMap.put(positionCounter, eachUser.getUsername());
                    System.out.println(positionCounter + ". " + eachUser.getUsername());
                }
            }
            System.out.println("0. Cancel");
            System.out.println("Choose the User with the corresponding number!");
            userInput = scanner.nextInt();

            if(userInput == 0) {
                cancel = true;
                break;
            }
            User choosenUser = usersDB.getSingleUserWithPortr(findThePort(myMap, userInput));

            if (usersToAddToGroup.contains(choosenUser)) {
                System.out.println("Already added");
            } else {
                usersToAddToGroup.add(choosenUser);
                portListToSend.add(choosenUser.getPort());
            }
            System.out.println("Do you want to add more Users?(1/0)");
            addMoreUsers = scannerMultiple.nextInt();
        }

        if(!cancel) {
            System.out.print("Name the group with: ");
            for (User eachUser : usersToAddToGroup) {
                System.out.print(eachUser.getUsername() + " ");
            }
            groupName = scannerString.nextLine();

            groupsDB.addNewGroup(groupName, usersToAddToGroup);

            JSONObject jsonObjectToSend = new JSONObject();
            jsonObjectToSend.put("kind", "group");
            jsonObjectToSend.put("name", groupName);
            jsonObjectToSend.put("group", portListToSend);

            tSender.sendMessageToUsers(myPort, jsonObjectToSend, portListToSend);

        }
        else System.out.println("Canceling...");

    }

    public void sendMessageToGroup() throws UnknownHostException, IOException {
        Integer positionCounter = 0;
        Integer userInput;
        String messageToSend;
        List<Integer> portListToSend = new ArrayList<>();

        System.out.println("To which group do you want to send a message?");
        for (String eachUserName : groupsDB.getGroupNames()) {
            positionCounter++;
            System.out.println(positionCounter + ". " + eachUserName);
        }

        System.out.println("0. Cancel");
        System.out.println("Choose the Group with the corresponding number!");
        userInput = scanner.nextInt();

        if(userInput == 0) {
            System.out.println("Canceling...");
        }
        else{

            System.out.println("Message: ");
            messageToSend = scannerString.nextLine();

            String groupName = groupsDB.getGroupNames().get(userInput - 1);
            portListToSend = groupsDB.returnUserPortsForGroupName(groupName);

            JSONObject jsonObjectToSend = new JSONObject();
            jsonObjectToSend.put("kind", "groupMessage");
            jsonObjectToSend.put("name", groupName);
            jsonObjectToSend.put("sender", myPort);
            jsonObjectToSend.put("message", messageToSend);

            tSender.sendMessageToUsers(myPort, jsonObjectToSend, portListToSend);
        }
    }

    private Integer findThePort(Map<Integer, String> myMap, Integer userInput) {
        String corespondingUser = "";
        for (Map.Entry<Integer, String> entry : myMap.entrySet()) {
            if (entry.getKey().equals(userInput))
                corespondingUser = entry.getValue();
        }
        return usersDB.getPortFromName(corespondingUser);
    }

    public void leaveGroup() throws UnknownHostException, IOException {
        Integer positionCounter = 0;
        Integer userInput;
        List<Integer> portListToSend = new ArrayList<>();

        System.out.println("Witch group do you want to leave?");
        for (String eachUserName : groupsDB.getGroupNames()) {
            positionCounter++;
            System.out.println(positionCounter + ". " + eachUserName);
        }
        System.out.println("0. Cancel");
        System.out.println("Choose the Group with the corresponding number!");
        userInput = scanner.nextInt();

        if(userInput == 0) {
            System.out.println("Canceling...");
        }
        else {
            String groupName = groupsDB.getGroupNames().get(userInput - 1);

            //groupsDB.leaveGroup(usersDB.getSingleUserWithPortr(myPort), groupName);

    
            portListToSend = groupsDB.returnUserPortsForGroupName(groupName);

            JSONObject jsonObjectToSend = new JSONObject();
            jsonObjectToSend.put("kind", "leaveGroup");
            jsonObjectToSend.put("name", groupName);
            jsonObjectToSend.put("sender", myPort);

            groupsDB.deleteGroup(groupName);

            tSender.sendMessageToUsers(myPort, jsonObjectToSend, portListToSend);
        }
    }
}
