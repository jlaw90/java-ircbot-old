package net.newbiehacker.commodorej;

import net.newbiehacker.commodorej.event.ErrorHandler;
import net.newbiehacker.commodorej.event.IRCEvent;
import net.newbiehacker.commodorej.event.IRCEventListener;

import java.util.*;

/**
 * The ConnectionManager allows creations of sessions to a server and ways to manipulate them.
 *
 * @author newbiehacker
 */
public final class ConnectionManager {
    final List<ErrorHandler> errHandlers;
    private final List<Session> sessions;
    private final EventQueue eventQueue;
    private Thread qThread;

    /**
     * Constructs a new ConnectionManager which will have its own event queue and sessions
     */
    public ConnectionManager() {
        errHandlers = new ArrayList<ErrorHandler>();
        sessions = new ArrayList<Session>();
        eventQueue = new EventQueue(this);
    }

    void fireEvent(IRCEvent e) {
        eventQueue.push(e);
        if (!eventQueue.running) {
            qThread = new Thread(eventQueue);
            qThread.setPriority(1);
            qThread.start();
        }
    }

    void fireError(Throwable t) {
        synchronized (errHandlers) {
            for (ErrorHandler eh : errHandlers)
                try {
                    eh.onError(t);
                } catch (Throwable t1) {
                    fireError(t1);
                }
        }
    }

    void removeSession(Session s) {
        sessions.remove(s);
    }

    /**
     * Returns a read-only list of our sessions
     *
     * @return a read-only list of our sessions
     */
    public List<Session> getSessions() {
        synchronized (sessions) {
            return Collections.unmodifiableList(sessions);
        }
    }

    /**
     * Attempts to connect to the specified server and login with the specified details<br />
     * The new Session is returned if successful
     *
     * @param host  the host we wish to connect to
     * @param port  the port that the irc server is bound to on the host
     * @param nick  the nickname we want to use
     * @param login the login we want to use
     * @param name  the full name we want to use
     * @return the newly created Session if we connect successfully
     */
    public Session connect(String host, int port, String nick, String login, String name) {
        Session s = new Session(this, host.toLowerCase(), port, nick, login, name);
        s.connect();
        synchronized (sessions) {
            sessions.add(s);
        }
        return s;
    }

    /**
     * Returns the session corresponding to the specified host (if any)
     *
     * @param host the host the session is connected to
     * @return the session that is connected to the specified host
     */
    public Session getSession(String host) {
        host = host.toLowerCase();
        synchronized (sessions) {
            for (Session s : sessions)
                if (s.getHost().equals(host))
                    return s;
        }
        return null;
    }

    /**
     * Registers the specified IRCEventListener to be notified of new events
     *
     * @param iel the IRCEventListener we wish to be notified of new events
     */
    public void registerListener(IRCEventListener iel) {
        eventQueue.addEventListener(iel);
    }

    /**
     * Unregisters the specified IRCEventListener so it will not be notified of new events
     *
     * @param iel the IRCEventListener we do not wish to be notified of events any more
     */
    public void unregisterListener(IRCEventListener iel) {
        eventQueue.removeListener(iel);
    }

    /**
     * Registers the specified ErrorHandler so that it will be notified when the library encounbters an error
     *
     * @param eh the ErrorHandler we wish to notify when the library encounters an error
     */
    public void registerErrorHandler(ErrorHandler eh) {
        synchronized (errHandlers) {
            errHandlers.add(eh);
        }
    }

    /**
     * Unregisters the specified ErrorHandler so that it will no longer be notified when the library encounters an error
     *
     * @param eh the ErrorHandler we no longer wish to be notified of errors
     */
    public void unregisterErrorHandler(ErrorHandler eh) {
        synchronized (errHandlers) {
            errHandlers.remove(eh);
        }
    }

    /**
     * Attempts to close down our event queue and close all open sessions
     */
    public void stop() {
        synchronized (qThread) {
            eventQueue.running = false;
            try {
                qThread.join();
            } catch (InterruptedException e) {
                fireError(e);
            }
            for (Session s : sessions) {
                s.close();
            }
            sessions.clear();
        }
    }

    private static final class EventQueue implements Runnable {
        private final ConnectionManager parent;
        private final Object lock;
        private final Queue<IRCEvent> queue;
        private final List<IRCEventListener> listeners;
        boolean running;

        EventQueue(ConnectionManager parent) {
            this.parent = parent;
            queue = new LinkedList<IRCEvent>();
            listeners = new ArrayList<IRCEventListener>();
            lock = new Object();
        }

        void push(IRCEvent e) {
            if(e == null)
                return;
            synchronized (lock) {
                queue.add(e);
                lock.notifyAll();
            }
        }

        void addEventListener(IRCEventListener iel) {
            synchronized (lock) {
                listeners.add(iel);
            }
        }

        void removeListener(IRCEventListener iel) {
            synchronized (lock) {
                listeners.remove(iel);
            }
        }

        public void run() {
            running = true;
            try {
                while (running) {
                    synchronized (lock) {
                        while (queue.size() > 0) {
                            for (IRCEventListener iel : listeners)
                                try {
                                    iel.onIRCEvent(queue.poll());
                                } catch (Throwable t) {
                                    parent.fireError(t);
                                }
                        }
                    }
                    Thread.sleep(1);
                }
            } catch (Throwable t) {
                parent.fireError(t);
                run();
            }
        }
    }
}