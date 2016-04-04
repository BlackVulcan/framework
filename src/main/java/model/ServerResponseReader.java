package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import controller.game.GameListener;
import org.json.*;

/**
 * Created by Jules on 29-3-2016.
 *
 * The responseReader reads all input from the server, it also parses all input, if it has found something useful he notifies all listeners
 * If the response doesn't turn out to be useful he adds it to a queue. Lines can be gotten from the queue.
 *
 * Wij gebruiken voor het parsen van de JSON een JSON parser van het internet.
 * Deze parser is gemaakt door Douglas Crockford en de parser is te vinden op http://mvnrepository.com/artifact/org.json/json/20160212.
 */
public class ServerResponseReader implements Runnable {

    /**
     * Constants for protocol communication
     */
    public static final String GAME_PREFIX = "SVR GAME ";

    public static final String MATCH_PREFIX = "MATCH ", YOURTURN_PREFIX = "YOURTURN ", MOVE_PREFIX = "MOVE ", CHALLENGE_PREFIX = "CHALLENGE ",
            WIN_PREFIX = "WIN ", LOSS_PREFIX = "LOSS ", DRAW_PREFIX = "DRAW ";

    private static final String PLAYERTOMOVE_VARNAME = "PLAYERTOMOVE", GAMETYPE_VARNAME = "GAMETYPE", OPPONENT_VARNAME = "OPPONENT";
    private static final String TURNMESSAGE_VARNAME = "TURNMESSAGE", PLAYER_VARNAME = "PLAYER", MOVE_VARNAME = "MOVE", DETAILS_VARNAME = "DETAILS";
    private static final String CHALLENGER_VARNAME = "CHALLENGER", CHALLENGENUMBER_VARNAME = "CHALLENGENUMBER";
    private static final String PLAYERONESCORE_VARNAME = "PLAYERONESCORE", PLAYERTWOSCORE_VARNAME = "PLAYERTWOSCORE", COMMENT_VARNAME = "COMMENT";
    public static final String CANCELLED_PREFIX = "CANCELLED ";
    private Object stopLock = new Object();

    /**
     * All gameListeners which will be notified of events
     */
    private ArrayList<GameListener> listeners = new ArrayList<>();

    /**
     * All server responses which should not be sent to listeners (Like a game has started for example, mostly this queue consists of
     * OK's and ERR's). When a line from this queue is read it is also deleted.
     */
    private Queue<String> responseBuffer = new LinkedList<>();
    /**
     * A reader to read the input stream.
     */
    private BufferedReader reader;

    /**
     * A boolean indicating if this thread should run
     */
    boolean running = true;

    /**
     *
     * @param socket The socket on which's inputStream to read.
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
                    synchronized (responseBuffer){
                        responseBuffer.add(in);
                        responseBuffer.notifyAll();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        synchronized (stopLock){
            stopLock.notifyAll();
        }
    }


    /**
     * Parse a String read from the inputStream
     * @param s The string to parse.
     * @return true if a Line containing information for listeners has been found, false otherwise
     */
    private boolean parse(String s) {
    	if(s == null)
    		return false;
    	
        if (s.equals("Strategic Game Server [Version 1.0]") || s.equals("(C) Copyright 2009 Hanze Hogeschool Groningen")) {
            return true;
        }

        s = s.trim();

        if (s.startsWith(GAME_PREFIX)) {
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
                    gameListener.yourTurn(jsonObject.getString(TURNMESSAGE_VARNAME));
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
                
                for (GameListener gameListener : listeners) {
                    gameListener.challenge(challenger, challengeNumber, challengeGameType);
                }

                
            } else if (s.startsWith(WIN_PREFIX)) {
                JSONObject jsonObject = new JSONObject(s.substring(WIN_PREFIX.length()));

                for (GameListener gameListener : listeners) {
                    gameListener.win(jsonObject.getString(PLAYERONESCORE_VARNAME), jsonObject.getString(PLAYERTWOSCORE_VARNAME),jsonObject.getString(COMMENT_VARNAME));
                }
            } else if (s.startsWith(LOSS_PREFIX)) {
                JSONObject jsonObject = new JSONObject(s.substring(LOSS_PREFIX.length()));

                for (GameListener gameListener : listeners) {
                    gameListener.loss(jsonObject.getString(PLAYERONESCORE_VARNAME), jsonObject.getString(PLAYERTWOSCORE_VARNAME),jsonObject.getString(COMMENT_VARNAME));
                }
            } else if (s.startsWith(DRAW_PREFIX)) {
                JSONObject jsonObject = new JSONObject(s.substring(DRAW_PREFIX.length()));

                for (GameListener gameListener : listeners) {
                    gameListener.draw(jsonObject.getString(PLAYERONESCORE_VARNAME), jsonObject.getString(PLAYERTWOSCORE_VARNAME),jsonObject.getString(COMMENT_VARNAME));
                }
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * Add a new gamelistener
     * @param listener The listener which wishes to be notified of game events
     */
    public void addGameListener(GameListener listener) {
        listeners.add(listener);
    }

    /**
     * Stop the thread reading from the server
     */
    public void stop() {
        synchronized (stopLock){
            running = false;
            try {
                stopLock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Read i lines from the buffer (Server responses which are not sent to listeners). This method blocks until i lines have been
     * gathered.
     * @param i The amount of lines to read
     * @return The lines which have been read
     */
    public List<String> read(int i) {
        List<String> result = new ArrayList<>(i);
        for (int j = 0; j < i; j++) {
            synchronized (responseBuffer){
                while(responseBuffer.size()==0){
                    try {
                        responseBuffer.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                result.add(responseBuffer.remove());
            }
        }

        return result;
    }
}
