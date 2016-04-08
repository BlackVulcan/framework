package controller;

import controller.game.GameController;
import model.Model;
import model.ServerConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import view.ContainerView;
import view.LobbyView;
import view.LoginBox;
import view.MenuView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

public class Controller implements ActionListener {
	private static final Logger logger = LogManager.getLogger(Controller.class);
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
		gameController = new GameController(this.model, serverConnection);

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
		String command = e.getActionCommand();
		if (source instanceof Model) {
			Model model = (Model) e.getSource();
			if (sourceID == Model.GAME_CHANGED && command != null && command.equals(Model.GAMEMODULE_SET)) {
				model.getGameModule().addMoveListener(gameController);
				lobbyView.stopAutomaticRefresh();
				containerView.showView(model.getGameModule().getView());
				model.setPlayingGame(true);
			} else if (sourceID == Model.GAME_CHANGED && command != null && command.equals(Model.GAME_IS_CLOSED)) {
				loadLobby();
				containerView.showView(lobbyView);
				containerView.reset();
			}
		} else if (source instanceof MenuView) {
			if (sourceID == view.MenuView.SERVER_CONNECTION_SHOW) {
				loginBox.resetError();
				loginBox.setVisible(true);
			} else if (sourceID == MenuView.DISCONNECT_FROM_SERVER) {
				if (serverConnection.isConnected()) {
					containerView.reset();
					lobbyView.reset();
					close();
					containerView.showView(lobbyView);
					containerView.setServerConnection("");
				}
			} else if (sourceID == MenuView.TOGGLE_AI) {
				model.setPlayWithAI(!model.getPlayWithAI());
				menuView.setPlayWithAI(model.getPlayWithAI());
			} else if (sourceID == MenuView.RETURN_TO_LOBBY) {
				if (serverConnection.isConnected()) {
					model.setPlayingGame(false);
				}
			} else if (sourceID == MenuView.SURRENDER) {
				if (serverConnection.isConnected() && model.getPlayingGame()) {
					int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to surrender?", "Surrender",
							JOptionPane.YES_NO_OPTION);
					if (result == JOptionPane.YES_OPTION) {
						serverConnection.forfeit();
						model.setPlayingGame(false);
					}
				}
			}
		} else if (source instanceof LobbyView) {
			if (sourceID == LobbyView.LOBBY_REFRESH) {
				List<String> playerList = serverConnection.getPlayerlist();
				if (playerList == null) {
					logger.trace("playerlist was null. Closing connection");
					containerView.reset();
					lobbyView.reset();
					close();
					containerView.showView(lobbyView);
					containerView.setServerConnection("");
					JOptionPane.showMessageDialog(null, "Server disconnected unexpectly");
					System.exit(0);
					return;
				}
				lobbyView.setAvailablePlayers(serverConnection.getPlayerlist(), model.getClientName());
			} else if (sourceID == LobbyView.PLAY_GAME) {
				String gameType = lobbyView.getSelectedGame();
				if (gameType != null) {
					String[] gameSides = model.getGameSides(gameType);
					String[] buttons = new String[3];

					buttons[0] = gameSides[0];
					buttons[1] = gameSides[1];
					buttons[2] = "Cancel";

					int result = JOptionPane.showOptionDialog(null, "Subscribing for " + gameType + "\n\nChoose a side",
							"Subscribe", JOptionPane.OK_OPTION, 1, null, buttons, buttons[1]);

					if (result != -1 && result != 2) {
						model.setChosenGameSides(gameType, buttons[result]);
						subscribe(gameType);
					}
				}
			} else if (sourceID == LobbyView.CHALLENGE_PLAYER) {
				String player = lobbyView.getSelectedPlayer();
				String gameType = lobbyView.getSelectedGame();
				if (player != null && gameType != null) {
					String[] gameSides = model.getGameSides(gameType);
					String[] buttons = new String[3];

					buttons[0] = gameSides[0];
					buttons[1] = gameSides[1];
					buttons[2] = "Cancel";

					int result = JOptionPane.showOptionDialog(null,
							"Challenging " + player + " for " + gameType + "\n\nChoose a side", "Challenge",
							JOptionPane.OK_OPTION, 1, null, buttons, buttons[1]);

					if (result != -1 && result != 2) {
						model.setChosenGameSides(gameType, buttons[result]);
						challenge(player, gameType);
					}
				}
			} else if (sourceID == LobbyView.CHALLENGE_ACCEPTED) {
				String challengeNumber = e.getActionCommand();
				String player = lobbyView.getPlayerFromChallenge(challengeNumber);
				String gameType = lobbyView.getGameTypeFromChallenge(challengeNumber);
				if (player != null && gameType != null) {
					String[] gameSides = model.getGameSides(gameType);
					String[] buttons = new String[3];

					buttons[0] = gameSides[0];
					buttons[1] = gameSides[1];
					buttons[2] = "Cancel";

					int result = JOptionPane.showOptionDialog(null,
							"Accept challenge by " + player + " for " + gameType + "\n\nChoose a side", "Challenge",
							JOptionPane.OK_OPTION, 1, null, buttons, buttons[1]);

					if (result != -1 && result != 2) {
						lobbyView.deleteChallenge(e.getActionCommand());
						model.setChosenGameSides(gameType, buttons[result]);
						acceptChallenge(command);
					}
				}

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
						containerView.setServerConnection("Connected with " + loginBox.getHost() + " as " + loginBox.getName());
						return;
					} else {
						close();
					}
				}
				loginBox.showConnectError();
			}
		}
	}

	public void close() {
		logger.trace("Closing connection to server.");
		serverConnection.close();
	}

	public boolean connect(String hostname, int port) {
		logger.trace("Connecting to server {} on port {}.", hostname, port);
		try {
			serverConnection = new ServerConnection(hostname, port);
			gameController.setServerConnection(serverConnection);
			serverConnection.addGameListener(gameController);
			return true;
		} catch (IOException e) {
			logger.error("Error while connecting.", e);
			return false;
		}
	}

	public boolean login(String username) {
		logger.trace("Trying to login as {}.", username);
		if (serverConnection.login(username)) {
			model.setClientName(username);
			return true;
		} else {
			return false;
		}
	}

	public boolean logout() {
		// todo: implement method.
		throw new RuntimeException("Not implemented");
		// return serverConnection.logout();
	}

	public boolean subscribe(String gameType) {
		logger.trace("Subscribing for {}.", gameType);
		return serverConnection.subscribe(gameType);
	}

	public void challenge(String player, String gameType) {
		logger.trace("Challenging {} for a game of {}.", player, gameType);
		serverConnection.challenge(player, gameType);
	}

	public void acceptChallenge(String challengeId) {
		logger.trace("Accepting challenge {}.", challengeId);
		serverConnection.acceptChallenge(challengeId);
	}

	public void acceptMatch() {
		// todo: implement method.
		throw new RuntimeException("Not implemented");
	}

	/**
	 * Sets the lobby with available games and players.
	 */
	public void loadLobby() {
		logger.trace("Loading lobby view.");
		lobbyView.setAvailableGames(serverConnection.getGamelist());
		lobbyView.setAvailablePlayers(serverConnection.getPlayerlist(), model.getClientName());
		lobbyView.automaticRefresh();
	}
}
