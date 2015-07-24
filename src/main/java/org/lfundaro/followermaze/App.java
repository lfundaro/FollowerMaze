package org.lfundaro.followermaze;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;
import org.lfundaro.followermaze.events.Event;
import org.lfundaro.followermaze.utils.PropertiesReader;

/**
 * FollowerMaze application.
 * This is the SoundCloud code challenge.
 * @author Lorenzo Fundaro
 *
 */
public class App {

    private static final Logger logger = Logger.getLogger(App.class.getName());

    public static void main(String[] args) {

        logger.info("Initializing application.");
        Properties prop = PropertiesReader.getProperties();

        if (prop == null) {
            logger.severe("Cannot read property file");
            //TODO maybe instead of returning we should fall back to default settings
            return;
        }

        //initializing structures
        BlockingQueue<Client> clients = new ArrayBlockingQueue<Client>(Integer.valueOf(prop.getProperty("clientsBound")), true);
        BlockingQueue<Event> events = new ArrayBlockingQueue<Event>(Integer.valueOf(prop.getProperty("eventsBound")), true);
        BlockingQueue<Event> readyForDelivery = new ArrayBlockingQueue<Event>(Integer.valueOf(prop.getProperty("deliveryBound")), true);

        try {
            final ClientListener clientListener = new ClientListener(Integer.valueOf(prop.getProperty("clientsNotificationPort")), clients);
            Thread clientListenerThread = new Thread(clientListener);
            clientListenerThread.start();

            final EventListener eventListener = new EventListener(Integer.valueOf(prop.getProperty("eventListenerPort")), events);
            Thread eventListenerThread = new Thread(eventListener);
            eventListenerThread.start();

            final Sorter sorter = new Sorter(events, readyForDelivery);
            Thread sorterThread = new Thread(sorter);
            sorterThread.start();

            final Sender sender = new Sender(readyForDelivery, clients);
            Thread senderThread = new Thread(sender);
            senderThread.start();

            //this hook catches ^C signal and calls cleanUp on each thread
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    logger.info("Cleaning up resources");
                    clientListener.cleanUp();
                    eventListener.cleanUp();
                }
            });

        } catch (IOException ex) {
            logger.severe(ex.getMessage());
        }
    }
}
