package net.newbiehacker.commodorej.event;

/**
 * An ErrorHandler is an interface to be able to interpret errors thrown from the library and display them in a custom way
 * @author newbiehacker
 */
public interface ErrorHandler {
    /**
     * Called when the lib receives an error that it can't handle
     * @param t a Throwable representing the error hat the lib couldn't handle
     */
    void onError(Throwable t);
}