/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lfundaro.followermaze.events;

/**
 *
 * @author Lorenzo
 */
public class PrivateMessageEvent extends BaseEvent implements Event {

    private long fromUser;
    private long toUser;

    public PrivateMessageEvent(long fromUser, long toUser, long seq, ActionType action) {
        super(seq, action);
        this.fromUser = fromUser;
        this.toUser = toUser;
    }

    public long getFromUser() {
        return fromUser;
    }

    public void setFromUser(long fromUser) {
        this.fromUser = fromUser;
    }

    public long getToUser() {
        return toUser;
    }

    public void setToUser(long toUser) {
        this.toUser = toUser;
    }

    @Override
    public String toString() {
        return super.toString() + "|" + fromUser + "|" + toUser;
    }
}
