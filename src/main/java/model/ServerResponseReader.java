package model;

import controller.game.GameListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by Jules on 29-3-2016.
 * <p>
 * The responseReader reads all input from the server, it also parses all input, if it has found something useful he notifies all listeners
 * If the response doesn't turn out to be useful he adds it to a queue. Lines can be gotten from the queue.
 * <p>
 * Wij gebruiken voor het parsen van de JSON een JSON parser van het internet.
 * Deze parser is gemaakt door Douglas Crockford en de parser is te vinden op http://mvnrepository.com/artifact/org.json/json/20160212.
 */
public class ServerResponseReader implements Runnable {
    /**
     * Constants for protocol communication
     */
    private static final String GAME_PREFIX = "SVR GAME ";
    private static final String MATCH_PREFIX = "MATCH ";
    private static final String YOURTURN_PREFIX = "YOURTURN ";
    private static final String MOVE_PREFIX = "MOVE ";
    private static final String CHALLENGE_PREFIX = "CHALLENGE ";
    private static final String WIN_PREFIX = "WIN ";
    private static final String LOSS_PREFIX = "LOSS ";
    private static final String DRAW_PREFIX = "DRAW ";
    private static final Logger LOGGER = LogManager.getLogger(ServerResponseReader.class);
    private static final String PLAYERTOMOVE_VARNAME = "PLAYERTOMOVE";
    private static final String GAMETYPE_VARNAME = "GAMETYPE";
    private static final String OPPONENT_VARNAME = "OPPONENT";
    private static final String TURNMESSAGE_VARNAME = "TURNMESSAGE";
    private static final String PLAYER_VARNAME = "PLAYER";
    private static final String MOVE_VARNAME = "MOVE";
    private static final String DETAILS_VARNAME = "DETAILS";
    private static final String CHALLENGER_VARNAME = "CHALLENGER";
    private static final String CHALLENGENUMBER_VARNAME = "CHALLENGENUMBER";
    private static final String PLAYERONESCORE_VARNAME = "PLAYERONESCORE";
    private static final String PLAYERTWOSCORE_VARNAME = "PLAYERTWOSCORE";
    private static final String COMMENT_VARNAME = "COMMENT";
    private static final String CANCELLED_PREFIX = "CANCELLED ";
    private static final String CHALLENGETURNTIME_VARNAME = "TURNTIME";
    private final Object stopLock = new Object();
    /**
     * All server responses which should not be sent to listeners (Like a game has started for example, mostly this queue consists of
     * OK's and ERR's). When a line from this queue is read it is also deleted.
     */
    private final Queue<String> responseBuffer = new LinkedList<>();
    /**
     * A boolean indicating if this thread should run
     */
    private boolean running = true;
    /**
     * All gameListeners which will be notified of events
     */
    private ArrayList<GameListener> listeners = new ArrayList<>();
    /**
     * A reader to read the input stream.
     */
    private BufferedReader reader;

    /**
     * @param socket The socket on which's inputStream to read.
     *
     * @throws IOException
     */
    public ServerResponseReader(Socket socket) throws IOException {
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    /**
     * This method can be started and will read input of the socket. It has to be started in a new Thread, because it will run till
     * stop is called.
     */
    @Override
    public void run() {
        while (running) {
            try {
                String in = reader.readLine();

                if (!parse(in)) {
                    synchronized (responseBuffer) {
                        if (in == null) {
                            continue;
                        }

                        if (in.toLowerCase().startsWith("ok") || in.toLowerCase().startsWith("err") || in.toLowerCase().startsWith("svr gamelist") || in.toLowerCase().startsWith("svr playerlist")) {
                            responseBuffer.add(in);
                            responseBuffer.notifyAll();
                        }
                    }
                }
            } catch (IOException | JSONException e) {
                LOGGER.error("Error when receiving data.", e);
            }
        }
        synchronized (stopLock) {
            stopLock.notifyAll();
        }
    }


    /**
     * Parse a String read from the inputStream
     *
     * @param s The string to parse.
     *
     * @return true if a Line containing information for listeners has been found, false otherwise
     */
    private boolean parse(String s) {
        if (s == null) {
            return false;
        }

        if ("Strategic Game Server [Version 1.0]".equals(s) || s.startsWith("(C) Copyright 2009") || s.startsWith("SVR MESSAGE")) {
            return true;
        }

        s = s.trim();
        LOGGER.trace(s);

        if (!s.startsWith(GAME_PREFIX)) {
            return false;
        }
        s = s.substring(GAME_PREFIX.length());

        if (s.startsWith(MATCH_PREFIX)) {
            JSONObject jsonObject = new JSONObject(s.substring(MATCH_PREFIX.length()));

            String playerMove = jsonObject.getString(PLAYERTOMOVE_VARNAME);
            String gameType = jsonObject.getString(GAMETYPE_VARNAME);
            String opponent = jsonObject.getString(OPPONENT_VARNAME);

            for (GameListener gameListener : listeners) {
                gameListener.match(playerMove, gameType, opponent);
            }
        } else if (s.startsWith(YOURTURN_PREFIX)) {
            JSONObject jsonObject = new JSONObject(s.substring(YOURTURN_PREFIX.length()));

            for (GameListener gameListener : listeners) {
                try {
                    gameListener.yourTurn(jsonObject.getString(TURNMESSAGE_VARNAME));
                } catch (JSONException e) {
                    gameListener.yourTurn("");
                }
            }
        } else if (s.startsWith(MOVE_PREFIX)) {
            JSONObject jsonObject = new JSONObject(s.substring(MOVE_PREFIX.length()));

            for (GameListener gameListener : listeners) {
                gameListener.move(jsonObject.getString(PLAYER_VARNAME), jsonObject.getString(MOVE_VARNAME), jsonObject.getString(DETAILS_VARNAME));
            }
        } else if (s.startsWith(CHALLENGE_PREFIX)) {
            if (s.substring(CHALLENGE_PREFIX.length()).startsWith(CANCELLED_PREFIX)) {
                JSONObject jsonObject = new JSONObject(s.substring(CHALLENGE_PREFIX.length() + CANCELLED_PREFIX.length()));

                for (GameListener gameListener : listeners) {
                    gameListener.challengeCancelled(jsonObject.getString(CHALLENGENUMBER_VARNAME));
                }

                return true;
            }
            JSONObject jsonObject = new JSONObject(s.substring(CHALLENGE_PREFIX.length()));

            String challenger = jsonObject.getString(CHALLENGER_VARNAME);
            String challengeNumber = jsonObject.getString(CHALLENGENUMBER_VARNAME);
            String challengeGameType = jsonObject.getString(GAMETYPE_VARNAME);
            String challengeTurnTime = jsonObject.getString(CHALLENGETURNTIME_VARNAME);

            for (GameListener gameListener : listeners) {
                gameListener.challenge(challenger, challengeNumber, challengeGameType, challengeTurnTime);
            }
        } else if (s.startsWith(WIN_PREFIX)) {
            JSONObject jsonObject = new JSONObject(s.substring(WIN_PREFIX.length()));

            for (GameListener gameListener : listeners) {
                gameListener.win(jsonObject.getString(PLAYERONESCORE_VARNAME), jsonObject.getString(PLAYERTWOSCORE_VARNAME), jsonObject.getString(COMMENT_VARNAME));
            }
        } else if (s.startsWith(LOSS_PREFIX)) {
            JSONObject jsonObject = new JSONObject(s.substring(LOSS_PREFIX.length()));

            for (GameListener gameListener : listeners) {
                gameListener.loss(jsonObject.getString(PLAYERONESCORE_VARNAME), jsonObject.getString(PLAYERTWOSCORE_VARNAME), jsonObject.getString(COMMENT_VARNAME));
            }
        } else if (s.startsWith(DRAW_PREFIX)) {
            JSONObject jsonObject = new JSONObject(s.substring(DRAW_PREFIX.length()));

            for (GameListener gameListener : listeners) {
                gameListener.draw(jsonObject.getString(PLAYERONESCORE_VARNAME), jsonObject.getString(PLAYERTWOSCORE_VARNAME), jsonObject.getString(COMMENT_VARNAME));
            }
        }
        return true;
    }

    /**
     * Add a new gamelistener
     *
     * @param listener The listener which wishes to be notified of game events
     */
    public void addGameListener(GameListener listener) {
        listeners.add(listener);
    }

    /**
     * Stop the thread reading from the server
     */
    public void stop() {
        synchronized (stopLock) {
            running = false;
            try {
                stopLock.wait();
            } catch (InterruptedException e) {
                LOGGER.warn("Interrupt exception");
            }
        }
    }

    /**
     * Read i lines from the buffer (Server responses which are not sent to listeners). This method blocks until i lines have been
     * gathered.
     *
     * @param i The amount of lines to read
     *
     * @return The lines which have been read
     */
    public List<String> read(int i) {
        List<String> result = new ArrayList<>(i);
        int tries = 0;
        for (int j = 0; j < i; j++) {
            synchronized (responseBuffer) {
                while (responseBuffer.isEmpty()) {
                    try {
                        responseBuffer.wait(500);
                        if (!result.isEmpty() && responseBuffer.isEmpty() && ++tries > 3) {
                            return result;
                        }
                    } catch (InterruptedException e) {
                        LOGGER.warn("Interrupt exception");
                    }
                }
                tries = 0;
                result.add(responseBuffer.remove());
            }
        }

        return result;
    }
}
