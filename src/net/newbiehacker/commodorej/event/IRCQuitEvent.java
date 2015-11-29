package net.newbiehacker.commodorej.event;

import net.newbiehacker.commodorej.Session;
import net.newbiehacker.commodorej.User;

/**
 * This class represents a quit event which is fired when a user quits from the server
 * @author newbiehacker
 */
public final class IRCQuitEvent extends MessagePrototype {
    public IRCQuitEvent(Session source, User user, String message) {
        super(source, Type.QUIT, user, null, message);
    }

    /**
     * Returns the user that quit
     * @return the user that quit
     */
    public User getUser() {
        return (User) sender;
    }

    /**
     * Returns the quit message
     * @return the quit message
     */
    public String getMessage() {
        return message;
    }

    public String toString() {
        return sender + " has quit (" + message + ")";
    }
}