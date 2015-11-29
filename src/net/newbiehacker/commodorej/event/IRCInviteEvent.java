package net.newbiehacker.commodorej.event;

import net.newbiehacker.commodorej.Session;
import net.newbiehacker.commodorej.Node;
import net.newbiehacker.commodorej.User;

/**
 * This class represents a invite event which is fired when a node invites a user to a channel
 * @author newbiehacker
 */
public final class IRCInviteEvent extends MessagePrototype {
    public IRCInviteEvent(Session source, Node sender, User target, String channel) {
        super(source, Type.INVITE, sender, target, channel);
    }

    /**
     * Returns the node which sent the invite
     * @return the node which send the invite
     */
    public Node getSender() {
        return sender;
    }

    /**
     * Returns the user that received the invite
     * @return the user that received the invite
     */
    public User getTarget() {
        return (User) target;
    }

    /**
     * Returns the name of the channel that the target was invited to
     * @return the name of the channel that the target was invited to
     */
    public String getChannel() {
        return message;
    }

    public String toString() {
        return sender + " invites " + target + " to " + message;
    }
}