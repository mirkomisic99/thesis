package com.example.demo;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Logger;

import com.example.network.TransactionServer;

/**
 * CLI where user can register peers, add books,
 * check a single book in library or print the blockcahin
 */
public class CLI {
    private Integer userAnswer = -1;
    private Scanner scanner = new Scanner(System.in);
    private Integer myPort = 301;
    private TransactionServer tServer = new TransactionServer(myPort);
    private CLIHandler cliHandler = new CLIHandler();

    Logger logger = Logger.getLogger(CLI.class.getName());

    public CLI() {
        tServer.start();
    }

    public void startCLI() throws UnknownHostException, IOException {

        while (userAnswer != 0) {
            System.out.println("**************************************");
            System.out.println("Choose the action you want to execute:");
            System.out.println("1. Register new User to network");
            System.out.println("2. Check all connected Users");
            System.out.println("3. Send a message");
            System.out.println("4. Send message to more users");
            System.out.println("5. Send message to all users");
            System.out.println("6. Create group");
            System.out.println("7. Send message to group");
            System.out.println("8. Leave group");

            System.out.println("To close choose 0");
            System.out.println("**************************************");

            this.userAnswer = scanner.nextInt();

            switch (this.userAnswer) {
                case 1:
                    cliHandler.registerNewUser();
                    break;
                case 2:
                    cliHandler.getAllConnectedUsers();
                    break;
                case 3:
                    cliHandler.sendMessageHandler();
                    break;
                case 4:
                    cliHandler.sendMessageToMoreUsers();
                    break;
                case 5:
                    cliHandler.sendMessageToAllUsers();
                    break;
                case 6:
                    cliHandler.createGroup();
                    break;
                case 7:
                    cliHandler.sendMessageToGroup();
                    break;
                case 8:
                    cliHandler.leaveGroup();
                    break;
                case 0:
                    System.exit(1);
                    break;
                default:
                    System.out.println("Wrong user Input(Try 1-7)");
                    break;
            }
        }

    }

}
