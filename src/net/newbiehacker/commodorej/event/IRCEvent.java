package net.newbiehacker.commodorej.event;

import net.newbiehacker.commodorej.Session;

import java.util.Date;

/**
 * This class represents an event (such as a message, a notice, a mode change, etc.) that is fired by our lib
 * @author newbiehacker
 */
public abstract class IRCEvent {
    /**
     * This enum is used to specify the type of IRCEvent, so that instance checking does not have to be done
     */
    public enum Type {
        /**
         * Specifies that this IRCEvent is an IRCJoinEvent.
         * This event is fired when a user joins a channel
         */
        JOIN,
        /**
         * Specifies that this IRCEvent is an IRCQuitEvent.
         * This event is fired when a user quits from the server
         */
        QUIT,
        /**
         * Specifies that this IRCEvent is an IRCPartEvent.
         * This event is fired when a user parts a channel
         */
        PART,
        /**
         * Specifies that this IRCEvent is an IRCNickChangeEvent.
         * This event is fired when a user changes their nick
         */
        NICK,
        /**
         * Specifies that this IRCEvent is an IRCActionEvent.
         * This event is fired when a node sends an action to a channel or the bot
         */
        ACTION,
        /**
         * Specifies that this IRCEvent is an IRCTopicChangeEvent.
         * This event is fired when the topic on a channel is changed
         */
        TOPIC,
        /**
         * Specifies that this IRCEvent is an IRCMessageEvent.
         * This event is fired when a node sends a message to a channel or the bot
         */
        MESSAGE,
        /**
         * Specifies that this IRCEvent is an IRCNoticeEvent.
         * This event is fired when a node sends a notice to a channel or the bot
         */
        NOTICE,
        /**
         * Specifies that this IRCEvent is an IRCCtcpRequestEvent.
         * This event is fired when a node sends a ctcp request to a channel or the bot
         */
        CTCP_REQUEST,
        /**
         * Specifies that this IRCEvent is an IRCCtcpResponseEvent.
         * This event is fired when a node sends a ctcp reply to a channel or the bot
         */
        CTCP_RESPONSE,
        /**
         * Specifies that this IRCEvent is an IRCModeChangeEvent.
         * This event is fired when a mode is changed on a node
         */
        MODE,
        /**
         * Specifies that this IRCEvent is an IRCKickEvent.
         * This event is fired when a user is kicked from a channel
         */
        KICK,
        /**
         * Specifies that this IRCEvent is an IRCInviteEvent.
         * This event is fired when a node is invited to a channel
         */
        INVITE,
        /**
         * Specifies that this IRCEvent is an IRCServerMessageEvent.
         * This event is fired when a message is received from the server with a numeric identifer
         */
        SERVER_MESSAGE,
        /**
         * Specifies that this IRCEvent is an IRCConnectEvent.
         * This event is fired when the bot is connected to the server and logged in with it's nick
         */
        CONNECT,
        /**
         * Specifies that this IRCEvent is an IRCDisconnectEvent.
         * This event is fired when the bot is disconnected from the server (usually due to a socket error)
         */
        DISCONNECT,
        /**
         * Specifies that this IRCEvent is an IRCErrorEvent.
         * This event is fired when the bot is forcefully disconnected from the server
         */
        ERROR
    }

    protected final Session source;
    protected final Type type;
    protected final Date time;

    protected IRCEvent(Session source, Type type) {
        this.source = source;
        this.type = type;
        time = new Date();
    }

    /**
     * Returns the connection that fired this event
     * @return the connection that fired this event
     */
    public Session getSource() {
        return source;
    }

    /**
     * Returns the type of this IRCEvent
     * @return the type of this IRCEvent
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns the time that this event was created
     * @return the time that this event was created
     */
    public Date getTime() {
        return time;
    }
}