package net.newbiehacker.commodorej.event;

import net.newbiehacker.commodorej.Session;

/**
 * This class represents a connect event, which is fired when the bot is connected to the server and has set it's nick
 * @author newbiehacker
 */
public final class IRCConnectEvent extends IRCEvent{
    public IRCConnectEvent(Session source) {
        super(source, Type.CONNECT);
    }

    public String toString() {
        return "Connected to " + source.getParameter("NETWORK");
    }
}