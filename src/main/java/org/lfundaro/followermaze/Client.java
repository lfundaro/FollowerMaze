/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lfundaro.followermaze;

import java.net.Socket;
import java.util.LinkedList;

/**
 *
 * @author Lorenzo
 */
public class Client {
    
    private long id;
    private Socket clientSocket;
    private LinkedList<Long> followers;

    public Client(long id, Socket clientSocket, LinkedList<Long> followers) {
        this.id = id;
        this.clientSocket = clientSocket;
        this.followers = followers;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public LinkedList<Long> getFollowers() {
        return followers;
    }

    public void setFollowers(LinkedList<Long> followers) {
        this.followers = followers;
    }
}
