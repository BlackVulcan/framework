package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class LobbyView extends JPanel implements View {
    ArrayList<JButton> buttons;
    JTable table;
    JList<String> playerList;
    JList<String> gameList;
    DefaultListModel<String> playerListModel, gameListModel;
    JPanel playPanel, lobbyPanel, playerPanel, challengePanel, gamePanel;
    JButton btnSpeel;
    private JPanel gamePlayerPanel;

    @Override
    public void actionPerformed(ActionEvent e) {
        //todo: do something
    }

    public LobbyView() {
        playerListModel = new DefaultListModel<>();
        gameListModel = new DefaultListModel<>();
        playPanel = new JPanel();
        lobbyPanel = new JPanel();
        challengePanel = new JPanel();
        btnSpeel = new JButton("Speel");
        //challengeScrollPane = new JScrollPane();

        this.setLayout(new BorderLayout(0, 0));
        this.add(playPanel, BorderLayout.SOUTH);
        this.add(lobbyPanel, BorderLayout.CENTER);

        playPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        playPanel.add(btnSpeel);

        lobbyPanel.setLayout(new BorderLayout(0, 0));
        lobbyPanel.add(challengePanel, BorderLayout.SOUTH);

        table = new JTable();
        table.setModel(new DefaultTableModel(
                new Object[][]{
                },
                new String[]{
                        "Spel", "Accepteren", "Weigeren", "Speler"
                }
        ));
        challengePanel.add(new JScrollPane(table));

        gamePlayerPanel = new JPanel();
        lobbyPanel.add(gamePlayerPanel, BorderLayout.CENTER);
        gamePlayerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        gameList = new JList<>(gameListModel);
        gamePanel = new JPanel();
        gamePanel.add(new JScrollPane(gameList));
        gamePlayerPanel.add(gamePanel);

        playerList = new JList<>(playerListModel);
        playerPanel = new JPanel();
        gamePlayerPanel.add(playerPanel);
        playerPanel.add(new JScrollPane(playerList));
    }

    public ArrayList<JButton> getButtons() {
        return buttons;
    }

    public void setAvailableGames(List<String> games) {
        for (String game : games) {
            gameListModel.addElement(game);
        }
    }

    public void setAvailablePlayers(List<String> players) {
        for (String player : players) {
            playerListModel.addElement(player);
        }
    }
}
