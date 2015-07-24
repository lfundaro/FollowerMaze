/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lfundaro.followermaze;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.lfundaro.followermaze.events.ActionType;
import org.lfundaro.followermaze.events.FollowEvent;
import org.lfundaro.followermaze.events.UnfollowEvent;

/**
 *
 * @author Lorenzo
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SenderTest {

    private Sender sender;
    private Method addFollowerMethod;
    private Method removeFollowerMethod;
    private Method updateClientsMethod;

    public SenderTest() {
        try {
            addFollowerMethod = Sender.class.getDeclaredMethod("addFollower", FollowEvent.class);
            addFollowerMethod.setAccessible(true);
            removeFollowerMethod = Sender.class.getDeclaredMethod("removeFollower", UnfollowEvent.class);
            removeFollowerMethod.setAccessible(true);
            updateClientsMethod = Sender.class.getDeclaredMethod("updateClients");
            updateClientsMethod.setAccessible(true);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(SenderTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(SenderTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Before
    public void setUp() throws Exception {
        sender = new Sender(null, null);
    }

    @Test
    public void addFollowerToNonExistentClientShouldCreateTheClientWithNewFollower() {
        try {
            FollowEvent event = new FollowEvent(40, 68, 4, ActionType.FOLLOW);
            assertFalse(sender.getClients().containsKey(event.getToUser()));
            addFollowerMethod.invoke(sender, event);
            assertTrue(sender.getClients().containsKey(event.getToUser()));
            assertTrue(sender.getClients().get(event.getToUser()).getFollowers().contains(event.getFromUser()));
        } catch (IllegalAccessException ex) {
            Logger.getLogger(SenderTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(SenderTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(SenderTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void addFollowerToExistentClientShouldAddNewFollower() {
        try {
            //Create the client first
            Client client = new Client(100, new LinkedList<Long>());
            sender.getClients().put(client.getId(), client);
            FollowEvent event = new FollowEvent(40, 100, 4, ActionType.FOLLOW);
            addFollowerMethod.invoke(sender, event);
            assertEquals(event.getFromUser(), sender.getClients().get(event.getToUser()).getFollowers().getFirst().longValue());
        } catch (IllegalAccessException ex) {
            Logger.getLogger(SenderTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(SenderTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(SenderTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void addFollowerToClientWithNoFollowersShouldModifyClientsFollowers() {
        try {
            Client client = new Client(100, new LinkedList<Long>());
            sender.getClients().put(client.getId(), client);
            assertTrue(client.getFollowers().isEmpty());
            FollowEvent event = new FollowEvent(40, 100, 4, ActionType.FOLLOW);
            addFollowerMethod.invoke(sender, event);
            assertFalse(client.getFollowers().isEmpty());
        } catch (IllegalAccessException ex) {
            Logger.getLogger(SenderTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(SenderTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(SenderTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void removeFollowerShouldRemoveFollowerFromClient() {
        try {
            Client client = new Client(100, new LinkedList<Long>());
            client.getFollowers().add(new Long(50));
            client.getFollowers().add(new Long(200));
            sender.getClients().put(client.getId(), client);
            UnfollowEvent unfollow = new UnfollowEvent(50, 100, 3, ActionType.UNFOLLOW);
            removeFollowerMethod.invoke(sender, unfollow);
            assertFalse(client.getFollowers().contains(new Long(50)));
        } catch (IllegalAccessException ex) {
            Logger.getLogger(SenderTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(SenderTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(SenderTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void updateClientsShouldAddNewClientInCaseIDidNotExist() {
        try {
            Client client = new Client(100, new LinkedList<Long>());
            assertFalse(sender.getClients().containsKey(client.getId()));
            sender.setClientQueue(new ArrayBlockingQueue<Client>(100, true));
            sender.getClientQueue().add(client);
            updateClientsMethod.invoke(sender);
            assertTrue(sender.getClients().containsKey(client.getId()));
        } catch (IllegalAccessException ex) {
            Logger.getLogger(SenderTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(SenderTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(SenderTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void updateClientsShouldUpdateClientInCaseItExistedBefore() {
        try {
            LinkedList<Long> followers = new LinkedList<Long>();
            followers.add(new Long(50));
            followers.add(new Long(40));
            Client client = new Client(100, followers);
            HashMap<Long, Client> clients = new HashMap<Long, Client>();
            clients.put(client.getId(), client);
            sender.setClients(clients);
            sender.setClientQueue(new ArrayBlockingQueue<Client>(100, true));
            sender.getClientQueue().add(new Client(100,new Socket()));
            updateClientsMethod.invoke(sender);
            //now assert that the client with id 100 was added with its socket and 
            //its followers were not erased by the duplicate.
            //the correct behavior is a proper merge of both clients with id 100.
            assertTrue(sender.getClients().get(client.getId()).getClientSocket() != null);
            assertFalse(sender.getClients().get(client.getId()).getFollowers().isEmpty());
            assertTrue(sender.getClients().get(client.getId()).getFollowers().contains(new Long(40)));
        } catch (IllegalAccessException ex) {
            Logger.getLogger(SenderTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(SenderTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(SenderTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
