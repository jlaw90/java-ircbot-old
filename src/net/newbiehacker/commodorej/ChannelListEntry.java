package net.newbiehacker.commodorej;

import java.util.Date;

/**
 * This class represents an entry in a channel list (such as the ban list or the except list)
 * @author newbiehacker
 */
public final class ChannelListEntry {
    private final String mask, setter;
    private final Date time;

    ChannelListEntry(String mask, String setter, long time) {
        this.mask = mask;
        this.setter = setter;
        this.time = new Date(time * 1000);
    }

    /**
     * Returns the mask of this entry
     * @return the mask of this entry
     */
    public String getMask() {
        return mask;
    }

    /**
     * Returns the person who added this channel list entry
     * @return the person who added this channel list entry
     */
    public String getSetter() {
        return setter;
    }

    /**
     * Returns the time that this entry was set
     * @return the time that this entry was set
     */
    public Date getTime() {
        return time;
    }

    public String toString() {
        return mask + " set by " + setter + " on " + time;
    }
}