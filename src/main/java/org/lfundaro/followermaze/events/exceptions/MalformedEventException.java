package org.lfundaro.followermaze.events.exceptions;

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
