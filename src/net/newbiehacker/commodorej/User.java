package net.newbiehacker.commodorej;

import java.util.*;

/**
 * This class represents a user on IRC
 * @author newbiehacker
 */
public final class User extends Node {
    String nick, name, host;
    final List<String> channels;
    final List<Character> modes;

    User(String nick, String name, String host) {
        super(Type.USER);
        this.nick = nick;
        this.name = name;
        this.host = host;
        this.channels = new ArrayList<String>();
        this.modes = new ArrayList<Character>();
    }

    /**
     * Returns the nick of this user
     * @return the nick of this user
     */
    public String getNick() {
        return nick;
    }

    /**
     * Returns the name of this user
     * @return the name of this user
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the host of this user
     * @return the host of this user
     */
    public String getHost() {
        return host;
    }

    /**
     * Returns whether the bot has seen the user on the specified channel
     * @param channel the channel we wish to check that the user is on
     * @return whether the bot has seen the user on the specified channel
     */
    public boolean onChannel(String channel) {
        return channels.contains(channel);
    }

    /**
     * Returns a list of channels that we are sure this user is on
     * @return a list of channels that we are sure this user is on
     */
    public List<String> getChannels() {
        return Collections.unmodifiableList(channels);
    }

    /**
     * Returns whether this user has the specified mode set
     * @param mode the mode we wish to check whether the specified user has set on them
     * @return whether this user has the specified mode set
     */
    public boolean hasMode(char mode) {
        return modes.contains(mode);
    }

    /**
     * Returns all modes set on this user
     * @return all modes set on this user
     */
    public List<Character> getModes() {
        return Collections.unmodifiableList(modes);
    }

    public String toString() {
        return nick;
    }
}