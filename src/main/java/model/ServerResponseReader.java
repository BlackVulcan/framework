package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import controller.GameListener;
import org.json.*;

/**
 * Created by Jules on 29-3-2016.
 * Wij gebruiken voor het parsen van de JSON een JSON parser van het internet.
 * Deze parser is gemaakt door Douglas Crockford en de parser is te vinden op http://mvnrepository.com/artifact/org.json/json/20160212.
 */
public class ServerResponseReader implements Runnable {

    public static final String GAME_PREFIX = "SVR GAME ";

    public static final String MATCH_PREFIX = "MATCH ", YOURTURN_PREFIX = "YOURTURN ", MOVE_PREFIX = "MOVE ", CHALLENGE_PREFIX = "CHALLENGE ",
            WIN_PREFIX = "WIN ", LOSS_PREFIX = "LOSS ", DRAW_PREFIX = "DRAW ";

    private static final String PLAYERTOMOVE_VARNAME = "PLAYERTOMOVE", GAMETYPE_VARNAME = "GAMETYPE", OPPONENT_VARNAME = "OPPONENT";
    private static final String TURNMESSAGE_VARNAME = "TURNMESSAGE", PLAYER_VARNAME = "PLAYER", MOVE_VARNAME = "MOVE", DETAILS_VARNAME = "DETAILS";
    private static final String CHALLENGER_VARNAME = "CHALLENGER", CHALLENGENUMBER_VARNAME = "CHALLENGENUMBER";
    private static final String PLAYERONESCORE_VARNAME = "PLAYERONESCORE", PLAYERTWOSCORE_VARNAME = "PLAYERTWOSCORE", COMMENT_VARNAME = "COMMENT";
    public static final String CANCELLED_PREFIX = "CANCELLED ";

    private ArrayList<GameListener> listeners = new ArrayList<>();

    private Queue<String> responseBuffer = new LinkedList<>();
    private BufferedReader reader;

    boolean running = true;

    public ServerResponseReader(Socket socket) throws IOException {
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

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
    }



    private boolean parse(String s) {
        if (s.equals("Strategic Game Server [Version 1.0]") || s.equals("(C) Copyright 2009 Hanze Hogeschool Groningen")) {
            return true;
        }

        s = s.trim();

        if (s.startsWith(GAME_PREFIX)) {
            s = s.substring(GAME_PREFIX.length());

            if (s.startsWith(MATCH_PREFIX)) {
                JSONObject jsonObject = new JSONObject(s.substring(MATCH_PREFIX.length()));

                for (GameListener gameListener : listeners) {
                    gameListener.match(jsonObject.getString(PLAYERTOMOVE_VARNAME), jsonObject.getString(GAMETYPE_VARNAME), jsonObject.getString(OPPONENT_VARNAME));
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

                for (GameListener gameListener : listeners) {
                    gameListener.challenge(jsonObject.getString(CHALLENGER_VARNAME), jsonObject.getString(CHALLENGENUMBER_VARNAME), jsonObject.getString(GAMETYPE_VARNAME));
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

    public void addGameListener(GameListener listener) {
        listeners.add(listener);
    }

    public void stop() {
        running = false;
    }

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
