package net.newbiehacker.commodorej.event;

import net.newbiehacker.commodorej.Node;
import net.newbiehacker.commodorej.Session;

/**
 * @author newbiehacker
 */
abstract class MessagePrototype extends IRCEvent {
    protected final Node sender, target;
    protected final String message;

    protected MessagePrototype(Session source, Type type, Node sender, Node target, String message) {
        super(source, type);
        this.sender = sender;
        this.target = target;
        this.message = message;
    }
}