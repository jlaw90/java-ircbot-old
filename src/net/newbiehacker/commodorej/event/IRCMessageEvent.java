package net.newbiehacker.commodorej.event;

import net.newbiehacker.commodorej.Session;
import net.newbiehacker.commodorej.Node;

/**
 * This class represents a message event which is fired when a node sends a message to another node
 * @author newbiehacker
 */
public final class IRCMessageEvent extends MessagePrototype {
    public IRCMessageEvent(Session source, Node sender, Node target, String message) {
        super(source, Type.MESSAGE, sender, target, message);
    }

    /**
     * Returns the node that sent this message
     * @return the node that sent this message
     */
    public Node getSender() {
        return sender;
    }

    /**
     * Returns the node that was the recipient of this message
     * @return the node that was the recipient of this message
     */
    public Node getTarget() {
        return target;
    }

    /**
     * Returns the message
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    public String toString() {
        return "(" + target + ")<" + sender + "> " + message;
    }
}