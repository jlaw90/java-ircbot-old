package net.newbiehacker.commodorej.event;

/**
 * A class implementing this can be added to an IRCConnections listeners so that it can be notified when an event happens
 *
 * @author newbiehacker
 * @see net.newbiehacker.commodorej.ConnectionManager#registerListener(IRCEventListener)
 */
public interface IRCEventListener {
    /**
     * Called whenever an event is fired from the library
     *
     * @param e the IRCEvent that has been fired
     */
    void onIRCEvent(IRCEvent e);
}