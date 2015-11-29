package net;

import net.newbiehacker.commodorej.*;
import net.newbiehacker.commodorej.event.*;

import javax.script.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.HashMap;

/**
 * Debigging/Testing class for CommodoreJ<br/>
 * This class provides a basic implementation of CommodoreJ and takes advantage of the javax.script package to allow dynamic calls to be made during runtime, which allows for easier debugging.
 *
 * @author newbiehacker
 */
public final class Tester implements IRCEventListener, ErrorHandler {
    private ConnectionManager cm;
    private ScriptEngine engine;
    private Timer kickTimer;
    private Map<String, Integer> warnings;
    public static int nickChangeTime = 60;
    public static int warningThreshhold = 3;

    private Tester(String host, int port, String nick, String login, String name) throws IOException {
        cm = new ConnectionManager();
        warnings = new HashMap<String, Integer>();
        cm.registerListener(this);
        cm.registerErrorHandler(this);
        kickTimer = new Timer("KickTimer", true);
        Session ic = cm.connect(host, port, nick, login, name);
        ic.setVerbose(true);
        engine = new ScriptEngineManager().getEngineByExtension("js");
        Bindings b = engine.getBindings(ScriptContext.GLOBAL_SCOPE);
        b.put("source", ic);
        engine.setBindings(b, ScriptContext.GLOBAL_SCOPE);
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while ((line = in.readLine()) != null) {
            if (line.startsWith("eval ")) {
                String cmd = line.substring(5);
                try {
                    Object o = engine.eval(cmd);
                    if (o != null)
                        System.out.println("Eval: " + o);
                } catch (ScriptException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Application entry point
     *
     * @param args the arguments to send to this program
     * @throws IOException if there was an error while trying to connect to the server
     */
    public static void main(String[] args) throws IOException {
        if (args.length < 5) {
            System.out.println("Usage: Tester [host] [port] [nick] [login] [name]");
            System.exit(0);
        }
        Tester t = new Tester(args[0], Integer.parseInt(args[1]), args[2], args[3], Misc.join(" ", args, 4, args.length - 4));
    }

    public void onIRCEvent(IRCEvent e) {
        final Session source = e.getSource();
        switch (e.getType()) {
            case CONNECT:
                source.sendMessage("NickServ", "IDENTIFY <omitted>");
                source.joinChannel("#rst");
                break;
            case JOIN:
                IRCJoinEvent ije = (IRCJoinEvent) e;
                User u = ije.getUser();
                if (u.getNick().startsWith("Frugoo")) {
                    source.sendMessage("#frugooscape.net", u.getNick() + ", please change your nick within " + nickChangeTime + " seconds using \u00034\u0002/nick <my nick>\u0002\u000F or you will be kicked.");
                    kickTimer.schedule(new NickKicker(ije), nickChangeTime * 1000);
                }
                break;
            case MESSAGE:
                IRCMessageEvent ime = (IRCMessageEvent) e;
                if (ime.getTarget().getType() == Node.Type.CHANNEL) {
                    Channel target = (Channel) ime.getTarget();
                    if (ime.getSender().getType() == Node.Type.USER) {
                        User sender = (User) ime.getSender();
                        String msg = ime.getMessage();
                        if(!msg.startsWith("!") || msg.length() < 2)
                            return;
                        String[] cmdData = msg.substring(1).split(" ");
                        String cmd = cmdData[0].toLowerCase();
                        String[] args = new String[cmdData.length - 1];
                        System.arraycopy(cmdData, 1, args, 0, args.length);
                        UserType utype = UserType.fromChar(target.getUserMode(sender));
                        switch (utype) {
                            case Owner:
                                // Owner commands
                            case Protect:
                                // Protect commands
                            case Op:
                                // Op commands
                            case Halfop:
                                if(cmd.equals("warn") && args.length >= 2) {
                                    String nick = args[0];
                                    String reason = arrayJoin(args, 1, args.length, " ");
                                    User t = source.getUser(nick);
                                    if(t != null) {
                                        if(!warnings.containsKey(t.getHost()))
                                            warnings.put(t.getHost(), 0);
                                        int count = warnings.get(t.getHost()) + 1;
                                        warnings.put(t.getHost(), count);
                                        if(count > warningThreshhold) {
                                            source.sendLine("MODE " + target.getName() + " +b *!*@" + t.getHost());
                                            source.sendLine("KICK " + target.getName() + " " + t.getNick() + " :" + reason + " (you were warned)");
                                        } else
                                            source.sendLine("KICK " + target.getName() + " " + t.getNick() + " :" +
                                                    (count == warningThreshhold? "last" : count +
                                                            getIntPrefix(count)) + " warning: " + reason);
                                    }
                                }
                            case Voice:
                                // Voice commands
                            default:
                                if(cmd.equals("s2m") && args.length >= 1) {
                                    char[] a = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
                                            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
                                            'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3',
                                            '4', '5', '6', '7', '8', '9', '.', ',',
                                            '?'};
                                    String[] m = {".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..", ".---",
                                            "-.-", ".-..", "--", "-.", "---", ".--.", "--.-", ".-.", "...", "-", "..-",
                                            "...-", ".--", "-..-", "-.--", "--..", "-----", ".----", "..---", "...--",
                                            "....-", ".....", "-....", "--...", "---..", "----.", ".-.-.-", "--..--",
                                            "..--.."};
                                    StringBuilder out = new StringBuilder("Morse:");
                                    for(String s: args) {
                                        s = s.toUpperCase();
                                        char[] chars = s.toCharArray();
                                        char_loop:
                                        for(char c: chars) {
                                            for(int i = 0; i < a.length; i++)
                                                if(c == a[i]) {
                                                    out.append(" ").append(m[i]);
                                                    continue char_loop;
                                                }
                                            out.append(" ?");
                                        }
                                        out.append(" ");
                                    }
                                    source.sendMessage(target.getName(), out.toString());
                                } else if(cmd.equals("m2s") && args.length >= 1) {
                                    char[] a = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
                                            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
                                            'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3',
                                            '4', '5', '6', '7', '8', '9', '.', ',',
                                            '?'};
                                    String[] m = {".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..", ".---",
                                            "-.-", ".-..", "--", "-.", "---", ".--.", "--.-", ".-.", "...", "-", "..-",
                                            "...-", ".--", "-..-", "-.--", "--..", "-----", ".----", "..---", "...--",
                                            "....-", ".....", "-....", "--...", "---..", "----.", ".-.-.-", "--..--",
                                            "..--.."};
                                    StringBuilder out = new StringBuilder("String:");
                                    s_loop:
                                    for(String s: args) {
                                        if(s.isEmpty()) {
                                            out.append(" ");
                                            continue;
                                        }
                                        for(int i = 0; i < m.length; i++) {
                                            if(s.equals(m[i])) {
                                                out.append(a[i]);
                                                continue s_loop;
                                            }
                                        }
                                        out.append("?");
                                    }
                                    source.sendMessage(target.getName(), out.toString());
                                }
                        }
                    }
                }
                break;
        }
    }

    public void onError(Throwable t) {
        t.printStackTrace();
    }

    private static String arrayJoin(String[] data, int off, int len, String delimiter) {
        StringBuilder sb = new StringBuilder();
        for(int i = off; i < len; i++)
            sb.append(data[i]).append(i + 1 == len? "": delimiter);
        return sb.toString();
    }

    public static String getIntPrefix(int i) {
        int hmod = i % 100;
        if(hmod >= 4 && hmod <= 20)
            return "th";
        int tmod = i % 10;
        switch(tmod) {
            case 0:
                return "th";
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    public static final class NickKicker extends TimerTask {
        private final Channel chan;
        private final User user;
        private final String nick;
        private final Session source;

        public NickKicker(IRCJoinEvent ije) {
            this.chan = ije.getChannel();
            this.user = ije.getUser();
            this.nick = user.getNick();
            this.source = ije.getSource();
        }

        public void run() {
            if (user.getNick().equals(nick) && chan.hasUser(user))
                source.sendLine("KICK " + chan.getName() + " " + nick + " :Change your nick!");
        }
    }

    public enum UserType {
        Normal,
        Voice,
        Halfop,
        Op,
        Protect,
        Owner;

        public static UserType fromChar(char c) {
            switch (c) {
                case '~':
                    return Owner;
                case '&':
                    return Protect;
                case '@':
                    return Op;
                case '%':
                    return Halfop;
                case '+':
                    return Voice;
                default:
                    return Normal;
            }
        }
    }
}