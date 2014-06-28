/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lfundaro.followermaze.events;

/**
 *
 * @author Lorenzo
 */
public enum ActionType {
    
    FOLLOW ('F'),
    UNFOLLOW ('U'),
    BROADCAST ('B'),
    PRIVATE_MSG ('P'),
    STATUS_UPDATE ('S');
    
    private final char shortName;

    private ActionType(char shortName) {
        this.shortName = shortName;
    }

    public char getShortName() {
        return shortName;
    }
    
}
