package net.newbiehacker.commodorej.event;

import net.newbiehacker.commodorej.Node;
import net.newbiehacker.commodorej.Session;

/**
 * This class represents a notice event which is fired when a node sends a notice to another node
 * @author newbiehacker
 */
public final class IRCNoticeEvent extends MessagePrototype {
    public IRCNoticeEvent(Session source, Node sender, Node target, String notice) {
        super(source, Type.NOTICE, sender, target, notice);
    }

    /**
     * Returns the node that sent this notice
     * @return the node that sent this notice
     */
    public Node getSender() {
        return sender;
    }

    /**
     * Returns the node that received this notice
     * @return the node that received this notice
     */
    public Node getTarget() {
        return target;
    }

    /**
     * Returns the notice
     * @return the notice
     */
    public String getNotice() {
        return message;
    }

    public String toString() {
        return "(" + target + ")>" + sender + "< " + message;
    }/**/
}