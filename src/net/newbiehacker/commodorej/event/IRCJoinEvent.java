package net.newbiehacker.commodorej.event;

import net.newbiehacker.commodorej.User;
import net.newbiehacker.commodorej.Channel;
import net.newbiehacker.commodorej.Session;

/**
 * This class represents a join event which is fired when a user joins a channel
 * @author newbiehacker
 */
public final class IRCJoinEvent extends MessagePrototype {
    public IRCJoinEvent(Session source, User user, Channel channel) {
        super(source, Type.JOIN, user, channel, null);
    }

    /**
     * Returns the user which joined the channel
     * @return the user which joined the channel
     */
    public User getUser() {
        return (User) sender;
    }

    /**
     * Returns the channel which the user joined
     * @return the channel which the user joined
     */
    public Channel getChannel() {
        return (Channel) target;
    }

    public String toString() {
        return sender + " joins " + target;
    }
}
