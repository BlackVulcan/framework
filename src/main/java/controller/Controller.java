package controller;

import controller.game.GameController;
import model.Model;
import model.ServerConnection;
import view.ContainerView;
import view.LobbyView;
import view.LoginBox;
import view.MenuView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Controller implements ActionListener {
	private final Model model;
	ContainerView containerView;
	MenuView menuView;
	LobbyView lobbyView;
	LoginBox loginBox;
	private ServerConnection serverConnection;
	private GameController gameController;

	public Controller(Model model) {
		this.model = model;
		this.containerView = new ContainerView();
		this.menuView = new MenuView();
		this.lobbyView = new LobbyView();
		
		System.out.println(lobbyView.getClass().getName());
		this.loginBox = new LoginBox(containerView);

		model.addActionListener(this);
		model.addActionListener(lobbyView);
		model.addActionListener(containerView);
		menuView.addActionListener(this);
		loginBox.addActionListener(this);
		lobbyView.addActionListener(this);

		containerView.setJMenuBar(menuView);

		gameController = new GameController(model, serverConnection);
		//maybe for later in the project
		//        //add actionListeners to control buttons
		//        for (JButton button : containerView.getButtons()) {
		//            button.addActionListener(this);
		//        }

		this.containerView.showView(lobbyView);
		System.out.println(lobbyView.getClass().getName());
		this.containerView.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		int sourceID = e.getID();
		if (source instanceof Model) {
			Model model = (Model) e.getSource();
			if (sourceID == Model.GAME_CHANGED && e.getActionCommand().equals(Model.GAMEMODULE_SET)) {
				model.getGameModule().addMoveListener(gameController);
				lobbyView.stopAutomatichRefresh();
				containerView.showView(model.getGameModule().getView());
			}
		} else if (source instanceof MenuView) {
			if (sourceID == view.MenuView.SERVER_CONNECTION_SHOW) {
				loginBox.setVisible(true);
			} else if (sourceID == MenuView.DiSCONNECT_FROM_SERVER) {
				lobbyView.reset();
				close();
			} else if (sourceID == MenuView.PLAY_WITH_AI) {
				//Activate AI
				System.out.println("AI is not yet implemented...");
			}
		} else if (source instanceof LobbyView) {
			if(sourceID == LobbyView.LOBBY_REFRESH){
				lobbyView.setAvailablePlayers(serverConnection.getPlayerlist(), model.getClientName());
			} else if (sourceID == LobbyView.PLAY_GAME){
			}
		} else if (source instanceof LoginBox) {
			if (sourceID == LoginBox.SERVER_CONNECTION_SET) {
				if (!loginBox.hasInput()) {
					loginBox.showEmptyError();
					return;
				}

				if (connect(loginBox.getHost(), loginBox.getPort())) {
					if (login(loginBox.getName())) {
						setLobby();
						loginBox.resetError();
						loginBox.setVisible(false);
						return;
					} else {
						close();
					}
				}
				loginBox.showConnectError();
			}
		}
	}

	private void close() {
		serverConnection.close();
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
		if (serverConnection.login(username)) {
			model.setClientName(username);
			return true;
		} else {
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
		lobbyView.setAvailableGames(serverConnection.getGamelist());
		lobbyView.setAvailablePlayers(serverConnection.getPlayerlist(), model.getClientName());
		lobbyView.automaticRefresh();

		// need to build something for getting challenges!!

		// begin test code
		//            model.setOpponent("Yokovaski");
		//            model.setTurn(model.getClientName());
		//            containerView.setTime(20000, model);
		// end test code
	}

	//public void setInfoPanel(String opponent, turn)
}
