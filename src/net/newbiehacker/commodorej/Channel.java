package net.newbiehacker.commodorej;

import java.util.*;

/**
 * This class represents a channel on the server
 * @author newbiehacker
 */
public final class Channel extends Node {
    final String name;
    final List<User> users;
    final Map<User, Character> userModes;
    final Map<Character, List<ChannelListEntry>> lists;
    final Map<Character, String> modes;
    String topic, topicSetter;
    Date topicTime;

    Channel(Session t, String name) {
        super(Type.CHANNEL);
        this.name = name;
        users = new ArrayList<User>();
        userModes = new HashMap<User, Character>();
        lists = new HashMap<Character, List<ChannelListEntry>>();
        modes = new HashMap<Character, String>();

        // Get chanmodes setting as sent from the server
        String modes = t.getParameter("CHANMODES");
        if(modes == null)
            throw new RuntimeException("Server does not support CHANMODES!!");
        char[] listTypes = modes.split(",")[0].toCharArray();

        // Set up our channel lists
        for(char c: listTypes) {
            lists.put(c, new ArrayList<ChannelListEntry>());
            t.sendLine("MODE " + name + " +" + c);
        }
        // Request all other modes for this channel
        t.sendLine("MODE " + name);
    }

    /**
     * Returns the name of this channel
     * @return the name of this channel
     */
    public String getName() {
        return name;
    }

    /**
     * Returns whether the specified user is currently on this channel
     * @param user the user we wish to check for on this channel
     * @return whether the specified user is on this channel
     */
    public boolean hasUser(User user) {
        return users.contains(user);
    }

    /**
     * Returns all the users currently on this channel
     * @return all the users currently on this channel
     */
    public List<User> getUsers() {
        return Collections.unmodifiableList(users);
    }

    /**
     * Returns the usermode of the specified user (e.g. '@' for op) or '\u0000' for a normal user
     * @param u the user we wish to get the user mode of
     * @return the usermode of the specified user (e.g. '@' for op) or '\u0000' for a normal user
     */
    public char getUserMode(User u) {
        Character c = userModes.get(u);
        if(c == null)
            return '\u0000';
        return c;
    }

    /**
     * Returns a list of entries for the specified channel list
     * @param list the mode character representing the list (e.g. 'b' for the ban list, 'I' for the invite list, 'e' for the excepts list, etc.)
     * @return a list of entries for the specified channel list
     */
    public List<ChannelListEntry> getChannelList(char list) {
        return lists.get(list);
    }

    /**
     * Returns whether the specified mode is set on this channel
     * @param mode the mode we wish to check for
     * @return whether the specified mode is set on this channel
     */
    public boolean hasMode(char mode) {
        return modes.containsKey(mode);
    }

    /**
     * Returns the parameter for the specified mode (if it's set)
     * @param mode the mode we wish to get the parameter for
     * @return the parameter for the specified mode, or null if we do not have that mode
     */
    public String getModeParameter(char mode) {
        return modes.get(mode);
    }

    /**
     * Returns the topic of this channel
     * @return the topic of this channel
     */
    public String getTopic() {
        return topic;
    }

    /**
     * Returns the nick of the person who set the topic on this channel
     * @return the nick of the person who set the topic on this channel
     */
    public String getTopicSetter() {
        return topicSetter;
    }

    /**
     * Returns the date that the topic was set
     * @return the date that the topic was set
     */
    public Date getTopicTime() {
        return topicTime;
    }

    public String toString() {
        return name;
    }
}