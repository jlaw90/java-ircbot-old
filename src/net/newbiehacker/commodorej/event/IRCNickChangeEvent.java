package net.newbiehacker.commodorej.event;

import net.newbiehacker.commodorej.User;
import net.newbiehacker.commodorej.Session;

/**
 * This class represents a nick change event which is fired when a user changes their nick
 * @author newbiehacker
 */
public final class IRCNickChangeEvent extends IRCEvent {
    private final User user;
    private final String oldNick;

    public IRCNickChangeEvent(Session source, User user, String oldNick) {
        super(source, Type.NICK);
        this.user = user;
        this.oldNick = oldNick;
    }

    /**
     * Returns the user whos nick has changed
     * @return the user whos nick has changed
     */
    public User getUser() {
        return user;
    }

    /**
     * The nick that the user had before this event was fired
     * @return the nick that the user had before this event was fired
     */
    public String getOldNick() {
        return oldNick;
    }

    public String toString() {
        return oldNick + " is now known as " + user;
    }
}