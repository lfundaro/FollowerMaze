/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lfundaro.followermaze;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.lfundaro.followermaze.events.ActionType;
import org.lfundaro.followermaze.events.BroadcastEvent;
import org.lfundaro.followermaze.events.Event;
import org.lfundaro.followermaze.events.FollowEvent;
import org.lfundaro.followermaze.events.PrivateMessageEvent;
import org.lfundaro.followermaze.events.StatusUpdateEvent;
import org.lfundaro.followermaze.events.UnfollowEvent;

/**
 *
 * @author Lorenzo
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SorterTest {

    private Sorter sorter;
    private Method insertMessageMethod;
    private Method checkForFlushMethod;

    public SorterTest() {
        try {
            insertMessageMethod = Sorter.class.getDeclaredMethod("insertMessage", Event.class);
            insertMessageMethod.setAccessible(true);
            checkForFlushMethod = Sorter.class.getDeclaredMethod("checkForFlush");
            checkForFlushMethod.setAccessible(true);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(SorterTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(SorterTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Before
    public void setUp() throws Exception {
        sorter = new Sorter(null, null);
    }

    @Test
    public void checkForFlushShouldIncreaseInternalSequenceCounterWhenTrue() {
        try {
            LinkedList<Event> msgBuffer = sorter.getMsgBuffer();
            msgBuffer.add(new FollowEvent(10, 7, 1, ActionType.FOLLOW));
            int oldSeq = sorter.getCurrentSeq();
            checkForFlushMethod.invoke(sorter);
            assertEquals(oldSeq+1, sorter.getCurrentSeq());
            oldSeq = sorter.getCurrentSeq();
            msgBuffer.poll();
            msgBuffer.add(new BroadcastEvent(2, ActionType.FOLLOW));
            msgBuffer.add(new PrivateMessageEvent(53, 56, 3, ActionType.FOLLOW));
            msgBuffer.add(new UnfollowEvent(56, 90, 4, ActionType.FOLLOW));
            msgBuffer.add(new FollowEvent(78, 90, 20, ActionType.FOLLOW));
            checkForFlushMethod.invoke(sorter);
            System.out.println(sorter.getCurrentSeq());
            assertEquals(oldSeq + 3, sorter.getCurrentSeq());
        } catch (IllegalAccessException ex) {
            Logger.getLogger(SorterTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(SorterTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(SorterTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void insertOnEmptyMessageBufferShouldIncreaseBufferByOne() {
        try {
            insertMessageMethod.invoke(sorter, new FollowEvent(10, 20, 1, ActionType.FOLLOW));
            assertEquals("insertMessage([],msg) should give [msg]",1, sorter.getMsgBuffer().size());
        } catch (IllegalAccessException ex) {
            Logger.getLogger(SorterTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(SorterTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(SorterTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void insertMessageOnNonEmptyListShouldKeepMessageOrdering() {
        try {
            LinkedList<Event> msgBuffer = sorter.getMsgBuffer();
            msgBuffer.add(new FollowEvent(10, 7, 1, ActionType.FOLLOW));
            insertMessageMethod.invoke(sorter, new BroadcastEvent(2, ActionType.FOLLOW));
            assertTrue(msgBuffer.getFirst().getSeq() < msgBuffer.getLast().getSeq());
            insertMessageMethod.invoke(sorter, new StatusUpdateEvent(10, 15, ActionType.STATUS_UPDATE));
            assertEquals(15, msgBuffer.getLast().getSeq());
            insertMessageMethod.invoke(sorter, new StatusUpdateEvent(10, 14, ActionType.STATUS_UPDATE));
            assertEquals(14, msgBuffer.get(msgBuffer.size() - 2).getSeq());
        } catch (IllegalAccessException ex) {
            Logger.getLogger(SorterTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(SorterTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(SorterTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
