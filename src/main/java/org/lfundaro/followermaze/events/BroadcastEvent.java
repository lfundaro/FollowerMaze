/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lfundaro.followermaze.events;

/**
 *
 * @author Lorenzo
 */
public class BroadcastEvent extends Event {

    public BroadcastEvent(long seq, ActionType action) {
        super(seq, action);
    }

}
