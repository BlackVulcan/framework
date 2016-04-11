package model;

import controller.game.GameModuleLoader;
import nl.abstractteam.gamemodule.ClientAbstractGameModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Model {
    public static final int TURN_SWITCHED = 1;
    public static final int SERVER_CONNECTION_SET = 2;
    public static final int GAME_CHANGED = 3;
    public static final int GAME_DRAW = 4;
    public static final int GAME_WIN = 5;
    public static final int GAME_LOSS = 6;
    public static final int NEW_CHALLENGE = 7;
    public static final int CANCEL_CHALLENGE = 8;
    public static final int TURN_MESSAGE_CHANGED = 9;
    public static final String GAME_IS_CLOSED = "game is closed";
    public static final String GAMEMODULE_SET = "gamemodule is set";
    public static final String OPPONENT_SET = "opponent is set";
    public static final String CHALLENGE_GAME_TYPE = "gametype";
    public static final String CHALLENGE_PLAYER = "player";
    public static final String CHALLENGE_GAME_NUMBER = "gamenumber";
    private static final Logger LOGGER = LogManager.getLogger(Model.class);
    private ArrayList<ActionListener> actionListenerList = new ArrayList<>();
    private ClientAbstractGameModule gameModule;
    private String clientName;
    private String opponent;
    private String serverAddress;
    private String turnMessage;
    private int gameResult = 0;
    private boolean myTurn = false;
    private boolean playWithAI = false;
    private boolean playingGame = false;
    private GameModuleLoader gameModuleLoader;
    private ArrayList<String> challengeGameTypes;
    private ArrayList<String> challengePlayers;
    private ArrayList<String> challengeNumbers;
    private HashMap<String, String[]> gameSides = new HashMap<>();
    private HashMap<String, String> chosenGameSide = new HashMap<>();
    private Random random = new Random();

    public Model() {
        challengeGameTypes = new ArrayList<>();
        challengePlayers = new ArrayList<>();
        challengeNumbers = new ArrayList<>();

        String[] sides = {"X", "O"};
        gameSides.put("Tic-tac-toe", sides);
    }

    public void addActionListener(ActionListener actionListener) {
        actionListenerList.add(actionListener);
    }

    public void notifyListeners() {
        processEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
    }

    private void processEvent(ActionEvent e) {
        for (ActionListener l : actionListenerList)
            l.actionPerformed(e);
    }

    public String[] getGameSides(String gameType) {
        return gameSides.get(gameType);
    }

    public void setGameSides(String gameType, String[] sides) {
        gameSides.put(gameType, sides);
    }

    public String getChosenGameSides(String gameType) {
        if (chosenGameSide.get(gameType) == null) {
            if (random.nextInt(3000) % 2 > 0)
                setChosenGameSides(gameType, getGameSides(gameType)[0]);
            else
                setChosenGameSides(gameType, getGameSides(gameType)[1]);
        }
        return chosenGameSide.get(gameType);
    }

    public void setChosenGameSides(String gameType, String side) {
        chosenGameSide.put(gameType, side);
    }

    public GameModuleLoader getGameModuleLoader() {
        return this.gameModuleLoader;
    }

    public void setGameModuleLoader(GameModuleLoader gameModuleLoader) {
        this.gameModuleLoader = gameModuleLoader;
    }

    public ClientAbstractGameModule getGameModule() {
        return gameModule;
    }

    public void setGameModule(ClientAbstractGameModule gameModule) {
        LOGGER.trace("Setting game module to {}.", gameModule.getClass().getName());
        this.gameModule = gameModule;
        processEvent(new ActionEvent(this, GAME_CHANGED, GAMEMODULE_SET));
    }

    public boolean getPlayingGame() {
        return playingGame;
    }

    public void setPlayingGame(boolean playingGame) {
        this.playingGame = playingGame;
        if (!playingGame)
            processEvent(new ActionEvent(this, GAME_CHANGED, GAME_IS_CLOSED));
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getOpponent() {
        return opponent;
    }

    public void setOpponent(String opponent) {
        this.opponent = opponent;
        processEvent(new ActionEvent(this, GAME_CHANGED, OPPONENT_SET));
    }

    public String getServerAddress() {
        return this.serverAddress;
    }

    public void setServerAddress(String serverAdress) {
        this.serverAddress = serverAdress;
        processEvent(new ActionEvent(this, SERVER_CONNECTION_SET, null));
    }

    public boolean getTurn() {
        return this.myTurn;
    }

    public void setTurn(String player) {
        LOGGER.trace("Setting current turn to: {}.", player);
        this.myTurn = player.equals(this.clientName);
        processEvent(new ActionEvent(this, TURN_SWITCHED, null));
    }

    public void putGameModulePieces(String gameType, String[] pieces) {
        gameSides.put(gameType, pieces);
    }

    public String getTurnMessage() {
        return turnMessage;
    }

    public void setTurnMessage(String message) {
        turnMessage = message;
        processEvent(new ActionEvent(this, TURN_MESSAGE_CHANGED, null));
    }

    public int getGameResult() {
        return gameResult;
    }

    public void setGameResult(int gameResult) {
        this.gameResult = gameResult;
        processEvent(new ActionEvent(this, gameResult, null));
    }

    public boolean getPlayWithAI() {
        return playWithAI;
    }

    public void setPlayWithAI(boolean playWithAI) {
        this.playWithAI = playWithAI;
    }

    public void setNewChallenge(String gameType, String player, String challengeNumber) {
        challengeGameTypes.add(gameType);
        challengePlayers.add(player);
        challengeNumbers.add(challengeNumber);
        processEvent(new ActionEvent(this, NEW_CHALLENGE, Integer.toString(challengeGameTypes.size() - 1)));
    }

    public HashMap<String, String> getChallenge(int index) {
        HashMap<String, String> challenge = new HashMap<>();
        challenge.put(CHALLENGE_GAME_TYPE, challengeGameTypes.get(index));
        challenge.put(CHALLENGE_PLAYER, challengePlayers.get(index));
        challenge.put(CHALLENGE_GAME_NUMBER, challengeNumbers.get(index));
        return challenge;
    }

    public void cancelChallenge(String challengeNumber) {
        processEvent(new ActionEvent(this, CANCEL_CHALLENGE, challengeNumber));
    }
}
