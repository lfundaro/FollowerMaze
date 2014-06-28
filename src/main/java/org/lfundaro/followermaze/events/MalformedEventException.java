/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lfundaro.followermaze.events;

/**
 *
 * @author Lorenzo
 */
public class MalformedEventException extends Exception {

    public MalformedEventException() {
    }
    
    public MalformedEventException(String eventString) {
        super("Malformed event string: "+eventString);
    }
    
    
    
    
    
}
