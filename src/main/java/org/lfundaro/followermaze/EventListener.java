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
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lfundaro.followermaze.events.Event;
import org.lfundaro.followermaze.events.EventFactory;
import org.lfundaro.followermaze.events.MalformedEventException;

/**
 *
 * @author Lorenzo
 */
public class EventListener implements Runnable {

    private ServerSocket serverSocket;
    private BlockingQueue<Event> eventQueue;

    public EventListener(int serverPort, BlockingQueue<Event> queue) throws IOException {
        this.serverSocket = new ServerSocket(serverPort);
        this.eventQueue = queue;
    }

    @Override
    public void run() {
        BufferedReader br = null;
        Socket clientSocket = null;
        String read;
        try { //TODO refactor to try-with-resources
            clientSocket = serverSocket.accept();
            br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            while ((read = br.readLine()) != null) {
                Event event = EventFactory.buildEvent(read);
                eventQueue.add(event);  // TODO change this to offer method
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
