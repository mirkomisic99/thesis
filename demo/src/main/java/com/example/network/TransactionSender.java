package com.example.network;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import org.json.JSONObject;

/**
 * Class for sending transactions.
 */
public class TransactionSender {

    private Socket socket;
    private BufferedWriter bufferedWriter;

    public void sendRegistration(JSONObject transactionObject, Integer port)
            throws UnknownHostException, IOException {
        try {
            for (int currentPort = 301; currentPort <= 303; currentPort++) {
                if (currentPort != port)
                    sendMessage(transactionObject, currentPort);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeAllConnections(socket, bufferedWriter);
        }

    }

    public void sendMessageToUsers(Integer myPort, JSONObject jsonObjectToSend, List<Integer> portListToSend)
            throws UnknownHostException, IOException {
        try {
            for (Integer eachPort : portListToSend) {
                if (!eachPort.equals(myPort)) {
                    sendMessage(jsonObjectToSend, eachPort);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeAllConnections(socket, bufferedWriter);
        }

    }

    public void sendMessage(JSONObject transactionObject, int currentPort) throws UnknownHostException, IOException {
        this.socket = new Socket("localhost", currentPort);
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        bufferedWriter.write(transactionObject.toString());
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    private void closeAllConnections(Socket socket, BufferedWriter bufferedWriter) {
        try {
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }

            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
