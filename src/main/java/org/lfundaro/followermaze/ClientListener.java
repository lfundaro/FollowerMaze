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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Lorenzo
 */
public class ClientListener extends Thread {

    private ServerSocket serverSocket;
    private LinkedList<Socket> clientSockets;

    public ClientListener(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        this.clientSockets = new LinkedList<Socket>();
    }

    @Override
    public void run() {
        BufferedReader br = null;
        String read;
        try { //TODO refactor to try-with-resources
            while (true) {
                //TODO we need to use an executorService here so we can escalate.
                clientSockets.add(serverSocket.accept());
                br = new BufferedReader(new InputStreamReader(clientSockets.getLast().getInputStream()));
                while ((read = br.readLine()) != null) {
                    long seq = Long.valueOf(read);
                }
                br.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NumberFormatException ex) {
            Logger.getLogger(EventListener.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (clientSockets != null) {
                    closeSockets(clientSockets);
                }
                if (serverSocket != null) {
                    serverSocket.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void closeSockets(List<Socket> clientSockets) {
        for(Socket s : clientSockets) {
            try {
                s.close();
            } catch (IOException ex) {
                Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
