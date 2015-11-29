package net.newbiehacker.commodorej.event;

import net.newbiehacker.commodorej.Session;

/**
 * This class represents a disconnect event, which is fired when the bot is forcefully disconnected from the server with no reason (usually a socket error)
 * @author newbiehacker
 */
public class IRCDisconnectEvent extends IRCEvent {
    public IRCDisconnectEvent(Session source) {
        super(source, Type.DISCONNECT);
    }

    public String toString() {
        return "Disconnected from " + source.getParameter("NETWORK");
    }
}