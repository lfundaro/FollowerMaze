/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lfundaro.followermaze;

import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.*;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.lfundaro.followermaze.events.BroadcastEvent;
import org.lfundaro.followermaze.events.Event;
import org.lfundaro.followermaze.events.EventFactory;
import org.lfundaro.followermaze.events.FollowEvent;
import org.lfundaro.followermaze.events.PrivateMessageEvent;
import org.lfundaro.followermaze.events.StatusUpdateEvent;
import org.lfundaro.followermaze.events.UnfollowEvent;
import org.lfundaro.followermaze.events.exceptions.MalformedEventException;

/**
 *
 * @author Lorenzo
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EventFactoryTest {

    @Test
    public void buildEventShouldThrowExceptionWhenMalformedEvent() {
        String event = "A";
        try {
            EventFactory.buildEvent(event);
        } catch (Exception e) {
            assertTrue(e instanceof MalformedEventException);
        }
        event = "1:U:12:9";
        try {
            EventFactory.buildEvent(event);
        } catch (Exception e) {
            assertTrue(e instanceof MalformedEventException);
        }
        event = "1|U|12|9.456";
        try {
            EventFactory.buildEvent(event);
        } catch (Exception e) {
            assertTrue(e instanceof MalformedEventException);
        }
        event = "1|Z|12|9";
        try {
            EventFactory.buildEvent(event);
        } catch (Exception e) {
            assertTrue(e instanceof MalformedEventException);
        }
    }

    @Test
    public void buidlEventShouldReturnFollowEventWhenParsingFollowEvent() {
        try {
            String event = "1|F|23|89";
            Event e = EventFactory.buildEvent(event);
            assertTrue(e instanceof FollowEvent);
        } catch (MalformedEventException ex) {
            Logger.getLogger(EventFactoryTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void buildEventShouldReturnUnfollowEventWhenParsingUnfollowEvent() {
        try {
            String event = "1|U|23|89";
            Event e = EventFactory.buildEvent(event);
            assertTrue(e instanceof UnfollowEvent);
        } catch (MalformedEventException ex) {
            Logger.getLogger(EventFactoryTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void buildEventShouldReturnBroadcastEventWhenParsingBroadcastEvent() {
        try {
            String event = "5678|B";
            Event e = EventFactory.buildEvent(event);
            assertTrue(e instanceof BroadcastEvent);
        } catch (MalformedEventException ex) {
            Logger.getLogger(EventFactoryTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void buildEventShouldReturnPrivateMsgEventWhenParsingPrivateMsgEvent() {
        try {
            String event = "1|P|23|89";
            Event e = EventFactory.buildEvent(event);
            assertTrue(e instanceof PrivateMessageEvent);
        } catch (MalformedEventException ex) {
            Logger.getLogger(EventFactoryTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void buildEventShouldReturnStatusUpdateEventWhenParsingStatusUpdateEvent() {
        try {
            String event = "561|S|23";
            Event e = EventFactory.buildEvent(event);
            assertTrue(e instanceof StatusUpdateEvent);
        } catch (MalformedEventException ex) {
            Logger.getLogger(EventFactoryTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
