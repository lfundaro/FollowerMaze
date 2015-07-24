package org.lfundaro.followermaze;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lfundaro.followermaze.events.BroadcastEvent;
import org.lfundaro.followermaze.events.Event;
import org.lfundaro.followermaze.events.FollowEvent;
import org.lfundaro.followermaze.events.PrivateMessageEvent;
import org.lfundaro.followermaze.events.StatusUpdateEvent;
import org.lfundaro.followermaze.events.UnfollowEvent;

/**
 * Sends notifications to clients.
 * @author Lorenzo
 */
public class Sender implements Runnable {

    private BlockingQueue<Event> readyForDelivery;
    private BlockingQueue<Client> clientQueue;
    private HashMap<Long, Client> clients;
    private static final Logger logger = Logger.getLogger(Sender.class.getName());

    public Sender(BlockingQueue<Event> readyForDelivery, BlockingQueue<Client> clientQueue) {
        this.readyForDelivery = readyForDelivery;
        this.clientQueue = clientQueue;
        this.clients = new HashMap<Long, Client>();
    }

    public void run() {
        while (true) {
            try {
                logger.finest("Update clients");
                updateClients();
                logger.finest("Waiting for events");
                Event event = readyForDelivery.poll(500, TimeUnit.MILLISECONDS);
                if (event != null) {
                    logger.log(Level.FINEST, "Processing event with seq: {0}", String.valueOf(event.getSeq()));
                    processEvent(event);
                }
                logger.finest("Not event found in queue");
            } catch (InterruptedException ex) {
                logger.severe(ex.getMessage());
            }
        }
    }

    private void processEvent(Event event) {
        switch (event.getAction()) {
            case FOLLOW:
                handleFollow((FollowEvent) event);
                break;
            case UNFOLLOW:
                handleUnfollow((UnfollowEvent) event);
                break;
            case BROADCAST:
                handleBroadcast((BroadcastEvent) event);
                break;
            case PRIVATE_MSG:
                handlePrivateMsg((PrivateMessageEvent) event);
                break;
            case STATUS_UPDATE:
                handleStatusUpdate((StatusUpdateEvent) event);
                break;
            default:
                logger.log(Level.INFO, "Event does not match any event type: {0}", event.toString());
                break;
        }
    }

    private void handleFollow(FollowEvent followEvent) {
        addFollower(followEvent);
        //notify 
        Client receiver = clients.get(followEvent.getToUser());
        notifyClient(followEvent, receiver);
    }

    private void addFollower(FollowEvent followEvent) {
        logger.log(Level.INFO, "Client {0} now follows {1}", 
                new Object[]{String.valueOf(followEvent.getFromUser()), String.valueOf(followEvent.getToUser())});
        if (clients.containsKey(followEvent.getToUser())) {
            logger.finest("Client existed before. Updating his followers");
            Client client = clients.get(followEvent.getToUser());
            client.getFollowers().add(followEvent.getFromUser());
        } else {
            logger.finest("Client did not exist. Creating a new one");
            LinkedList<Long> followers = new LinkedList();
            followers.add(followEvent.getFromUser());
            clients.put(followEvent.getToUser(), new Client(followEvent.getToUser(), followers));
        }
    }

    private void handleUnfollow(UnfollowEvent unfollowEvent) {
        removeFollower(unfollowEvent);
    }

    private void removeFollower(UnfollowEvent event) {
        if (clients.containsKey(event.getToUser())) {
            //FIXME this remove is O(n). Could be improved by inserting in order
            //and then doing a binary search
            clients.get(event.getToUser()).getFollowers().remove(event.getFromUser());
            logger.log(Level.INFO, "Remove follower {0} from {1}", 
                    new Object[]{String.valueOf(event.getFromUser()), String.valueOf(event.getToUser())});
        } else {
            logger.finest("Client did not exist. No remove action executed");
        }
    }

    private void handleBroadcast(BroadcastEvent broadcastEvent) {
        logger.log(Level.INFO, "Broadcasting event: {0}", broadcastEvent.toString());
        for (Client client : clients.values()) {
            notifyClient(broadcastEvent, client);
        }
    }

    private void handlePrivateMsg(PrivateMessageEvent privateMessageEvent) {
        if (clients.containsKey(privateMessageEvent.getToUser())) {
            logger.log(Level.INFO, "Client {0} sends private message to {1}", 
                    new Object[]{String.valueOf(privateMessageEvent.getFromUser()), String.valueOf(privateMessageEvent.getToUser())});
            Client receiver = clients.get(privateMessageEvent.getToUser());
            notifyClient(privateMessageEvent, receiver);
        }
    }

    private void handleStatusUpdate(StatusUpdateEvent statusUpdateEvent) {
        if (clients.containsKey(statusUpdateEvent.getFromUser())) {
            logger.log(Level.INFO, "Client {0} updates his status", String.valueOf(statusUpdateEvent.getFromUser()));
            LinkedList<Long> followers = clients.get(statusUpdateEvent.getFromUser()).getFollowers();
            for (Long clientId : followers) {
                if (clients.containsKey(clientId)) {
                    Client receiver = clients.get(clientId);
                    notifyClient(statusUpdateEvent, receiver);
                }
            }
        }
    }

    private void notifyClient(Event event, Client receiver) {
        if (receiver.getClientSocket() != null) {
            Socket so = receiver.getClientSocket();
            try {
                Writer out = new BufferedWriter(new OutputStreamWriter(so.getOutputStream()));
                out.write(event.toString() + "\n");
                out.flush();
                logger.log(Level.INFO, "Client {0} notified", String.valueOf(receiver.getId()));
            } catch (IOException ex) {
                logger.severe(ex.getMessage());
            }
        }
    }

    private void updateClients() {
        if (!clientQueue.isEmpty()) {
            ArrayList<Client> newClients = new ArrayList<Client>();
            clientQueue.drainTo(newClients);
            for (Client c : newClients) {
                logger.log(Level.INFO, "Client {0} reported for notifications", String.valueOf(c.getId()));
                if (clients.containsKey(c.getId())) {
                    logger.finest("Client did exist in clients hashmap");
                    // If a client has followers but has no
                    // client connected to receive notifications (Socket) 
                    // then its entry must be updated instead 
                    // of replace by a new one.
                    Client oldClient = clients.get(c.getId());
                    c.setFollowers(oldClient.getFollowers());
                    clients.put(c.getId(), c);
                } else {
                    logger.finest("Client did not exist in clients hashmap");
                    clients.put(c.getId(), c);
                }
            }
        }
    }

    public BlockingQueue<Event> getReadyForDelivery() {
        return readyForDelivery;
    }

    public void setReadyForDelivery(BlockingQueue<Event> readyForDelivery) {
        this.readyForDelivery = readyForDelivery;
    }

    public BlockingQueue<Client> getClientQueue() {
        return clientQueue;
    }

    public void setClientQueue(BlockingQueue<Client> clientQueue) {
        this.clientQueue = clientQueue;
    }

    public HashMap<Long, Client> getClients() {
        return clients;
    }

    public void setClients(HashMap<Long, Client> clients) {
        this.clients = clients;
    }
}
