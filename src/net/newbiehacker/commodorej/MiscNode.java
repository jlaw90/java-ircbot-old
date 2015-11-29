package net.newbiehacker.commodorej;

/**
 * This class represents a node which is not a channel or a user (could possibly be the server)
 * @author newbiehacker
 */
public final class MiscNode extends Node {
    private String name;

    MiscNode(String name) {
        super(Type.MISC);
        this.name = name;
    }

    /**
     * Returns the name of this node
     * @return the name of this node
     */
    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }
}