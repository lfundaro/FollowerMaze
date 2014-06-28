/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lfundaro.followermaze;

import java.net.Socket;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import org.lfundaro.followermaze.events.Event;

/**
 *
 * @author Lorenzo
 */
public class TaskDispatcher extends Thread implements ObserverDispatcher {
    
    private Map<Long,Client> clients;
    private Queue<Event> tasks;
    private long currentSeq;

    public TaskDispatcher(Map<Long, Client> clients, Queue<Event> tasks) {
        this.clients = clients;
        this.tasks = tasks;
        this.currentSeq = 0;
    }
    
    @Override
    public void run() {
        
    }

    public void pushNewClient(long clientId, Socket clientSocket) {
        if (!clients.containsKey(clientId)) {
            clients.put(clientId, new Client(clientId, clientSocket, new LinkedList<Long>()));
        } 
    }

    public void pushNewEvent(Event e) {
        tasks.add(e);
        currentSeq++;
    }
    
}
