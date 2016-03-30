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

        containerView.setJMenuBar(menuView);

        gameController = new GameController(model,serverConnection);
        connect("localhost",7789);
        login("laurens");
        subscribe("Guess Game");
        //maybe for later in the project
//        //add actionListeners to control buttons
//        for (JButton button : containerView.getButtons()) {
//            button.addActionListener(this);
//        }
        
        setLobby();
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
                System.out.println("View is being set in the containerView");
                containerView.showView(model.getGameModule().getView());
            }
        } else if (source instanceof LobbyView) {
            //do stuff
        }

    }

    public boolean connect(String hostname, int port) {
        try {
            serverConnection = new ServerConnection(hostname, port);
            gameController.setServerConnection(serverConnection);
            serverConnection.addGameListener(gameController);
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
    
    public void setLobby(){
    	//lobbyView.setAvailableGames(serverConnection.getGamelist());
    	//lobbyView.setAvailablePlayers(serverConnection.getPlayerlist());
    	
    	List<String> list = new ArrayList<>();
    	for(int i = 0; i < 50; i++){
    		list.add("TeGekkeGame");
    	}
    	lobbyView.setAvailableGames(list);
    	
    	List<String> list2 = new ArrayList<>();
    	for(int i = 0; i < 50; i++){
    		list2.add("Ikzelf");
    	}
    	lobbyView.setAvailablePlayers(list2);
    	containerView.pack();
    	
    	for(int i = 0; i < 50; i++){
    		lobbyView.setChallenge("TeGekkeGame", "Ikzelf");
    	}
    }
}
