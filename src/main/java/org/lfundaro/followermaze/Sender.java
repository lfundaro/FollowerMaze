/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
 *
 * @author Lorenzo
 */
public class Sender implements Runnable {

    private BlockingQueue<Event> readyForDelivery;
    private BlockingQueue<Client> clientQueue;
    private HashMap<Long, Client> clients;

    public Sender(BlockingQueue<Event> readyForDelivery, BlockingQueue<Client> clientQueue) {
        this.readyForDelivery = readyForDelivery;
        this.clientQueue = clientQueue;
        this.clients = new HashMap<Long, Client>();
    }

    public void run() {
        while (true) {
            try {
                updateClients();
                Event event = readyForDelivery.poll(500, TimeUnit.MILLISECONDS);
                if (event != null) {
                    processEvent(event);
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
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
        if (clients.containsKey(followEvent.getToUser())) {
            Client client = clients.get(followEvent.getToUser());
            client.getFollowers().add(followEvent.getFromUser());
        } else {
            LinkedList<Long> followers = new LinkedList();
            followers.add(followEvent.getFromUser());
            clients.put(followEvent.getToUser(), new Client(followEvent.getToUser(), followers));
        }
    }

    private void handleUnfollow(UnfollowEvent unfollowEvent) {
        removeFollower(unfollowEvent);
    }

    private void removeFollower(UnfollowEvent event) {
        clients.get(event.getToUser()).getFollowers().remove(event.getFromUser());
    }

    private void handleBroadcast(BroadcastEvent broadcastEvent) {
        for (Client client : clients.values()) {
            notifyClient(broadcastEvent, client);
        }
    }

    private void handlePrivateMsg(PrivateMessageEvent privateMessageEvent) {
        if (clients.containsKey(privateMessageEvent.getToUser())) {
            Client receiver = clients.get(privateMessageEvent.getToUser());
            notifyClient(privateMessageEvent, receiver);
        }
    }

    private void handleStatusUpdate(StatusUpdateEvent statusUpdateEvent) {
        if (clients.containsKey(statusUpdateEvent.getFromUser())) {
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
            } catch (IOException ex) {
                Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void updateClients() {
        if (!clientQueue.isEmpty()) {
            ArrayList<Client> newClients = new ArrayList<Client>();
            clientQueue.drainTo(newClients);
            for (Client c : newClients) {
                if (clients.containsKey(c.getId())) {
                    // If a client has followers but has no
                    // client connected to receive notifications (Socket) 
                    // then its entry must be updated instead 
                    // of replace by a new one.
                    Client oldClient = clients.get(c.getId());
                    c.setFollowers(oldClient.getFollowers());
                    clients.put(c.getId(), c);
                } else {
                    clients.put(c.getId(), c);
                }
            }
        }
    }
}
