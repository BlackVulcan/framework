package model;

import nl.abstractteam.gamemodule.ClientAbstractGameModule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * The Class Model.
 */
public class Model {
    public static final int TURN_SWITCHED = 1;
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
    public static final String CHALLENGE_TURN_TIME = "turntime";
    private static final int SERVER_CONNECTION_SET = 2;
    private static final Logger LOGGER = LogManager.getLogger(Model.class);
    private ArrayList<ActionListener> actionListenerList = new ArrayList<>();
    private ClientAbstractGameModule gameModule;
    private String clientName;
    private String opponent;
    private String serverAddress;
    private String serverPort;
    private String turnMessage;
    private String turnTime = "10";
    private int challengeTurnTime = 10;
    private boolean myTurn = false;
    private boolean playWithAI = false;
    private boolean playingGame = false;
    private String playingGameType = "";
    private ArrayList<String> challengeGameTypes;
    private ArrayList<String> challengePlayers;
    private ArrayList<String> challengeNumbers;
    private ArrayList<String> challengeTurnTimes;
    private HashMap<String, String[]> gameSides = new HashMap<>();
    private HashMap<String, String> chosenGameSide = new HashMap<>();
    private Random random = new Random();

    /**
     * Instantiates a new model.
     */
    public Model() {
        challengeGameTypes = new ArrayList<>();
        challengePlayers = new ArrayList<>();
        challengeNumbers = new ArrayList<>();
        challengeTurnTimes = new ArrayList<>();
    }

    /**
     * Adds the action listener.
     *
     * @param actionListener the action listener
     */
    public void addActionListener(ActionListener actionListener) {
        actionListenerList.add(actionListener);
    }

    /**
     * Process event.
     *
     * @param e the e
     */
    private void processEvent(ActionEvent e) {
        for (ActionListener l : actionListenerList)
            l.actionPerformed(e);
    }

    /**
     * Gets the challenge turn time.
     *
     * @return the challenge turn time
     */
    public int getChallengeTurnTime() {
        return challengeTurnTime;
    }

    /**
     * Sets the challenge turn time.
     *
     * @param challengeTurnTime the new challenge turn time
     */
    public void setChallengeTurnTime(String challengeTurnTime) {
        this.challengeTurnTime = challengeTurnTime.matches("\\d+") ? Integer.parseInt(challengeTurnTime) * 1000 : 10000;
    }

    /**
     * Gets the turn time.
     *
     * @return the turn time
     */
    public String getTurnTime() {
        return turnTime;
    }

    /**
     * Sets the turn time.
     *
     * @param turnTime the new turn time
     */
    public void setTurnTime(String turnTime) {
        this.turnTime = turnTime.matches("\\d+") ? turnTime : "10";
    }

    /**
     * Gets the game sides.
     *
     * @param gameType the game type
     * @return the game sides
     */
    public String[] getGameSides(String gameType) {
        return gameSides.get(gameType);
    }

    /**
     * Gets the chosen game sides.
     *
     * @param gameType the game type
     * @return the chosen game sides
     */
    public String getChosenGameSides(String gameType) {
        if (chosenGameSide.get(gameType) == null) {
            if (random.nextInt(3000) % 2 > 0)
                setChosenGameSides(gameType, getGameSides(gameType)[0]);
            else
                setChosenGameSides(gameType, getGameSides(gameType)[1]);
        }
        return chosenGameSide.get(gameType);
    }

    /**
     * Sets the chosen game sides.
     *
     * @param gameType the game type
     * @param side the side
     */
    public void setChosenGameSides(String gameType, String side) {
        chosenGameSide.put(gameType, side);
    }

    /**
     * Gets the game module.
     *
     * @return the game module
     */
    public ClientAbstractGameModule getGameModule() {
        return gameModule;
    }

    /**
     * Sets the game module.
     *
     * @param gameModule the game module
     * @param gameType the game type
     */
    public void setGameModule(ClientAbstractGameModule gameModule, String gameType) {
        LOGGER.trace("Setting game module to {}.", gameModule.getClass().getName());
        this.gameModule = gameModule;
        this.playingGameType = gameType;
        processEvent(new ActionEvent(this, GAME_CHANGED, GAMEMODULE_SET));
    }

    /**
     * Gets the playing game.
     *
     * @return the playing game
     */
    public boolean getPlayingGame() {
        return playingGame;
    }

    /**
     * Sets the playing game.
     *
     * @param playingGame the new playing game
     */
    public void setPlayingGame(boolean playingGame) {
        this.playingGame = playingGame;
        if (!playingGame)
            processEvent(new ActionEvent(this, GAME_CHANGED, GAME_IS_CLOSED));
    }

    /**
     * Gets the playing game type.
     *
     * @return the playing game type
     */
    public String getPlayingGameType() {
        return playingGameType;
    }

    /**
     * Gets the client name.
     *
     * @return the client name
     */
    public String getClientName() {
        return clientName;
    }

    /**
     * Sets the client name.
     *
     * @param clientName the new client name
     */
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    /**
     * Gets the opponent.
     *
     * @return the opponent
     */
    public String getOpponent() {
        return opponent;
    }

    /**
     * Sets the opponent.
     *
     * @param opponent the new opponent
     */
    public void setOpponent(String opponent) {
        this.opponent = opponent;
        processEvent(new ActionEvent(this, GAME_CHANGED, OPPONENT_SET));
    }

    /**
     * Gets the server address.
     *
     * @return the server address
     */
    public String getServerAddress() {
        return this.serverAddress;
    }

    /**
     * Sets the server address.
     *
     * @param serverAddress the server address
     * @return the model
     */
    public Model setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
        processEvent(new ActionEvent(this, SERVER_CONNECTION_SET, null));
        return this;
    }

    /**
     * Gets the server port.
     *
     * @return the server port
     */
    public String getServerPort() {
        return serverPort;
    }

    /**
     * Sets the server port.
     *
     * @param port the port
     * @return the model
     */
    public Model setServerPort(String port) {
        this.serverPort = port;
        return this;
    }

    /**
     * Gets the turn.
     *
     * @return the turn
     */
    public boolean getTurn() {
        return this.myTurn;
    }

    /**
     * Sets the turn.
     *
     * @param player the new turn
     */
    public void setTurn(String player) {
        LOGGER.trace("Setting current turn to: {}.", player);
        this.myTurn = player.equals(this.clientName);
        processEvent(new ActionEvent(this, TURN_SWITCHED, null));
    }

    /**
     * Put game module pieces.
     *
     * @param gameType the game type
     * @param pieces the pieces
     */
    public void putGameModulePieces(String gameType, String[] pieces) {
        gameSides.put(gameType, pieces);
    }

    /**
     * Gets the turn message.
     *
     * @return the turn message
     */
    public String getTurnMessage() {
        return turnMessage;
    }

    /**
     * Sets the turn message.
     *
     * @param message the new turn message
     */
    public void setTurnMessage(String message) {
        turnMessage = message;
        processEvent(new ActionEvent(this, TURN_MESSAGE_CHANGED, null));
    }

    /**
     * Sets the game result.
     *
     * @param gameResult the new game result
     */
    public void setGameResult(int gameResult) {
        processEvent(new ActionEvent(this, gameResult, null));
    }

    /**
     * Gets the play with ai.
     *
     * @return the play with ai
     */
    public boolean getPlayWithAI() {
        return playWithAI;
    }

    /**
     * Sets the play with ai.
     *
     * @param playWithAI the new play with ai
     */
    public void setPlayWithAI(boolean playWithAI) {
        this.playWithAI = playWithAI;
    }

    /**
     * Sets the new challenge.
     *
     * @param gameType the game type
     * @param player the player
     * @param challengeNumber the challenge number
     * @param challengeTurnTime the challenge turn time
     */
    public void setNewChallenge(String gameType, String player, String challengeNumber, String challengeTurnTime) {
        challengeGameTypes.add(gameType);
        challengePlayers.add(player);
        challengeNumbers.add(challengeNumber);
        challengeTurnTimes.add(challengeTurnTime);
        processEvent(new ActionEvent(this, NEW_CHALLENGE, Integer.toString(challengeGameTypes.size() - 1)));
    }

    /**
     * Gets the challenge.
     *
     * @param index the index
     * @return the challenge
     */
    public Map<String, String> getChallenge(int index) {
        HashMap<String, String> challenge = new HashMap<>();
        challenge.put(CHALLENGE_GAME_TYPE, challengeGameTypes.get(index));
        challenge.put(CHALLENGE_PLAYER, challengePlayers.get(index));
        challenge.put(CHALLENGE_GAME_NUMBER, challengeNumbers.get(index));
        challenge.put(CHALLENGE_TURN_TIME, challengeTurnTimes.get(index));
        return challenge;
    }

    /**
     * Cancel challenge.
     *
     * @param challengeNumber the challenge number
     */
    public void cancelChallenge(String challengeNumber) {
        processEvent(new ActionEvent(this, CANCEL_CHALLENGE, challengeNumber));
    }
}
