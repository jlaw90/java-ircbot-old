package net.newbiehacker.commodorej.event;

import net.newbiehacker.commodorej.Node;
import net.newbiehacker.commodorej.Session;

/**
 * This class represents a server message event which is fired when the server sends a numeric response to the client
 * @author newbiehacker
 */
public final class IRCServerMessageEvent extends MessagePrototype {
    private final int numeric;

    public IRCServerMessageEvent(Session source, Node server, Node target, int numeric, String message) {
        super(source, Type.SERVER_MESSAGE, server, target, message);
        this.numeric = numeric;
    }

    /**
     * Returns the sender (which will be a MiscNode)
     * @return the sender (which will be a MiscNode)
     */
    public Node getSender() {
        return sender;
    }

    /**
     * Returns the target of this message (which will be us)
     * @return the target of this message (which will be us)
     */
    public Node getTarget() {
        return target;
    }

    /**
     * Returns the numeric of this server response
     * @return the numeric of this server response
     */
    public int getNumeric() {
        return numeric;
    }

    /**
     * Returns the content of the message
     * @return the content of the message
     */
    public String getMessage() {
        return message;
    }

    public String toString() {
        return sender + " " + numeric + " " + target + " " + message;
    }
}