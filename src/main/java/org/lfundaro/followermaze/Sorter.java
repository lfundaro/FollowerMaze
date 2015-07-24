package org.lfundaro.followermaze;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lfundaro.followermaze.events.Event;

/**
 * Sorts events and enqueues them so the Sender sends them to clients in order.
 * @author Lorenzo
 */
public class Sorter implements Runnable {

    private LinkedList<Event> msgBuffer;
    private int currentSeq;
    private BlockingQueue<Event> eventQueue;
    private BlockingQueue<Event> readyForDelivery;
    private static final Logger logger = Logger.getLogger(Sorter.class.getName());

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
                    logger.log(Level.INFO, "Found {0} messages to flush", String.valueOf(n));
                    while (n > 0) {
                        Event e = (Event) msgBuffer.removeFirst();
                        while(!readyForDelivery.offer(e)){}
                        n--;
                    }
                }
            }
            } catch (InterruptedException ex) {
                logger.severe(ex.getMessage());
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
            logger.finest("Message Buffer was empty. Inserting new message");
            msgBuffer.add(msg);
        } else {
            for (int i = 0; i < msgBuffer.size(); i++) {
                if (msg.getSeq() < msgBuffer.get(i).getSeq()) {
                    msgBuffer.add(i, msg);
                    logger.finest("Message inserted");
                    return;
                } else if (msg.getSeq() > msgBuffer.get(i).getSeq()) {
                    continue;
                }
            }
            logger.finest("Message id is the greatest from all");
            //in case msg seq is greater than any other msg in buffer
            msgBuffer.addLast(msg);
        }
    }

    public LinkedList<Event> getMsgBuffer() {
        return msgBuffer;
    }

    public void setMsgBuffer(LinkedList<Event> msgBuffer) {
        this.msgBuffer = msgBuffer;
    }

    public int getCurrentSeq() {
        return currentSeq;
    }

    public void setCurrentSeq(int currentSeq) {
        this.currentSeq = currentSeq;
    }

    public BlockingQueue<Event> getEventQueue() {
        return eventQueue;
    }

    public void setEventQueue(BlockingQueue<Event> eventQueue) {
        this.eventQueue = eventQueue;
    }

    public BlockingQueue<Event> getReadyForDelivery() {
        return readyForDelivery;
    }

    public void setReadyForDelivery(BlockingQueue<Event> readyForDelivery) {
        this.readyForDelivery = readyForDelivery;
    }
}
