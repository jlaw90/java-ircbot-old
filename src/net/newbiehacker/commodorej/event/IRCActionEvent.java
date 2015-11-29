package net.newbiehacker.commodorej.event;

import net.newbiehacker.commodorej.Node;
import net.newbiehacker.commodorej.Session;

/**
 * This class represents an action event, which is fired when a node sends an action to another node on the server
 * @author newbiehacker
 */
public final class IRCActionEvent extends MessagePrototype {
    public IRCActionEvent(Session source, Node sender, Node target, String action) {
        super(source, Type.ACTION, sender, target, action);
    }

    /**
     * Returns the node that sent the action
     * @return the node that sent the action
     */
    public Node getSender() {
        return sender;
    }

    /**
     * Returns the node that received the action
     * @return the node that received the action
     */
    public Node getTarget() {
        return target;
    }

    /**
     * Returns the action
     * @return the action
     */
    public String getAction() {
        return message;
    }

    public String toString() {
        return "(" + target + ") * " + sender + " " + message;
    }
}