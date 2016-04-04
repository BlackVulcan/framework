package controller;

import controller.game.GameController;
import model.Model;
import model.ServerConnection;
import view.ContainerView;
import view.LobbyView;
import view.LoginBox;
import view.MenuView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

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
		containerView = new ContainerView();
		menuView = new MenuView();
		lobbyView = new LobbyView();
		loginBox = new LoginBox(containerView);
		gameController = new GameController(model, serverConnection);

		this.model.addActionListener(this);
		this.model.addActionListener(lobbyView);
		this.model.addActionListener(containerView);
		menuView.addActionListener(this);
		loginBox.addActionListener(this);
		lobbyView.addActionListener(this);

		containerView.setJMenuBar(menuView);
		containerView.showView(lobbyView);
		containerView.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		int sourceID = e.getID();
		if (source instanceof Model) {
			Model model = (Model) e.getSource();
			if (sourceID == Model.GAME_CHANGED && e.getActionCommand() != null && e.getActionCommand().equals(Model.GAMEMODULE_SET)) {
				model.getGameModule().addMoveListener(gameController);
				lobbyView.stopAutomaticRefresh();
				containerView.showView(model.getGameModule().getView());
			}
		} else if (source instanceof ContainerView) {
			if (sourceID == ContainerView.RETURN_TO_LOBBY){
				containerView.showView(lobbyView);
				loadLobby();
			}
		}else if (source instanceof MenuView) {
			if (sourceID == view.MenuView.SERVER_CONNECTION_SHOW) {
				loginBox.setVisible(true);
			} else if (sourceID == MenuView.DiSCONNECT_FROM_SERVER) {
				lobbyView.reset();
				close();
			} else if (sourceID == MenuView.ENABLE_AI) {
				model.setPlayWithAI(true);
			} else if (sourceID == MenuView.DISABLE_AI){
				model.setPlayWithAI(false);
			}
		} else if (source instanceof LobbyView) {
			if(sourceID == LobbyView.LOBBY_REFRESH){
				lobbyView.setAvailablePlayers(serverConnection.getPlayerlist(), model.getClientName());
			} else if (sourceID == LobbyView.PLAY_GAME){
				String gameType = lobbyView.getSelectedGame();
				if(gameType != null){
					int result = JOptionPane.showConfirmDialog(null, 
							"Subcribe to " + gameType + "?",null, JOptionPane.YES_NO_OPTION);
					if(result == JOptionPane.YES_OPTION)
						subscribe(gameType);
				}
			} else if (sourceID == LobbyView.CHALLENGE_PLAYER){
				String player = lobbyView.getSelectedPlayer();
				String gameType = lobbyView.getSelectedGame();
				if(player != null && gameType != null){
					int result = JOptionPane.showConfirmDialog(null, 
							"Challenge " + player + " to play " + gameType + "?" ,null, JOptionPane.YES_NO_OPTION);
					if(result == JOptionPane.YES_OPTION)
						challenge(player, gameType);
				}
			} else if (sourceID == LobbyView.CHALLENGE_ACCEPTED){
				acceptChallenge(e.getActionCommand());
			}
		} else if (source instanceof LoginBox) {
			if (sourceID == LoginBox.SERVER_CONNECTION_SET) {
				if (!loginBox.hasInput()) {
					loginBox.showEmptyError();
					return;
				}

				if (serverConnection != null && serverConnection.isConnected()) {
					loginBox.showAlreadyConnected();
					return;
				}

				if (connect(loginBox.getHost(), loginBox.getPort())) {
					if (login(loginBox.getName())) {
						loadLobby();
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
			serverConnection.addGameListener(gameController);
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

	public boolean subscribe(String gameType) {
		return serverConnection.subscribe(gameType);
	}

	public void challenge(String player, String gameType) {
		serverConnection.challenge(player, gameType);
	}

	public void acceptChallenge(String challengeId) {
		serverConnection.acceptChallenge(challengeId);
	}

	public void acceptMatch() {
		//todo: implement method.
		throw new RuntimeException("Not implemented");
	}

	/**
	 * Sets the lobby with available games and players.
	 */
	public void loadLobby() {
		lobbyView.setAvailableGames(serverConnection.getGamelist());
		lobbyView.setAvailablePlayers(serverConnection.getPlayerlist(), model.getClientName());
		lobbyView.automaticRefresh();
	}
}
