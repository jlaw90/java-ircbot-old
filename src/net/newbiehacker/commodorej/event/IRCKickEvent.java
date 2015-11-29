package net.newbiehacker.commodorej.event;

import net.newbiehacker.commodorej.Channel;
import net.newbiehacker.commodorej.Session;
import net.newbiehacker.commodorej.Node;
import net.newbiehacker.commodorej.User;

/**
 * @author newbiehacker
 */
public final class IRCKickEvent extends MessagePrototype {
    private final Channel channel;

    public IRCKickEvent(Session source, Node kicker, User target, Channel channel, String message) {
        super(source, Type.KICK, kicker, target, message);
        this.channel = channel;
    }

    /**
     * Returns the node which kicked the target from the channel
     * @return the node which kicked the target from the channel
     */
    public Node getKicker() {
        return sender;
    }

    /**
     * Returns the user which was kicked from the channel
     * @return the user which was kicked from the channel
     */
    public User getTarget() {
        return (User) target;
    }

    /**
     * Returns the channel that the user was kicked from
     * @return the channel that the user was kicked from
     */
    public Channel getChannel() {
        return channel;
    }

    /**
     * Returns the message explaining why the user was kicked
     * @return the message explaining why the user was kicked
     */
    public String getMessage() {
        return message;
    }

    public String toString() {
        return sender + " has kicked " + target + " from " + channel + " (" + message + ")";
    }
}