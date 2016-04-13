package view;

import model.Model;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import util.ButtonColumn;
import util.ColumnSorter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

/**
 * The Class LobbyView.
 */
public class LobbyView extends JPanel implements View {
    public static final int LOBBY_REFRESH = 1;
    public static final int PLAY_GAME = 2;
    public static final int CHALLENGE_PLAYER = 3;
    public static final int CHALLENGE_ACCEPTED = 4;
    private static final Logger LOGGER = LogManager.getLogger(LobbyView.class);
    private static final String CHALLENGE_ACCEPT = "Accept";
    private static final String CHALLENGE_REJECT = "Reject";
    private static final long serialVersionUID = 1L;
    private JTable challengeTable;
    private JList<String> playerList;
    private JList<String> gameList;
    private DefaultListModel<String> playerListModel, gameListModel;
    private boolean automaticRefresh = false;
    private ArrayList<ActionListener> actionListenerList = new ArrayList<>();

    /**
     * Instantiates a new lobby view.
     */
    public LobbyView() {
        playerListModel = new DefaultListModel<>();
        gameListModel = new DefaultListModel<>();
        JPanel playPanel = new JPanel();
        JPanel lobbyPanel = new JPanel();
        JPanel challengePanel = new JPanel();

        this.setLayout(new BorderLayout(0, 0));
        this.add(playPanel, BorderLayout.SOUTH);
        this.add(lobbyPanel, BorderLayout.CENTER);

        playPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        JButton subscribe = new JButton("Subscribe");
        ActionEvent playGame = new ActionEvent(this, PLAY_GAME, null);
        subscribe.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                processEvent(playGame);
            }
        });
        playPanel.add(subscribe);

        JButton challenge = new JButton("Challenge");
        ActionEvent challengePlayer = new ActionEvent(this, CHALLENGE_PLAYER, null);
        challenge.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                processEvent(challengePlayer);
            }
        });
        playPanel.add(challenge);
        lobbyPanel.setLayout(new GridLayout(0, 1, 0, 0));

        JPanel gamePlayerPanel = new JPanel();
        lobbyPanel.add(gamePlayerPanel);
        gamePlayerPanel.setLayout(new GridLayout(1, 0, 0, 0));
        gameList = new JList<>(gameListModel);
        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new BorderLayout(0, 0));
        gamePanel.add(new JScrollPane(gameList), BorderLayout.CENTER);
        gamePlayerPanel.add(gamePanel);

        playerList = new JList<>(playerListModel);
        JPanel playerPanel = new JPanel();
        gamePlayerPanel.add(playerPanel);
        playerPanel.setLayout(new BorderLayout(0, 0));
        playerPanel.add(new JScrollPane(playerList));
        lobbyPanel.add(challengePanel);
        challengePanel.setLayout(new BorderLayout(0, 0));

        challengeTable = new JTable();
        challengeTable.setPreferredScrollableViewportSize(challengeTable.getPreferredSize());
        challengeTable.setFillsViewportHeight(true);
        DefaultTableModel challengeTableModel = new DefaultTableModel(new Object[][]{},
                new String[]{"ID", "Game", "Player", "Turn Time", CHALLENGE_ACCEPT, CHALLENGE_REJECT});
        challengeTable.setModel(challengeTableModel);

        challengeTable.setAutoCreateColumnsFromModel(false);
        challengeTable.getTableHeader().setReorderingAllowed(false);
        challengeTable.setAutoCreateRowSorter(true);

        Vector data = challengeTableModel.getDataVector();
        Collections.sort(data, new ColumnSorter(1));
        challengeTableModel.fireTableStructureChanged();

        Action acceptChallenge = new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                JTable table = (JTable) e.getSource();
                int modelRow = Integer.parseInt(e.getActionCommand());
                acceptChallenge((String) table.getValueAt(modelRow, 0));
            }
        };

        Action rejectChallenge = new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(null, CHALLENGE_REJECT + " challenge?", null,
                        JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    JTable table = (JTable) e.getSource();
                    int modelRow = Integer.parseInt(e.getActionCommand());
                    ((DefaultTableModel) table.getModel()).removeRow(modelRow);
                }
            }
        };

        new ButtonColumn(challengeTable, acceptChallenge, 4);
        new ButtonColumn(challengeTable, rejectChallenge, 5);

        challengePanel.add(new JScrollPane(challengeTable), BorderLayout.CENTER);
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object object = e.getSource();
        int sourceID = e.getID();
        if (object instanceof Model) {
            Model model = (Model) object;
            if (sourceID == Model.NEW_CHALLENGE) {
                int index = Integer.parseInt(e.getActionCommand());
                setChallenge(model.getChallenge(index));
            } else if (sourceID == Model.CANCEL_CHALLENGE) {
                deleteChallenge(e.getActionCommand());
            }
        }
    }

    /**
     * Gets the selected game.
     *
     * @return the selected game
     */
    public String getSelectedGame() {
        int gameIndex = gameList.getSelectedIndex();
        if (gameIndex >= 0)
            return gameListModel.getElementAt(gameIndex);
        return null;
    }

    /**
     * Sets the available games.
     *
     * @param games the new available games
     */
    public void setAvailableGames(List<String> games) {
        resetGameList();
        for (String game : games) {
            gameListModel.addElement(game);
        }
    }

    /**
     * Gets the selected player.
     *
     * @return the selected player
     */
    public String getSelectedPlayer() {
        int playerIndex = playerList.getSelectedIndex();
        if (playerIndex >= 0)
            return playerListModel.getElementAt(playerIndex);
        return null;
    }

    /**
     * Sets the available players.
     *
     * @param players the players
     * @param clientName the client name
     */
    public void setAvailablePlayers(List<String> players, String clientName) {
        for (int i = playerListModel.size() - 1; i >= 0; i--) {
            if (!players.contains(playerListModel.get(i))) {
                playerListModel.remove(i);
            }
        }

        players.stream().filter(player -> !playerListModel.contains(player) && !player.equals(clientName)).forEach(player -> playerListModel.addElement(player));
    }

    /**
     * Sets the challenge.
     *
     * @param challenge the challenge
     */
    private void setChallenge(Map<String, String> challenge) {
        DefaultTableModel model = (DefaultTableModel) challengeTable.getModel();

        // needs to be modified to accept or reject a challenge
        model.addRow(
                new Object[]{challenge.get(Model.CHALLENGE_GAME_NUMBER), challenge.get(Model.CHALLENGE_GAME_TYPE),
                        challenge.get(Model.CHALLENGE_PLAYER), challenge.get(Model.CHALLENGE_TURN_TIME), CHALLENGE_ACCEPT, CHALLENGE_REJECT,});
    }

    /**
     * Accept challenge.
     *
     * @param challengeNumber the challenge number
     */
    private void acceptChallenge(String challengeNumber) {
        processEvent(new ActionEvent(this, CHALLENGE_ACCEPTED, challengeNumber));
    }

    /**
     * Delete challenge.
     *
     * @param challengeNumber the challenge number
     */
    public void deleteChallenge(String challengeNumber) {
        DefaultTableModel challengeTableModel = (DefaultTableModel) challengeTable.getModel();
        for (int i = challengeTableModel.getRowCount() - 1; i >= 0; i--) {
            if (challengeNumber.equals(challengeTableModel.getValueAt(i, 0))) {
                challengeTableModel.removeRow(i);
            }
        }
    }

    /**
     * Reset challenge.
     */
    private void resetChallenge() {
        DefaultTableModel challengeTableModel = (DefaultTableModel) challengeTable.getModel();
        for (int i = challengeTableModel.getRowCount() - 1; i >= 0; i--) {
            challengeTableModel.removeRow(i);
        }
    }

    /**
     * Gets the player from the challenge.
     *
     * @param challengeNumber the challenge number
     * @return the player from challenge
     */
    public String getPlayerFromChallenge(String challengeNumber) {
        DefaultTableModel challengeTableModel = (DefaultTableModel) challengeTable.getModel();
        for (int i = challengeTableModel.getRowCount() - 1; i >= 0; i--) {
            if (challengeNumber.equals(challengeTableModel.getValueAt(i, 0))) {
                return (String) challengeTableModel.getValueAt(i, 2);
            }
        }
        return null;
    }

    /**
     * Gets the game type from the challenge.
     *
     * @param challengeNumber the challenge number
     * @return the game type from challenge
     */
    public String getGameTypeFromChallenge(String challengeNumber) {
        DefaultTableModel challengeTableModel = (DefaultTableModel) challengeTable.getModel();
        for (int i = challengeTableModel.getRowCount() - 1; i >= 0; i--) {
            if (challengeNumber.equals(challengeTableModel.getValueAt(i, 0))) {
                return (String) challengeTableModel.getValueAt(i, 1);
            }
        }
        return null;
    }

    /**
     * Gets the turn time from the challenge.
     *
     * @param challengeNumber the challenge number
     * @return the turn time from challenge
     */
    public String getTurnTimeFromChallenge(String challengeNumber) {
        DefaultTableModel challengeTableModel = (DefaultTableModel) challengeTable.getModel();
        for (int i = challengeTableModel.getRowCount() - 1; i >= 0; i--) {
            if (challengeNumber.equals(challengeTableModel.getValueAt(i, 0))) {
                return (String) challengeTableModel.getValueAt(i, 3);
            }
        }
        return null;
    }

    /**
     * Reset the player list.
     */
    private void resetPlayerList() {
        for (int i = playerListModel.size() - 1; i >= 0; i--) {
            playerListModel.remove(i);
        }
    }

    /**
     * Reset the game list.
     */
    private void resetGameList() {
        for (int i = gameListModel.size() - 1; i >= 0; i--) {
            gameListModel.remove(i);
        }
    }

    /**
     * Reset all the lists and the challenge table.
     */
    public void reset() {
        resetChallenge();
        resetPlayerList();
        resetGameList();
    }

    /**
     * Stop the automatic refresh of the lobby.
     */
    public void stopAutomaticRefresh() {
        automaticRefresh = false;
    }

    /**
     * Start the automatic refresh of the lobby.
     */
    public void automaticRefresh() {
        automaticRefresh = true;
        ActionEvent refreshLobby = new ActionEvent(this, LOBBY_REFRESH, null);
        Runnable thread = () -> {
            while (automaticRefresh) {
                try {
                    processEvent(refreshLobby);
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    LOGGER.warn("Interrupt when refreshing lobby.");
                }
            }
        };
        new Thread(thread).start();
    }

    /**
     * Process event to listeners.
     *
     * @param e the e
     */
    private void processEvent(ActionEvent e) {
        for (ActionListener l : actionListenerList)
            l.actionPerformed(e);
    }

    /**
     * Adds an action listener on this class.
     *
     * @param actionListener the action listener
     */
    public void addActionListener(ActionListener actionListener) {
        actionListenerList.add(actionListener);
    }
}
