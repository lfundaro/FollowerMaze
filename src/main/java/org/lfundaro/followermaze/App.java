package org.lfundaro.followermaze;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lfundaro.followermaze.events.Event;

/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) {
        BlockingQueue<Client> clients = new ArrayBlockingQueue<Client>(30000, true);
        BlockingQueue<Event> events = new ArrayBlockingQueue<Event>(100000, true);
        BlockingQueue<Event> readyForDelivery = new ArrayBlockingQueue<Event>(1000, true);

        try {
            ClientListener clientListener = new ClientListener(9099, clients);
            Thread clientListenerThread = new Thread(clientListener);
            clientListenerThread.start();
            
            EventListener eventListener = new EventListener(9090, events);
            Thread eventListenerThread = new Thread(eventListener);
            eventListenerThread.start();
            
            Sorter sorter = new Sorter(events, readyForDelivery);
            Thread sorterThread = new Thread(sorter);
            sorterThread.start();
            
            Sender sender = new Sender(readyForDelivery, clients);
            Thread senderThread = new Thread(sender);
            senderThread.start();
            
        } catch (IOException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
