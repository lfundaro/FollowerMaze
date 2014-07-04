/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lfundaro.followermaze;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Lorenzo
 */
public class ClientListener implements Runnable {

    private ServerSocket serverSocket;
    private LinkedList<Socket> clientSocketList;
    private BlockingQueue<Client> clients;
    private static final int NTHREADS = 100;
    private static final Executor exec = Executors.newFixedThreadPool(NTHREADS);

    public ClientListener(int serverPort, BlockingQueue<Client> clients) throws IOException {
        this.serverSocket = new ServerSocket(serverPort);
        this.clientSocketList = new LinkedList<Socket>();
        this.clients = clients;
    }

    @Override
    public void run() {
        try { //TODO refactor to try-with-resources
            while (true) {
                //TODO we need to use an executorService here so we can escalate.
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
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NumberFormatException ex) {
            Logger.getLogger(EventListener.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (clientSocketList != null) {
                closeSockets(clientSocketList);
            }
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException ex) {
                    Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void handleRequest(Socket clientSocket) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            long id = Long.valueOf(br.readLine());
            clients.add(new Client(id, clientSocket));
        } catch (IOException ex) {
            Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void closeSockets(List<Socket> clientSockets) {
        for (Socket s : clientSockets) {
            try {
                s.close();
            } catch (IOException ex) {
                Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
