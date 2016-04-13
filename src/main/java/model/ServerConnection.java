package model;

import controller.game.GameListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class ServerConnection.
 */
public class ServerConnection {
    private static final Logger LOGGER = LogManager.getLogger(ServerConnection.class);
    private static final String GAMELIST = "gamelist";
    private static final String PLAYERLIST = "playerlist";
    private final Socket socket;
    private ServerResponseReader reader;
    private PrintWriter writer;

    /**
     * Creates a connection to a server with the specified IP address and port.
     *
     * @param ip   The IP address of the server
     * @param port The port of the game server
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public ServerConnection(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        reader = new ServerResponseReader(socket);
        new Thread(reader).start();
        writer = new PrintWriter(socket.getOutputStream());
    }

    /**
     * Add a new gamelistener.
     *
     * @param listener The listener which wishes to be notified of game events
     */
    public void addGameListener(GameListener listener) {
        reader.addGameListener(listener);
    }

    /**
     * Writes a line to the server directly.
     *
     * @param line the line
     * @return true, if successful
     */
    public boolean write(String line) {
        writer.println(line);
        writer.flush();
        List<String> result = reader.read(1);
        return !(result.size() == 1 && result.get(0) == null) && result.size() == 1 && result.get(0).startsWith("OK");
    }

    /**
     * Returns a list of a certain type.
     *
     * @param type The type that needs to be returned
     * @return the list
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
            LOGGER.trace("Error receiving", e);
            return new ArrayList<>();
        } catch (JSONException e) {
            LOGGER.trace("Error receiving", e);
            return new ArrayList<>();
        }
    }

    /**
     * Returns the list of games available.
     *
     * @return the gamelist
     */
    public List<String> getGamelist() {
        return get(GAMELIST);
    }

    /**
     * Returns the list of players currently logged in.
     *
     * @return the playerlist
     */
    public List<String> getPlayerlist() {
        return get(PLAYERLIST);
    }

    /**
     * Accepts the challenge with the specified challengenumber.
     *
     * @param challengeNumber the challenge number
     * @return true, if successful
     */
    public boolean acceptChallenge(String challengeNumber) {
        return write("challenge accept " + challengeNumber);
    }

    /**
     * Sends to the server that you are playing a move.
     *
     * @param s The move that you are playing
     * @return true, if successful
     */
    public boolean move(String s) {
        return write("move " + s);
    }

    /**
     * Logs the user out and closes the connection.
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
     * Logs the user in with the specified username.
     *
     * @param username the username
     * @return true, if successful
     */
    public boolean login(String username) {
        return write("login " + username);
    }

    /**
     * Subscribes the player to the specified gametype.
     *
     * @param gametype the gametype
     * @return true, if successful
     */
    public boolean subscribe(String gametype) {
        return write("subscribe " + gametype);
    }

    /**
     * Allows the player to challenge another player with the specified gametype.
     *
     * @param player the player
     * @param gametype the gametype
     * @param turnTime the turn time
     * @return true, if successful
     */
    public boolean challenge(String player, String gametype, String turnTime) {
        return write("challenge \"" + player + "\" \"" + gametype + "\" " + turnTime);
    }

    /**
     * Forfeits the current game.
     *
     * @return true, if successful
     */
    public boolean forfeit() {
        return write("forfeit");
    }

    /**
     * Checks if is connected.
     *
     * @return true, if is connected
     */
    public boolean isConnected() {
        return socket.isConnected() && !socket.isClosed();
    }
}
