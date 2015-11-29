package net.newbiehacker.commodorej.event;

import net.newbiehacker.commodorej.Session;
import net.newbiehacker.commodorej.Node;

/**
 ** This class represents a ctcp request event which is fired when a node sends a ctcp request to another node
 * @author newbiehacker
 */
public final class IRCCtcpRequestEvent extends MessagePrototype {
    public IRCCtcpRequestEvent(Session source, Node sender, Node target, String request) {
        super(source, Type.CTCP_REQUEST, sender, target, request);
    }

    /**
     * Returns the node which sent this CTCP request
     * @return the node which sent this CTCP request
     */
    public Node getSender() {
        return sender;
    }

    /**
     * Returns the node which was the target for this CTCP request
     * @return the node which was the target for this CTCP request
     */
    public Node getTarget() {
        return target;
    }

    /**
     * Returns the CTCP request string
     * @return the CTCP request string
     */
    public String getRequest() {
        return message;
    }

    public String toString() {
        return "(" + target + ")CTCP from " + sender + ": " + message;
    }
}