/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lfundaro.followermaze.events;

/**
 *
 * @author Lorenzo
 */
public class EventFactory {

    public static Event buildEvent(String input) throws MalformedEventException {
        String[] parts = input.split("\\|");
        if (parts.length < 2) {
            throw new MalformedEventException(input);
        }
        try {
            return parse(parts);
        }  catch (MalformedEventException ex) {
            throw new MalformedEventException(input);
        }
    }
    
    private static void printParts(String[] parts) {
        for(int i=0;i<parts.length;i++){
            System.out.print(parts[i]+" ");
        }
        System.out.println();
    }
    
    private static Event parse(String[] parts) throws MalformedEventException {
//        printParts(parts);
        try {
            char actionType = parts[1].toCharArray()[0];
            if (ActionType.FOLLOW.getShortName() == actionType) {
//                System.out.println("FollowType");
                return new FollowEvent(Long.valueOf(parts[2]), Long.valueOf(parts[3]), Long.valueOf(parts[0]), ActionType.FOLLOW);
            } else if (ActionType.UNFOLLOW.getShortName() == actionType) {
//                System.out.println("UnfollowType");
                return new UnfollowEvent(Long.valueOf(parts[2]), Long.valueOf(parts[3]), Long.valueOf(parts[0]), ActionType.UNFOLLOW);
            } else if (ActionType.BROADCAST.getShortName() == actionType) {
//                System.out.println("BroadcastType");
                return new BroadcastEvent(Long.valueOf(parts[0]), ActionType.BROADCAST);
            } else if (ActionType.PRIVATE_MSG.getShortName() == actionType) {
//                System.out.println("PrivateMsgType");
                return new PrivateMessageEvent(Long.valueOf(parts[2]), Long.valueOf(parts[3]), Long.valueOf(parts[0]), ActionType.PRIVATE_MSG);
            } else if (ActionType.STATUS_UPDATE.getShortName() == actionType) {
//                System.out.println("StatusUpdateType");
                return new StatusUpdateEvent(Long.valueOf(parts[2]), Long.valueOf(parts[0]), ActionType.STATUS_UPDATE);
            } else {
                throw new MalformedEventException();
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new MalformedEventException();
        } catch (NumberFormatException ex) {
            throw new MalformedEventException();
        }
    }
}
