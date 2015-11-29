package net.newbiehacker.commodorej;

/**
 * Represents a node, which is an object to which messages can be sent/received from
 * @author newbiehacker
 */
public abstract class Node {
    /**
     * The Type of this Node
     */
    public enum Type {
        /**
         * Specifies that this node is a User object
         */
        USER,
        /**
         * Specifies that this node is a Channel object
         */
        CHANNEL,
        /**
         * Specifies that this node is a MiscNode object (in other words, the lib has no idea what it is)
         */
        MISC
    }

    private final Type type;

    protected Node(Type t) {
        this.type = t;
    }

    /**
     * Returns the type of this node
     * @return the type of this node
     * @see net.newbiehacker.commodorej.Node.Type
     */
    public final Type getType() {
        return type;
    }
}