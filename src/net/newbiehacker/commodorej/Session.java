package net.newbiehacker.commodorej;

import net.newbiehacker.commodorej.event.*;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoSession;
import org.apache.mina.transport.socket.nio.SocketConnector;
import org.apache.mina.transport.socket.nio.SocketConnectorConfig;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;

/**
 * This class represents a connection to an IRC server with methods for obtaining information on the channels we're currently on and the users we've seen, etc.
 *
 * @author newbiehacker
 */
public final class Session implements IoHandler {
    private final ConnectionManager parent;
    private final int port;
    private String host, login, name, nick;
    private final Map<String, User> users;
    private final Map<String, String> parameters;
    private final Map<String, Channel> channels;
    private StringBuffer inputBuffer;
    private boolean sent_connect, received_connect, verbose;
    private IoSession session;

    Session(ConnectionManager parent, String host, int port, String nick, String login, String name) {
        this.parent = parent;
        this.host = host;
        this.port = port;
        this.login = login;
        this.name = name;
        this.nick = nick;
        this.users = new HashMap<String, User>();
        this.parameters = new HashMap<String, String>();
        this.channels = new HashMap<String, Channel>();
    }

    void connect() {
        try {
            if (session != null)
                throw new IOException("This connection is already connected");
            SocketConnector sc = new SocketConnector();
            SocketConnectorConfig conf = sc.getDefaultConfig();
            conf.setConnectTimeout(5);
            sc.connect(new InetSocketAddress(host, port), this);
        } catch (Throwable t) {
            parent.fireError(t);
        }
    }

    /**
     * Returns the ConnectionManager instance that created this session
     *
     * @return the ConnectionManager instance that created this session
     */
    public ConnectionManager getParent() {
        return parent;
    }

    /**
     * Returns whether this connection is supposed to be verbose or not
     *
     * @return whether this connection is supposed to be verbose or not
     */
    public boolean isVerbose() {
        return verbose;
    }

    /**
     * Sets whether this connection is supposed to be verbose
     *
     * @param verbose whether we want this connection to be verbose
     */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    private void appendStuff(String pre, String line) {
        if (line == null) {
            session.write(ByteBuffer.wrap((pre + "\r\n").getBytes()));
            return;
        }
        if (pre.length() >= 256) {
            session.write(ByteBuffer.wrap((pre + line + "\r\n").getBytes()));
        }
        final int off = pre.length();
        final int mLength = 256 - off;
        while (line.length() > mLength) {
            session.write(ByteBuffer.wrap((pre + line.substring(0, mLength) + "\r\n").getBytes()));
            line = line.substring(mLength);
        }
        if (line.length() > 0)
            session.write(ByteBuffer.wrap((pre + line + "\r\n").getBytes()));
    }

    /**
     * Appends a line to our output buffer which will later be sent to the server
     *
     * @param line the line to send
     */
    public void sendLine(String line) {
        appendStuff(line, null);
    }

    /**
     * Sends a message to the recipient
     * Please note that messages which go over 256 bytes will be split into multiple lines
     *
     * @param recipient the intended recipient of the message
     * @param message   the message body
     */
    public void sendMessage(String recipient, String message) {
        appendStuff("PRIVMSG " + recipient + " :", message);
    }

    /**
     * Sends a notice to the recipient
     * Please note that notices which go over 256 bytes will be split into multiple lines
     *
     * @param recipient the intended recipient of the notice
     * @param notice    the notice body
     */
    public void sendNotice(String recipient, String notice) {
        appendStuff("NOTICE " + recipient + " :", notice);
    }

    /**
     * Sends a CTCP request to the recipient
     * Please note that requests which go over 256 bytes will be split into multiple lines
     *
     * @param recipient the intended recipient of the request
     * @param request   the request body
     */
    public void sendCtcpRequest(String recipient, String request) {
        sendMessage(recipient, '\u0001' + request + '\u0001');
    }

    /**
     * Sends a CTCP response to the recipient
     * Please note that responses which go over 256 bytes will be split into multiple lines
     *
     * @param recipient the intended recipient of the response
     * @param response  the response body
     */
    public void sendCtcpResponse(String recipient, String response) {
        sendNotice(recipient, '\u0001' + response + '\u0001');
    }

    /**
     * Sends a action to the recipient
     * Please note that actions which go over 256 bytes will be split into multiple lines
     *
     * @param recipient the intended recipient of the action
     * @param action    the action body
     */
    public void sendAction(String recipient, String action) {
        sendCtcpRequest(recipient, "ACTION " + action);
    }

    /**
     * Changes the nick of the bot
     *
     * @param newNick the new nickname for the bot
     */
    public void changeNick(String newNick) {
        sendLine("NICK " + newNick);
    }

    /**
     * Changes the topic on the specified channel
     *
     * @param channel  the channel we wish to change the topic on
     * @param newTopic the topic we wish to change to
     */
    public void setTopic(String channel, String newTopic) {
        sendLine("TOPIC " + channel + " :" + newTopic);
    }

    /**
     * Sets a mode on the specified recipient
     *
     * @param recipient the recipient of this mode change
     * @param mode      the modes(s) we wish to (un)set
     */
    public void setMode(String recipient, String mode) {
        sendLine("MODE " + recipient + " :" + mode);
    }

    /**
     * Joins the specified channel with the specified key
     *
     * @param channel the channel we wish to join
     * @param key     the key on this channel
     */
    public void joinChannel(String channel, String key) {
        sendLine("JOIN " + channel + (key == null ? "" : " :" + key));
    }

    /**
     * Joins the specified channel
     *
     * @param channel the channel we wish to join
     */
    public void joinChannel(String channel) {
        joinChannel(channel, null);
    }

    /**
     * Parts the specified channel with the specified message
     *
     * @param channel the channel we wish to part
     * @param message the part message
     */
    public void partChannel(String channel, String message) {
        sendLine("PART " + channel + (message == null ? "" : " :" + message));
    }

    /**
     * Parts the specified channel
     *
     * @param channel the channel we wish to part
     */
    public void partChannel(String channel) {
        partChannel(channel, null);
    }

    /**
     * Quits from the server
     *
     * @param message our quit message
     */
    public void quit(String message) {
        sendLine("QUIT" + (message == null ? "" : " :" + message));
    }

    /**
     * Quits from the server
     */
    public void quit() {
        quit(null);
    }

    /**
     * Attempts to stop processing on this connection and to close all sockets related with it
     */
    public void close() {
        session.close();
    }

    /**
     * Returns the port that this Session will (or has) connect(ed) to
     *
     * @return the port that this Session will (or has) connect(ed) to
     */
    public int getPort() {
        return port;
    }

    /**
     * Returns the host of the server that this connection will (or has) connect(ed) to
     *
     * @return the host of the server that this connection will (or has) connect(ed) to
     */
    public String getHost() {
        return host;
    }

    /**
     * Returns the nick of the bot
     *
     * @return the nick of the bot
     */
    public String getNick() {
        return nick;
    }

    /**
     * Returns the name of the bot
     *
     * @return the name of the bot
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the login of the bot
     *
     * @return the login of the bot
     */
    public String getLogin() {
        return login;
    }

    /**
     * Returns whether this connection has the specified parameter
     *
     * @param s the name of the parameter we wish to check
     * @return whether this connection has the specified parameter
     */
    public boolean hasParameter(String s) {
        return parameters.containsKey(s);
    }

    /**
     * Returns the value of the specified parameter (e.g. for PREFIXES it could be "(~&@%+)qaohv")
     *
     * @param s the parameter we wish to get the value for
     * @return the value for the specified parameter, or null if it does not exist
     */
    public String getParameter(String s) {
        return parameters.get(s);
    }

    /**
     * Returns whether we have user data cache'd for the specified nick<br />
     * If this method returns true, it does not necessarily mean that the specified user is currently online, just that they have been during the runtime of the bot
     *
     * @param nick the nick we're checking for
     * @return whether we have uder data cache'd for the specified nick
     */
    public boolean containsUser(String nick) {
        return users.containsKey(nick);
    }

    /**
     * Returns the user with the specified nick
     *
     * @param nick the user we wish to get the User object for
     * @return the user with the specified nick or null if we do not have their information
     */
    public User getUser(String nick) {
        return users.get(nick);
    }

    /**
     * Returns the user-cache for all users we have seen in this session
     *
     * @return the user-cache for all users we have seen in this session
     */
    public List<User> getUsers() {
        List<User> users = new ArrayList<User>();
        for (User u : this.users.values()) {
            users.add(u);
        }
        return Collections.unmodifiableList(users);
    }

    /**
     * Returns the list of channels that the bot is currently on
     *
     * @return the list of channels that the bot is currently on
     */
    public List<Channel> getChannels() {
        List<Channel> channels = new ArrayList<Channel>();
        for (Channel c : this.channels.values()) {
            channels.add(c);
        }
        return Collections.unmodifiableList(channels);
    }

    /**
     * Returns whether the bot is currently on the specified channel
     *
     * @param channel the channel we wish to check if we are on
     * @return whether the bot is currently on this channel or not
     */
    public boolean onChannel(String channel) {
        return channels.containsKey(channel);
    }

    /**
     * Returns the channel object for the specified channel
     *
     * @param channel the name of the channel we wish to get the channel object for
     * @return the channel object for the specified channel
     */
    public Channel getChannel(String channel) {
        return channels.get(channel);
    }

    private boolean isNumeric(String s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c < '0' || c > '9')
                return false;
        }
        return true;
    }

    private User getUser(String nick, String name, String host) {
        if (nick.equals(this.nick))
            if (name != null)
                this.name = name;
        User u;
        if (users.containsKey(nick)) {
            u = users.get(nick);
            if (name != null)
                u.name = name;
            if (host != null)
                u.host = host;
        } else {
            u = new User(nick, name, host);
            users.put(nick, u);
        }
        return u;
    }

    @SuppressWarnings({"SuspiciousMethodCalls"})
    private void handleLine(String line) {
        try {
            if (line.charAt(0) == ':')
                line = line.substring(1);
            String[] data = line.split(" ");
            if (isNumeric(data[1])) {
                // A new server message approaches!!
                int id = Integer.parseInt(data[1]);
                String content = Misc.join(" ", data, 3, data.length - 3);
                if (content.charAt(0) == ':')
                    content = content.substring(1);
                // Parameter information
                if (id == 5) {
                    String mstring = Misc.join(" ", data, 3, data.length - 3);
                    mstring = mstring.substring(0, mstring.indexOf(':') - 1);
                    String[] params = mstring.split(" ");
                    for (String s : params) {
                        if (s.indexOf('=') == -1) {
                            parameters.put(s.charAt(0) == '-' ? s.substring(1) : s, null);
                        } else {
                            int idx = s.indexOf('=');
                            parameters.put(s.substring(0, idx), s.substring(idx + 1));
                        }
                    }
                    received_connect = true;
                } else if (id > 5 && received_connect && !sent_connect) {
                    parent.fireEvent(new IRCConnectEvent(this));
                    sent_connect = true;
                } else if (id == 352) {
                    // WHO line
                    String chan = data[3];


                    String login = data[4];
                    String host = data[5];
                    // String server = datat[6];
                    String nick = data[7];
                    String modes = data[8];
                    //String hops = data[9].substring(1, data[9].length - 2);
                    //String fullName = Misc.join(" ", data, 10, data.length - 10);
                    User u = getUser(nick, login, host);
                    if (!u.channels.contains(chan))
                        u.channels.add(chan);
                    String prefix = getParameter("PREFIX");
                    int sIdx = prefix.indexOf('(');
                    int eIdx = prefix.indexOf(')');
                    String pModes = prefix.substring(sIdx + 1, eIdx);
                    String pPrefixes = prefix.substring(eIdx + 1);
                    char[] pmc = pModes.toCharArray();
                    char[] ppc = pPrefixes.toCharArray();
                    if (modes.charAt(0) == ':')
                        modes = modes.substring(1);
                    char[] mModes = modes.toCharArray();
                    u.modes.clear(); // Clear the users modes for a fresh start
                    char oldMode = 255;
                    if (onChannel(chan)) {
                        Channel chanl = getChannel(chan);
                        if (chanl.getUserMode(u) != '\u0000')
                            oldMode = chanl.getUserMode(u);
                    }
                    main_loop:
                    for (char c : mModes) {
                        for (int i = 0; i < ppc.length; i++) {
                            char pp = ppc[i];
                            if (c == pp) {
                                // Get index of old mode
                                int oidx = 255;
                                if (oldMode != 255)
                                    for (int i1 = 0; i1 < pmc.length; i1++) {
                                        if (oldMode == pmc[i1]) {
                                            oidx = i1;
                                            break;
                                        }
                                    }
                                if (oldMode == 255 || i < oidx) {
                                    if (onChannel(chan))
                                        getChannel(chan).userModes.put(u, c);
                                    continue main_loop;
                                }
                            }
                        }
                        u.modes.add(c);
                    }
                    if (onChannel(chan) && getChannel(chan).users.contains(u))
                        getChannel(chan).users.add(u);
                } else if (id == 367) {
                    getChannel(data[3]).lists.get('b').add(new ChannelListEntry(data[4], data[5], Long.parseLong(data[6])));
                } else if (id == 348) {
                    getChannel(data[3]).lists.get('e').add(new ChannelListEntry(data[4], data[5], Long.parseLong(data[6])));
                } else if (id == 346) {
                    getChannel(data[3]).lists.get('I').add(new ChannelListEntry(data[4], data[5], Long.parseLong(data[6])));
                } else if (id == 324) {
                    String mstring = Misc.join(" ", data, 4, data.length - 4);
                    if (mstring.charAt(0) == ':')
                        mstring = mstring.substring(1);
                    String[] modeData = mstring.split(" ");
                    Misc.handleModes(this, Misc.getNode(this, data[3]), null, modeData);
                    parent.fireEvent(new IRCModeChangeEvent(this, new MiscNode(data[0]), Misc.getNode(this, data[3]), mstring));
                } else if (id == 332) {
                    // Topic server message
                    Channel channel = getChannel(data[3]);
                    channel.topic = line.substring(line.indexOf(':') + 1);
                } else if (id == 333) {
                    // Topic info message
                    Channel channel = getChannel(data[3]);
                    channel.topicSetter = data[4];
                    channel.topicTime = new Date(Long.parseLong(data[5]) * 1000);
                }
                parent.fireEvent(new IRCServerMessageEvent(this, new MiscNode(data[0]), Misc.getNode(this, data[2]), id, content));
                return;
            }
            // PING
            if ("PING".equals(data[0])) {
                sendLine("PONG " + data[1]);
                return;
            }
            String sender = data[0];
            String type = data[1];
            String target = data[2];
            int sIdx = sender.indexOf('!');
            Node sendObj = new MiscNode(sender);
            String uNick = null, uName = null, uHost = null;
            if (sIdx != -1) {
                int eIdx = sender.indexOf('@');
                uNick = sender.substring(0, sIdx);
                uName = sender.substring(sIdx + 1, eIdx);
                uHost = sender.substring(eIdx + 1);
                sendObj = getUser(uNick, uName, uHost);
                if (onChannel(target) && !getChannel(target).hasUser((User) sendObj))
                    getChannel(target).users.add((User) sendObj);
            }
            int off = sender.length() + type.length() + 3;
            String message = line.substring(off);
            if ("MODE".equals(type)) {
                String mstring = Misc.join(" ", data, 3, data.length - 3);
                if (mstring.charAt(0) == ':')
                    mstring = mstring.substring(1);
                String[] nArr = mstring.split(" ");
                Misc.handleModes(this, Misc.getNode(this, target), sendObj, nArr);
                parent.fireEvent(new IRCModeChangeEvent(this, sendObj, Misc.getNode(this, target), mstring));
                if (onChannel(target))
                    sendLine("WHO " + target);
            } else if ("NOTICE".equals(type)) {
                String notice = message.substring(target.length() + 1);
                // CTCP response
                if (notice.charAt(0) == 1 && notice.charAt(notice.length()) == 1)
                    parent.fireEvent(new IRCCtcpResponseEvent(this, sendObj, Misc.getNode(this, target), notice.substring(1, notice.length() - 2)));
                else
                    parent.fireEvent(new IRCNoticeEvent(this, sendObj, Misc.getNode(this, target), notice));
            } else if ("PRIVMSG".equals(type)) {
                message = message.substring(target.length() + 1);
                if (message.charAt(0) == 1 && message.charAt(message.length() - 1) == 1) {
                    String ctcp = message.substring(1, message.length() - 1);
                    if (ctcp.startsWith("ACTION"))
                        parent.fireEvent(new IRCActionEvent(this, sendObj, Misc.getNode(this, target), ctcp.substring(7)));
                    else {
                        if ("TIME".equals(ctcp.toUpperCase()))
                            sendCtcpResponse(sendObj.toString(), "TIME " + new Date());
                        else if ("VERSION".equals(ctcp.toUpperCase()))
                            sendCtcpResponse(sendObj.toString(), "VERSION Commodore (Java implementation) version 0.5b, Copyright 2008 James Lawrence (http://www.newbiehacker.net)");
                        parent.fireEvent(new IRCCtcpRequestEvent(this, sendObj, Misc.getNode(this, target), ctcp));
                    }
                } else
                    parent.fireEvent(new IRCMessageEvent(this, sendObj, Misc.getNode(this, target), message));
            } else if ("JOIN".equals(type)) {
                target = target.substring(1);
                if (uNick.equals(nick)) {
                    channels.put(target, new Channel(this, target));
                    sendLine("WHO " + target);
                }
                Channel chan = getChannel(target);
                ((User) sendObj).channels.add(target);
                chan.users.add((User) sendObj);
                parent.fireEvent(new IRCJoinEvent(this, (User) sendObj, getChannel(target)));
            } else if ("PART".equals(type)) {
                if (uNick.equals(nick)) {
                    parent.fireEvent(new IRCPartEvent(this, (User) sendObj, getChannel(target), message.length() == target.length() - 1 ? "" : message.substring(target.length() + 1)));
                    channels.remove(target);
                } else {
                    Channel c = getChannel(target);
                    c.users.remove(sendObj);
                    c.userModes.remove(sendObj);
                    parent.fireEvent(new IRCPartEvent(this, (User) sendObj, c, message.length() == target.length() - 1 ? "" : message.substring(target.length() + 1)));
                }
                ((User) sendObj).channels.remove(target);
            } else if ("QUIT".equals(type)) {
                parent.fireEvent(new IRCQuitEvent(this, (User) sendObj, message.length() == 0 ? "" : message));
                ((User) sendObj).channels.clear();
                // Remove the user from all channels
                for (Channel c : channels.values()) {
                    c.users.remove(sendObj);
                    c.userModes.remove(sendObj);
                }
            } else if ("NICK".equals(type)) {
                target = target.substring(1);
                if (uNick.equals(nick))
                    nick = target;
                User u = getUser(uNick, uName, uHost);
                u.nick = target;
                users.remove(uNick);
                users.put(target, u);
                // Re-who the nick
                sendLine("WHO " + target);
                parent.fireEvent(new IRCNickChangeEvent(this, (User) sendObj, uNick));
            } else if ("TOPIC".equals(type)) {
                Channel channel = getChannel(target);
                channel.topicSetter = uNick;
                channel.topicTime = new Date();
                channel.topic = message.substring(target.length() + 1);
                parent.fireEvent(new IRCTopicChangeEvent(this, channel));
            } else if ("KICK".equals(type)) {
                User u = getUser(data[3], null, null);
                Channel channel = getChannel(target);
                u.channels.remove(target);
                channel.users.remove(u);
                channel.userModes.remove(u);
                parent.fireEvent(new IRCKickEvent(this, sendObj, u, channel, message.substring(target.length() + u.nick.length() + 2)));
                if (u.getNick().equals(nick))
                    channels.remove(target);
            } else if ("INVITE".equals(type)) {
                parent.fireEvent(new IRCInviteEvent(this, sendObj, users.get(target), data[3].substring(1)));
            }
        } catch (Exception e) {
            parent.fireError(e);
        }
    }

    public void sessionCreated(IoSession session) throws Exception {
    }

    public void sessionOpened(IoSession session) throws Exception {
        this.session = session;
        sendLine("NICK " + nick);
        sendLine("USER " + login + " " + host + " " + " *8 :" + name);
    }

    public void sessionClosed(IoSession session) throws Exception {
        parent.fireEvent(new IRCDisconnectEvent(this));
        System.out.println("Session closed [in: " + session.getReadBytes() + " / out: " + session.getWrittenBytes() + "]");
    }

    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
    }

    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        parent.fireError(cause);
    }

    public void messageReceived(IoSession session, Object message) throws Exception {
        ByteBuffer buf = (ByteBuffer) message;
        if (inputBuffer == null)
            inputBuffer = new StringBuffer();
        while (buf.hasRemaining()) {
            char c = (char) buf.get();
            if (c == '\n') {
                String line = inputBuffer.toString();
                handleLine(line);
                if (verbose)
                    System.out.println("<<< " + line);
                inputBuffer = new StringBuffer();
            } else if (c != '\r')
                inputBuffer.append(c);
        }
    }

    public void messageSent(IoSession session, Object message) throws Exception {
        if (verbose) {
            ByteBuffer buf = (ByteBuffer) message;
            StringBuilder temp = new StringBuilder();
            while (buf.hasRemaining())
                temp.append((char) buf.get());
            System.out.print(">>> " + temp.toString());
        }
    }
}