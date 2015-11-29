package net.newbiehacker.commodorej.event;

import net.newbiehacker.commodorej.Channel;
import net.newbiehacker.commodorej.Session;

/**
 * This class represents a topic change event which is fired when a user changes the topic on a channel
 * @author newbiehacker
 */
public final class IRCTopicChangeEvent extends IRCEvent {
    private final Channel channel;

    public IRCTopicChangeEvent(Session source, Channel channel) {
        super(source, Type.TOPIC);
        this.channel = channel;
    }

    /**
     * Returns the channel on which the topic was modified
     * @return the channel on which the topic was modified
     */
    public Channel getChannel() {
        return channel;
    }

    public String toString() {
        return channel.getTopicSetter() + " changes the topic on " + channel.getName() + " to " + channel.getTopic();
    }
}