/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package main;

/**
 *
 * @author testi
 */
public class EntryException extends Exception {

    public EntryException(Throwable cause) {
        super(cause);
    }

    public EntryException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntryException(String message) {
        super(message);
    }

    public EntryException() {
    }

}
