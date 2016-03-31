package model;

import nl.abstractteam.gamemodule.ClientAbstractGameModule;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Model {
	public static final int TURN_SWITCHED = 1;
    public static final int SERVER_CONNECTION_SET = 2;
    public static final int GAME_CHANGED = 3;

    private ArrayList<ActionListener> actionListenerList = new ArrayList<>();
	private ClientAbstractGameModule gameModule;
    private String clientName;
    private String serverAddress;
    private boolean myTurn = false;

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
        processEvent(new ActionEvent(this, GAME_CHANGED,null));
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
    
    public void setServerAddress(String serverAdress){
    	this.serverAddress = serverAdress;
    	processEvent(new ActionEvent(this, SERVER_CONNECTION_SET, null));
    }
    
    public String getServerAddress(){
    	return this.serverAddress;
    }
    
    public void setTurn(boolean myTurn){
    	this.myTurn = myTurn;
    	processEvent(new ActionEvent(this, TURN_SWITCHED, null));
    }
    
    public boolean getTurn(){
    	return this.myTurn;
    }
}
