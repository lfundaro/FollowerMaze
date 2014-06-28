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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lfundaro.followermaze.events.Event;
import org.lfundaro.followermaze.events.EventFactory;
import org.lfundaro.followermaze.events.MalformedEventException;

/**
 *
 * @author Lorenzo
 */
public class EventListener extends Thread {

    private ServerSocket serverSocket;
    private Socket clientSocket;

    public EventListener(ServerSocket socket) {
        this.serverSocket = socket;
    }

    @Override
    public void run() {
        BufferedReader br = null;
        String read;
        try { //TODO refactor to try-with-resources
            while (true) {
                clientSocket = serverSocket.accept();
                br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                while ((read = br.readLine()) != null) {
                    Event event = EventFactory.buildEvent(read);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedEventException ex) {
            Logger.getLogger(EventListener.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
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
}
