package net.newbiehacker.commodorej.event;

import net.newbiehacker.commodorej.Session;
import net.newbiehacker.commodorej.Node;

/**
 ** This class represents a ctcp response event which is fired when a node sends a ctcp response to another node
 * @author newbiehacker
 */
public final class IRCCtcpResponseEvent extends MessagePrototype {
    public IRCCtcpResponseEvent(Session source, Node sender, Node target, String response) {
        super(source, Type.CTCP_RESPONSE, sender, target, response);
    }

    /**
     * Returns the node which sent this CTCP response
     * @return the node which sent this CTCP response
     */
    public Node getSender() {
        return sender;
    }

    /**
     * Returns the node which was the target for this CTCP response
     * @return the node which was the target for this CTCP response
     */
    public Node getTarget() {
        return target;
    }

    /**
     * Returns the CTCP response string
     * @return the CTCP response string
     */
    public String getResponse() {
        return message;
    }

    public String toString() {
        return "(" + target + ")CTCP reply from " + sender + ": " + message;
    }
}