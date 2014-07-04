/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lfundaro.followermaze;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lfundaro.followermaze.events.Event;

/**
 *
 * @author Lorenzo
 */
public class Sorter implements Runnable {

    private LinkedList<Event> msgBuffer;
    private int currentSeq;
    private BlockingQueue<Event> eventQueue;
    private BlockingQueue<Event> readyForDelivery;

    public Sorter(BlockingQueue<Event> eventQueue, BlockingQueue<Event> readyForDelivery) {
        this.msgBuffer = new LinkedList<Event>();
        this.currentSeq = 1;
        this.eventQueue = eventQueue;
        this.readyForDelivery = readyForDelivery;
    }

    public void run() {
        while (true) {
            try {
                Event event = eventQueue.poll(500, TimeUnit.MILLISECONDS);
            if (event == null) {
                continue;
            } else {
                insertMessage(event);
                if (checkForFlush()) {
                    long n = currentSeq - msgBuffer.getFirst().getSeq();
                    while (n > 0) {
                        readyForDelivery.add((Event) msgBuffer.removeFirst());
                        n--;
                    }
                }
            }
            } catch (InterruptedException ex) {
                Logger.getLogger(Sorter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private boolean checkForFlush() {
        int oldSeq = currentSeq;
        for (int i = 0; i < msgBuffer.size(); i++) {
            if (currentSeq == msgBuffer.get(i).getSeq()) {
                currentSeq++;
            } else {
                break;
            }
        }
        return oldSeq != currentSeq;
    }

    private void insertMessage(Event msg) {
        if (msgBuffer.isEmpty()) {
            msgBuffer.add(msg);
        } else {
            for (int i = 0; i < msgBuffer.size(); i++) {
                if (msg.getSeq() < msgBuffer.get(i).getSeq()) {
                    msgBuffer.add(i, msg);
                    return;
                } else if (msg.getSeq() > msgBuffer.get(i).getSeq()) {
                    continue;
                }
            }
            //in case msg seq is greater than any other msg in buffer
            msgBuffer.addLast(msg);
        }
    }
}
