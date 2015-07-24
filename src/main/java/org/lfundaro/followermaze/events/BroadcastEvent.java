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
