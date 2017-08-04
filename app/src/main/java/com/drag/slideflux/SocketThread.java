package com.drag.slideflux;

import java.io.IOException;
import java.net.Socket;

import static com.drag.slideflux.ConnectionStorage.connection;

public class SocketThread extends Thread {
    @Override
    public void run() {
        try {
            Socket socket = new Socket(ConnectionStorage.SERVER_ADDRESS, ConnectionStorage.SERVER_PORT);
            connection = new Connection(socket);
            clientHandshake();
            clientMainLoop();
        } catch (IOException | ClassNotFoundException e) {
            notifyConnectionStatusChanged(false);
        }
    }

    protected void clientHandshake() throws IOException, ClassNotFoundException {
        while (true) {
            Message response = connection.receive();

            if (response != null && response.getType() == MessageType.NAME_REQUEST) {
                connection.send(new Message(MessageType.USER_NAME, getUserName()));
            } else if (response != null && response.getType() == MessageType.NAME_ACCEPTED) {
                notifyConnectionStatusChanged(true);
                break;
            } else {
                throw new IOException("Unexpected MessageType");
            }
        }
    }

    protected void clientMainLoop() throws IOException, ClassNotFoundException {
        while (true) {
            Message response = connection.receive();

            if (response != null && response.getType() == MessageType.TEXT) {
                processIncomingMessage(response.getData());
            } else if (response != null && response.getType() == MessageType.USER_ADDED) {
                informAboutAddingNewUser(response.getData());
            } else if (response != null && response.getType() == MessageType.USER_REMOVED) {
                informAboutDeletingNewUser(response.getData());
            } else {
                throw new IOException("Unexpected MessageType");
            }
        }
    }
}
