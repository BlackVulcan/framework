package main.java.model;

import main.java.controller.GameListener;
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

    private static final String GAMELIST = "gamelist";
    private static final String PLAYERLIST = "playerlist";

    private ServerResponseReader reader;
    private PrintWriter writer;
    private final Socket socket;

    public ServerConnection(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        reader = new ServerResponseReader(socket);
        new Thread(reader).start();
        writer = new PrintWriter(socket.getOutputStream());
    }

    public void addGameListener(GameListener listener){
        reader.addGameListener(listener);
    }

    public boolean write(String line){
        writer.println(line);
        writer.flush();
        List<String> result = reader.read(1);
        return result.size() == 1 && result.get(0).startsWith("OK");
    }

    private List<String> get(String type){
        writer.println("get " + type);
        writer.flush();
        List<String> result = reader.read(2);
        JSONArray array = new JSONArray(result.get(1).substring(5 + type.length())); // We first recieve an OK before the playerlist arrives
        List<String> returnList = new ArrayList<>(array.length());
        for (int i = 0; i < array.length(); i++) {
            returnList.add(array.getString(i));
        }
        return returnList;
    }

    public List<String> getGamelist() {
        return get(GAMELIST);
    }

    public List<String> getPlayerlist() {
        return get(PLAYERLIST);
    }

    public boolean acceptChallenge(String challengeNumber) {
        return write("challenge accept " + challengeNumber);
    }

    public boolean move(String s) {
        return write("move " + s);
    }

    /**
     * Logout
     */
    public void close(){
        reader.stop();
        write("logout");
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean login(String username) {
        return write("login " + username);
    }

    public boolean subscribe(String gametype) {
        return write("subscribe " + gametype);
    }

    public boolean challenge(String player, String gametype) {
        return write("challenge \"" + player + "\" \"" + gametype + "\"");
    }

    public boolean forfeit() {
        return write("forfeit");
    }


}
