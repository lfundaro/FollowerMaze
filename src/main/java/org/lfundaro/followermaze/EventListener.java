package org.lfundaro.followermaze;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;
import org.lfundaro.followermaze.events.Event;
import org.lfundaro.followermaze.events.EventFactory;
import org.lfundaro.followermaze.events.exceptions.MalformedEventException;

/**
 * Listens for events and enqueues them to the Sorter.
 * @author Lorenzo
 */
public class EventListener implements Runnable {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private BlockingQueue<Event> eventQueue;
    private static final Logger logger = Logger.getLogger(EventListener.class.getName());

    public EventListener(int serverPort, BlockingQueue<Event> queue) throws IOException {
        this.serverSocket = new ServerSocket(serverPort);
        this.eventQueue = queue;
    }

    @Override
    public void run() {
        BufferedReader br = null;
        String read;
        try { 
            logger.info("Event listener ready. Waiting for event stream");
            clientSocket = serverSocket.accept();
            br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            logger.info("Listening event stream");
            while ((read = br.readLine()) != null) {
                Event event = EventFactory.buildEvent(read);
                while(!eventQueue.offer(event)){}
            }
        } catch (IOException ex) {
            logger.severe(ex.getMessage());
        } catch (MalformedEventException ex) {
            logger.severe(ex.getMessage());
        } finally {
            try {
                if (clientSocket != null) {
                    logger.finest("Closing client socket");
                    clientSocket.close();
                }
                if (serverSocket != null) {
                    logger.finest("Closing server socket");
                    serverSocket.close();
                }
                if (br != null) {
                    logger.finest("Closing input stream");
                    br.close();
                }
            } catch (IOException ex) {
                logger.severe(ex.getMessage());
            }
        }
    }
    
    public void cleanUp() {
        closeClientSocket();
        closeServerSocket();
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
    
    private void closeClientSocket() {
        if (clientSocket != null) {
            try {
                clientSocket.close();
            } catch (IOException ex) {
                logger.severe(ex.getMessage());
            }
        }
    }
}
