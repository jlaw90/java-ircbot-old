package net.newbiehacker.commodorej.event;

import net.newbiehacker.commodorej.Session;
import net.newbiehacker.commodorej.Node;

/**
 * This class represents a mode change event which is fired when a node changes the modes of another node
 * @author newbiehacker
 */
public final class IRCModeChangeEvent extends MessagePrototype {
    public IRCModeChangeEvent(Session source, Node sender, Node target, String modes) {
        super(source, Type.MODE, sender, target, modes);
    }

    /**
     * Returns the node that set the modes on the target
     * @return the node that set the modes on the target
     */
    public Node getSender() {
        return sender;
    }

    /**
     * Returns the node that had its mode changed
     * @return the node that had its mode changed
     */
    public Node getTarget() {
        return target;
    }

    /**
     * Returns a string representing the modes that were changed on the target
     * @return a string representing the modes that were changed on the target
     */
    public String getModeString() {
        return message;
    }

    public String toString() {
        return sender + " sets mode " + message + " on " + target;
    }
}