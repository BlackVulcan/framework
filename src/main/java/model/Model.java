package model;

import nl.abstractteam.gamemodule.ClientAbstractGameModule;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import controller.game.GameModuleLoader;

public class Model {
	public static final int TURN_SWITCHED = 1;
    public static final int SERVER_CONNECTION_SET = 2;
    public static final int GAME_CHANGED = 3;
    public static final int GAME_DRAW = 4;
    public static final int GAME_WIN = 5;
    public static final int GAME_LOSS = 6;
    public static final int NEW_CHALLENGE = 7;
    
    public static final String GAMEMODULE_SET = "gamemodule is set";
    public static final String OPPONENT_SET = "opponent is set";
    public static final String CHALLENGE_GAME_TYPE = "gametype";
    public static final String CHALLENGE_PLAYER = "player";
    public static final String CHALLENGE_GAME_NUMBER = "gamenumber";
    
    private ArrayList<ActionListener> actionListenerList = new ArrayList<>();
	private ClientAbstractGameModule gameModule;
    private String clientName, opponent, serverAddress;
    private int gameResult = 0;
    private boolean myTurn = false;
    private GameModuleLoader gameModuleLoader;
    private ArrayList<String> challengeGameTypes = new ArrayList<>(), 
    		challengePlayers = new ArrayList<>(), challengeNumbers = new ArrayList<>();

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

    public ClientAbstractGameModule getGameModule() {
        return gameModule;
    }

    public void setGameModule(ClientAbstractGameModule gameModule) {
        this.gameModule = gameModule;
        gameModule.setClientBegins(gameModule.getPlayerToMove().equals(clientName));
        processEvent(new ActionEvent(this, GAME_CHANGED,null));
    }
    
    public void setGameModuleLoader(GameModuleLoader gameModuleLoader){
    	this.gameModuleLoader = gameModuleLoader;
    }
    
    public GameModuleLoader getGameModuleLoader(){
    	return this.gameModuleLoader;
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
    
    public void setServerAddress(String serverAdress){
    	this.serverAddress = serverAdress;
    	processEvent(new ActionEvent(this, SERVER_CONNECTION_SET, null));
    }
    
    public String getServerAddress(){
    	return this.serverAddress;
    }
    
    public void setTurn(String player){
    	if(player.equals(this.clientName))
        	this.myTurn = true;
    	else
    		this.myTurn = false;
    	processEvent(new ActionEvent(this, TURN_SWITCHED, null));
    }
    
    public boolean getTurn(){
    	return this.myTurn;
    }
    
    public void setGameResult(int gameResult){
    	this.gameResult = gameResult;
    	processEvent(new ActionEvent(this, gameResult, null));
    }
    
    public int getGameResult(){
    	return gameResult;
    }
    
    public void setNewChallenge(String gameType, String player, String challengeNumber){
    	challengeGameTypes.add(gameType);
    	challengePlayers.add(player);
    	challengeNumbers.add(challengeNumber);
    	processEvent(new ActionEvent(this, NEW_CHALLENGE, (challengeGameTypes.size() -1) + ""));
    }
    
    public HashMap<String, String> getChallenge(int index){
    	HashMap<String, String> challenge = new HashMap<>();
    	challenge.put(CHALLENGE_GAME_TYPE, challengeGameTypes.get(index));
    	challenge.put(CHALLENGE_PLAYER, challengePlayers.get(index));
    	challenge.put(CHALLENGE_GAME_NUMBER, challengeNumbers.get(index));
    	return challenge;
    }
}
