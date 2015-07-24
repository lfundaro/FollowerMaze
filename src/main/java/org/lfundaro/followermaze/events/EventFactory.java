package org.lfundaro.followermaze.events;

import org.lfundaro.followermaze.events.exceptions.MalformedEventException;

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
    
    private static Event parse(String[] parts) throws MalformedEventException {
        try {
            char actionType = parts[1].toCharArray()[0];
            if (ActionType.FOLLOW.getShortName() == actionType) {
                return new FollowEvent(Long.valueOf(parts[2]), Long.valueOf(parts[3]), Long.valueOf(parts[0]), ActionType.FOLLOW);
            } else if (ActionType.UNFOLLOW.getShortName() == actionType) {
                return new UnfollowEvent(Long.valueOf(parts[2]), Long.valueOf(parts[3]), Long.valueOf(parts[0]), ActionType.UNFOLLOW);
            } else if (ActionType.BROADCAST.getShortName() == actionType) {
                return new BroadcastEvent(Long.valueOf(parts[0]), ActionType.BROADCAST);
            } else if (ActionType.PRIVATE_MSG.getShortName() == actionType) {
                return new PrivateMessageEvent(Long.valueOf(parts[2]), Long.valueOf(parts[3]), Long.valueOf(parts[0]), ActionType.PRIVATE_MSG);
            } else if (ActionType.STATUS_UPDATE.getShortName() == actionType) {
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
