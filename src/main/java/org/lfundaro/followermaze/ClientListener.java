package org.lfundaro.followermaze;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Listens for new clients and enqueues them so the Sender can 
 * consume them.
 * @author Lorenzo
 */
public class ClientListener implements Runnable {

    private ServerSocket serverSocket;
    private LinkedList<Socket> clientSocketList;
    private BlockingQueue<Client> clients;
    private static final Executor exec = Executors.newCachedThreadPool();
    private static final Logger logger = Logger.getLogger(ClientListener.class.getName());

    public ClientListener(int serverPort, BlockingQueue<Client> clients) throws IOException {
        this.serverSocket = new ServerSocket(serverPort);
        this.clientSocketList = new LinkedList<Socket>();
        this.clients = clients;
    }

    @Override
    public void run() {
        try { 
            while (true) {
                logger.finest("Entering accept state");
                final Socket clientSocket = serverSocket.accept();
                clientSocketList.add(clientSocket);
                Runnable task = new Runnable() {
                    public void run() {
                        handleRequest(clientSocket);
                    }
                };
                exec.execute(task);
            }
        } catch (IOException ex) {
            logger.severe(ex.getMessage());
        } catch (NumberFormatException ex) {
            logger.severe(ex.getMessage());
        } finally {
            if (clientSocketList != null) {
                closeClientSockets();
            }
            if (serverSocket != null) {
                try {
                    logger.finest("Closing server socket for client notifications");
                    serverSocket.close();
                } catch (IOException ex) {
                    logger.severe(ex.getMessage());
                }
            }
        }
    }

    private void handleRequest(Socket clientSocket) {
        try {
            logger.finest("Handling request");
            BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            long id = Long.valueOf(br.readLine());
            Client client = new Client(id, clientSocket); 
            while(!clients.offer(client)){}
            logger.log(Level.FINEST, "Registered new client with id: {0}", String.valueOf(id));
        } catch (IOException ex) {
            logger.severe(ex.getMessage());
        }
    }
    
    public void cleanUp() {
        closeServerSocket();
        closeClientSockets();
    }

    private void closeServerSocket() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException ex) {
                logger.severe(ex.getMessage());
            }
        }
    }

    private void closeClientSockets() {
        logger.info("Closing client notification sockets");
        for (Socket s : clientSocketList) {
            try {
                if (s != null) {
                    s.close();
                }
            } catch (IOException ex) {
                logger.severe(ex.getMessage());
            }
        }
    }
}
