/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lfundaro.followermaze.events;

/**
 *
 * @author Lorenzo
 */
public class Event  {

    protected long seq;
    protected ActionType action;

    public Event(long seq, ActionType action) {
        this.seq = seq;
        this.action = action;
    }

    public long getSeq() {
        return seq;
    }

    public void setSeq(long seq) {
        this.seq = seq;
    }

    public ActionType getAction() {
        return action;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }
    
    @Override
    public String toString() {
        return String.valueOf(seq)+"|"+action.getShortName();
    }
}
