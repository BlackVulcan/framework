package controller;

import controller.game.GameController;
import model.Model;
import model.ServerConnection;
import nl.abstractteam.gamemodule.ClientAbstractGameModule;
import view.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Controller implements ActionListener {
    private final Model model;
    private ServerConnection serverConnection;
    private GameController gameController;
    ContainerView containerView;
    MenuView menuView;
    LobbyView lobbyView;

    public Controller(Model model) {
        this.model = model;
        this.containerView = new ContainerView();
        this.menuView = new MenuView();
        this.lobbyView = new LobbyView();

        model.addActionListener(this);
        model.addActionListener(lobbyView);
        menuView.addActionListener(this);

        containerView.setJMenuBar(menuView);

        gameController = new GameController(model,serverConnection);
        //maybe for later in the project
//        //add actionListeners to control buttons
//        for (JButton button : containerView.getButtons()) {
//            button.addActionListener(this);
//        }
        
        this.containerView.showView(lobbyView);
        
        this.containerView.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source instanceof Model) {
            Model model = (Model) e.getSource();
            if(e.getID()==Model.GAME_CHANGED){
                model.getGameModule().addMoveListener(gameController);
                containerView.showView(model.getGameModule().getView());
            }
        } else if (source instanceof LobbyView) {
            //do stuff
        } else if (source instanceof MenuView) {
        	int sourceID = e.getID();
        	if (sourceID == MenuView.SERVER_CONNECTION_SET) {
        		setLobby();
        	} else if (sourceID == MenuView.DISCONNECTED_FROM_SERVER) {
        		lobbyView.reset();
        	}
        }

    }

    public boolean connect(String hostname, int port) {
        try {
            serverConnection = new ServerConnection(hostname, port);
            gameController.setServerConnection(serverConnection);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean login(String username) {
         if(serverConnection.login(username)){
             model.setClientName(username);
             return true;
         }
        else{
             return false;
         }
    }

    public boolean logout() {
        //todo: implement method.
        throw new RuntimeException("Not implemented");
//        return serverConnection.logout();
    }

    public boolean subscribe(String gameName) {
        return serverConnection.subscribe(gameName);
    }

    public void challenge(String player, String gameMode) {
        serverConnection.challenge(player, gameMode);
    }

    public void acceptChallenge(String challengeId) {
        serverConnection.acceptChallenge(challengeId);
    }

    public void acceptMatch() {
        //todo: implement method.
        throw new RuntimeException("Not implemented");
    }
    
    /**
     * Sets the lobby with available games and players if connected with a server.
     * 
     * Contains test data. Needs to be removed when connection with a server is possible
     */
    public void setLobby() {
    	//Needs to be changed when the registration of the ServerConnection class is moved to the Model class.
    	boolean connected = true;
    	if(connected) {
        	//lobbyView.setAvailableGames(serverConnection.getGamelist());
        	//lobbyView.setAvailablePlayers(serverConnection.getPlayerlist());
        	
        	List<String> list = new ArrayList<>();
        	for(int i = 0; i < 50; i++) {
        		list.add("TeGekkeGame");
        	}
        	lobbyView.setAvailableGames(list);
        	
        	List<String> list2 = new ArrayList<>();
        	for(int i = 0; i < 50; i++) {
        		list2.add("Ikzelf");
        	}
        	lobbyView.setAvailablePlayers(list2);
        	
        	for(int i = 0; i < 50; i++){
        		lobbyView.setChallenge("TeGekkeGame", "Ikzelf");
        	}
    	}
    }
}
