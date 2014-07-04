/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lfundaro.followermaze.events;

/**
 *
 * @author Lorenzo
 */
public class StatusUpdateEvent extends Event {

    private long fromUser;

    public StatusUpdateEvent(long fromUser, long seq, ActionType action) {
        super(seq, action);
        this.fromUser = fromUser;
    }

    public long getFromUser() {
        return fromUser;
    }

    public void setFromUser(long fromUser) {
        this.fromUser = fromUser;
    }

    @Override
    public String toString() {
        return super.toString() + "|" + fromUser;
    }
}
