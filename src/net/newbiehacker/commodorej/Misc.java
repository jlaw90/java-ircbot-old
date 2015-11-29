package net.newbiehacker.commodorej;

import java.util.Date;

/**
 * This class contains miscellaneous methods which were designed for use by the library
 * @author newbiehacker
 */
public final class Misc {
    private Misc() {}

    static boolean isChannel(Session source, String s) {
        if (s == null || s.isEmpty())
            return false;
        char c = s.charAt(0);
        char[] chanprefixes = new char[]{'#'}; // Default
        if (source.hasParameter("CHANTYPES"))
            chanprefixes = source.getParameter("CHANTYPES").toCharArray();
        for (char c1 : chanprefixes)
            if (c == c1)
                return true;
        return false;
    }

    static Node getNode(Session source, String s) {
        if (isChannel(source, s) && source.onChannel(s))
            return source.getChannel(s);
        else if (source.containsUser(s))
            return source.getUser(s);
        else
            return new MiscNode(s);
    }

    /**
     * Joins the string in an array together using the specified sperator
     * @param d the String to user to join these strings
     * @param s the String array we wish to join
     * @param start the start position to join from
     * @param length the length of string we wish to join
     * @return a String created from the items in s from start to start + length joined with d
     */
    public static String join(String d, String[] s, int start, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(s[start + i]);
            if (i + 1 != length)
                sb.append(d);
        }
        return sb.toString();
    }

    static void handleModes(Session owner, Node target, Node sender, String[] modedata) {
        int i = 1;
        String arr = modedata[0];
        char[] chars = arr.toCharArray();
        int mode = 0;
        final int ADD = 0, DEL = 1;
        main_loop:
        for (char c : chars) {
            if (c == '+')
                mode = ADD;
            else if (c == '-')
                mode = DEL;
            else {
                if (target.getType() == Node.Type.CHANNEL) {
                    Channel chan = (Channel) target;
                    String chanmodes = owner.getParameter("CHANMODES");
                    if (chanmodes == null)
                        throw new RuntimeException("Server doesn't support CHANMODES");
                    String[] types = chanmodes.split(",");
                    if (types.length != 4)
                        throw new RuntimeException("This library does not understand the CHANMODES parameter from this server");
                    // Parse A-Modes (Modes which modify lists such as b, I & e)
                    char[] modes = types[0].toCharArray();
                    for (char m : modes) {
                        if (c == m) {
                            if (mode == ADD)
                                chan.lists.get(c).add(new ChannelListEntry(modedata[i++], sender.toString(), (int) new Date().getTime()));
                            else if (mode == DEL) {
                                for (ChannelListEntry cle : chan.lists.get(c))
                                    if (cle.getMask().toLowerCase().equals(modedata[i].toLowerCase())) {
                                        chan.lists.get(c).remove(cle);
                                        continue main_loop;
                                    }
                            }
                            continue main_loop;
                        }
                    }
                    // Parse B-Modes (Modes which change channel settings and take a parameter when set and unset)
                    modes = types[1].toCharArray();
                    for (char m : modes) {
                        if (c == m) {
                            if (mode == ADD)
                                chan.modes.put(c, modedata[i++]);
                            else if (mode == DEL)
                                chan.modes.remove(c);
                            continue main_loop;
                        }
                    }
                    // Parse C-Modes (Modes which change channel settings and take a parameter when set, but not when unset)
                    modes = types[2].toCharArray();
                    for (int idx1 = 0; idx1 < modes.length; idx1++) {
                        char m = modes[idx1];
                        if (c == m) {
                            if (mode == ADD)
                                chan.modes.put(c, modedata[i++]);
                            else if (mode == DEL)
                                chan.modes.remove(c);
                            continue main_loop;
                        }
                    }
                    // Parse D-Modes (Modes which change channels settings and take no parameters)
                    modes = types[3].toCharArray();
                    for (int idx1 = 0; idx1 < modes.length; idx1++) {
                        char m = modes[idx1];
                        if (c == m) {
                            if (mode == ADD) {
                                chan.modes.put(c, null);
                            } else if (mode == DEL)
                                chan.modes.remove(c);
                            continue main_loop;
                        }
                    }
                    // See if it's an access change on a user
                    String prefix = owner.getParameter("PREFIX");
                    int sIdx = prefix.indexOf('(');
                    int eIdx = prefix.indexOf(')');
                    String pModes = prefix.substring(sIdx + 1, eIdx);
                    char[] pc = pModes.toCharArray();
                    for (int idx1 = 0; idx1 < modes.length; idx1++) {
                        char m = pc[idx1];
                        if (c == m) {
                            User u = owner.getUser(modedata[i++]);
                            if (mode == ADD) {
                                char oldMode = (char) 255;
                                if (chan.userModes.containsKey(u))
                                    oldMode = chan.userModes.get(u);
                                int oidx = -1;
                                if (oldMode != 255)
                                    for (int idx2 = 0; idx2 < pc.length; idx2++)
                                        if (pc[idx2] == oldMode) {
                                            oidx = idx2;
                                            break;
                                        }
                                if (oldMode == 255 || idx1 > oidx)
                                    chan.userModes.put(u, c);
                            } else
                                chan.userModes.remove(u);
                            continue main_loop;
                        }
                    }
                } else if (target.getType() == Node.Type.USER) {
                    User u = (User) target;
                    if (mode == DEL && u.modes.contains(c))
                        u.modes.remove(c);
                    else if (!u.modes.contains(c))
                        u.modes.add(c);
                }
            }
        }
    }
}