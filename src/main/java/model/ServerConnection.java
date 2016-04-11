package model;

import controller.game.GameListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jules on 29-3-2016.
 */
public class ServerConnection {
    private static final Logger LOGGER = LogManager.getLogger(ServerConnection.class);
    private static final String GAMELIST = "gamelist";
    private static final String PLAYERLIST = "playerlist";
    private final Socket socket;
    private ServerResponseReader reader;
    private PrintWriter writer;

    /**
     * Creates a connection to a server with the specified IP address and port
     *
     * @param ip   The IP address of the server
     * @param port The port of the game server
     *
     * @throws IOException
     */
    public ServerConnection(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        reader = new ServerResponseReader(socket);
        new Thread(reader).start();
        writer = new PrintWriter(socket.getOutputStream());
    }

    /**
     * Add a new gamelistener
     *
     * @param listener The listener which wishes to be notified of game events
     */
    public void addGameListener(GameListener listener) {
        reader.addGameListener(listener);
    }

    /**
     * Writes a line to the server directly
     *
     * @param line
     *
     * @return
     */
    public boolean write(String line) {
        writer.println(line);
        writer.flush();
        List<String> result = reader.read(1);
        return !(result.size() == 1 && result.get(0) == null) && result.size() == 1 && result.get(0).startsWith("OK");
    }

    /**
     * Returns a list of a certain type
     *
     * @param type The type that needs to be returned
     *
     * @return
     */
    private List<String> get(String type) {
        writer.println("get " + type);
        writer.flush();
        List<String> result = reader.read(2);
        if (result.size() != 2 || result.get(1) == null) {
            return new ArrayList<>();
        }

        try {
            JSONArray array = new JSONArray(result.get(1).substring(5 + type.length())); // We first recieve an OK before the playerlist arrives
            List<String> returnList = new ArrayList<>(array.length());
            for (int i = 0; i < array.length(); i++) {
                returnList.add(array.getString(i));
            }
            return returnList;
        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Returns the list of games available.
     *
     * @return
     */
    public List<String> getGamelist() {
        return get(GAMELIST);
    }

    /**
     * Returns the list of players currently logged in
     *
     * @return
     */
    public List<String> getPlayerlist() {
        return get(PLAYERLIST);
    }

    /**
     * Accepts the challenge with the specified challengenumber
     *
     * @param challengeNumber
     *
     * @return
     */
    public boolean acceptChallenge(String challengeNumber) {
        return write("challenge accept " + challengeNumber);
    }

    /**
     * Sends to the server that you are playing a move
     *
     * @param s The move that you are playing
     *
     * @return
     */
    public boolean move(String s) {
        return write("move " + s);
    }

    /**
     * Logs the user out and closes the connection
     */
    public void close() {
        writer.println("logout");
        writer.flush();
        try {
            socket.close();
        } catch (IOException e) {
            LOGGER.warn("IOException", e);
        }
        reader.stop();
    }

    /**
     * Logs the user in with the specified username
     *
     * @param username
     *
     * @return
     */
    public boolean login(String username) {
        return write("login " + username);
    }

    /**
     * Subscribes the player to the specified gametype
     *
     * @param gametype
     *
     * @return
     */
    public boolean subscribe(String gametype) {
        return write("subscribe " + gametype);
    }

    /**
     * Allows the player to challenge another player with the specified gametype
     *
     * @param player
     * @param gametype
     *
     * @return
     */
    public boolean challenge(String player, String gametype) {
        return write("challenge \"" + player + "\" \"" + gametype + "\"");
    }

    /**
     * Forfeits the current game
     *
     * @return
     */
    public boolean forfeit() {
        return write("forfeit");
    }

    public boolean isConnected() {
        return socket.isConnected() && !socket.isClosed();
    }
}
