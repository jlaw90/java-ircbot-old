package net.newbiehacker.commodorej.event;

import net.newbiehacker.commodorej.Session;
import net.newbiehacker.commodorej.User;
import net.newbiehacker.commodorej.Channel;

/**
 * This class represents a part event which is fired when a user parts a channel
 * @author newbiehacker
 */
public final class IRCPartEvent extends MessagePrototype {
    public IRCPartEvent(Session source, User user, Channel channel, String message) {
        super(source, Type.PART, user, channel, message);
    }

    /**
     * Returns the user that parted the channel
     * @return the user that parted the channel
     */
    public User getUser() {
        return (User) sender;
    }

    /**
     * Returns the channel that the user parted from
     * @return the channel that the user parted from
     */
    public Channel getChannel() {
        return (Channel) target;
    }

    /**
     * Returns the part message
     * @return the part message
     */
    public String getMessage() {
        return message;
    }

    public String toString() {
        return sender + " left " + target + " (" + message + ")";
    }
}