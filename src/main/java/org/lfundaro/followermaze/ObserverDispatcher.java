/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lfundaro.followermaze;

import java.net.Socket;
import org.lfundaro.followermaze.events.Event;

/**
 *
 * @author Lorenzo
 */
public interface ObserverDispatcher {
    
    public void pushNewClient(long clientId, Socket clientSocket);
    public void pushNewEvent(Event e);
    
}
