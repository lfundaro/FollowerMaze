package org.lfundaro.followermaze;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.lfundaro.followermaze.events.BroadcastEvent;
import org.lfundaro.followermaze.events.ActionType;
import org.lfundaro.followermaze.events.Event;
import org.lfundaro.followermaze.events.EventFactory;
import org.lfundaro.followermaze.events.MalformedEventException;

/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) {
        try {
            Event event = EventFactory.buildEvent("2345|F|10|9");
            System.out.println(event.toString());
        } catch (MalformedEventException ex) {
            System.out.println(ex.getMessage());
        }

//        //TODO catch control c signal 
//        Thread t1 = new Thread(new Runnable() {
//            public void run() {
//            }
//        });
//        t1.start();
//        Thread t2 = new Thread(new Runnable() {
//            public void run() {
//                Socket event_client_socket = null;
//                ServerSocket server_event_socket = null;
//                try {
//                    event_client_socket = null;
//                    server_event_socket = new ServerSocket(9090);
//                    while (true) {
//                        event_client_socket = server_event_socket.accept();
//                    }
//                } catch (IOException ex) {
//                    Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
//                } finally {
//                    try {
//                        if (event_client_socket != null) {
//                            event_client_socket.close();
//                        }
//                        if (server_event_socket != null) {
//                            server_event_socket.close();
//                        }
//                    } catch (IOException ex) {
//                        Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                }
//
//            }
//        });
//        t2.start();
//
//        try {
//            t1.join();
//            t2.join();
//        } catch (InterruptedException ex) {
//            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
//        }


    }
}
