package view;

import model.Model;
import org.apache.logging.log4j.LogManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URL;

/**
 * The Class ContainerView.
 */
public class ContainerView extends JFrame implements View {
    private static final long serialVersionUID = 1L;
    private static final String RESULT_DRAW = "It's a draw!";
    private static final String RESULT_LOSS = "You have lost the game!";
    private static final String RESULT_WIN = "You have won the game!";
    private static final String ICON_PATH = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "gameicon.png";
    private JPanel container;
    private JLabel turn, turnMessage, opponent, time, serverConnection, playSide;
    private boolean gameOver = false;

    /**
     * Instantiates a new container view.
     */
    public ContainerView() {
        super("Two player game framework");
        URL resource = getClass().getResource("/gameicon.png");
        LogManager.getLogger(ContainerView.class).trace(resource);
        ImageIcon img = new ImageIcon(resource);
        if (img.getImage() == null) {
            img = new ImageIcon(ICON_PATH);
        }
        this.setIconImage(img.getImage());

        JPanel informationPanel = new JPanel();
        getContentPane().add(informationPanel, BorderLayout.NORTH);
        informationPanel.setLayout(new GridLayout(0, 4, 0, 0));
        opponent = new JLabel("", SwingConstants.CENTER);
        turn = new JLabel("", SwingConstants.CENTER);
        turnMessage = new JLabel("", SwingConstants.CENTER);
        time = new JLabel("", SwingConstants.CENTER);
        informationPanel.add(opponent);
        informationPanel.add(turn);
        informationPanel.add(turnMessage);
        informationPanel.add(time);
        setFullScreen();
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(400, 300));

        container = new JPanel();
        container.setLayout(new BorderLayout(0, 0));
        this.getContentPane().add(new JScrollPane(container), BorderLayout.CENTER);

        JPanel serverPanel = new JPanel();
        getContentPane().add(serverPanel, BorderLayout.SOUTH);
        serverPanel.setLayout(new GridLayout(0, 2));
        serverConnection = new JLabel("");
        serverPanel.add(serverConnection);
        playSide = new JLabel("");
        serverPanel.add(playSide);
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object object = e.getSource();
        int objectID = e.getID();
        if (object instanceof Model) {
            Model model = (Model) object;
            if (objectID == Model.TURN_SWITCHED) {
                setTurn(model.getTurn());
                setTime(model.getChallengeTurnTime(), model);
            } else if (objectID == Model.GAME_DRAW && model.getPlayingGame()) {
                this.turn.setText(RESULT_DRAW);
                gameOver = true;
            } else if (objectID == Model.GAME_LOSS && model.getPlayingGame()) {
                this.turn.setText(RESULT_LOSS);
                gameOver = true;
            } else if (objectID == Model.GAME_WIN && model.getPlayingGame()) {
                this.turn.setText(RESULT_WIN);
                gameOver = true;
            } else if (objectID == Model.GAME_CHANGED && e.getActionCommand().equals(Model.OPPONENT_SET)) {
                this.opponent.setText("Opponent: " + model.getOpponent());
            } else if (objectID == Model.TURN_MESSAGE_CHANGED) {
                this.setTurnMessage(model.getTurnMessage());
            }
        }
    }

    /**
     * Loads the view of a module into the main JPanel.
     *
     * @param component the component
     */
    public void showView(Component component) {
        container.removeAll();
        container.add(component);
        revalidate();
        repaint();
    }

    /**
     * Sets the window to full screen modus.
     */
    private void setFullScreen() {
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    /**
     * Sets the turn.
     *
     * @param myTurn the new turn
     */
    private void setTurn(Boolean myTurn) {
        String turnInformation;
        if (myTurn)
            turnInformation = "Your turn!";
        else
            turnInformation = opponent.getText() + "'s turn!";
        this.turn.setText(turnInformation);
    }

    /**
     * Sets the turn message.
     *
     * @param message the new turn message
     */
    private void setTurnMessage(String message) {
        this.turnMessage.setText(message);
    }

    /**
     * Sets the time box with the remaining turn time.
     *
     * @param time the new time box
     */
    private void setTimeBox(String time) {
        this.time.setText(time);
    }

    /**
     * Sets the server connection text.
     *
     * @param serverConnection the new server connection
     */
    public void setServerConnection(String serverConnection) {
        this.serverConnection.setText(serverConnection);
    }

    /**
     * Sets the play side text.
     *
     * @param playSide the new play side
     */
    public void setPlaySide(String playSide) {
        this.playSide.setText(playSide);
    }

    /**
     * Sets the time.
     *
     * @param timeInMillis the time in millis
     * @param model the model
     */
    private void setTime(int timeInMillis, Model model) {
        Runnable thread = () -> {
            setTimeBox("");
            boolean timeIsRunning = model.getTurn();
            for (int i = timeInMillis; i >= 0; i--) {
                if (model.getTurn() && model.getPlayingGame() && !gameOver) {
                    setTimeBox("Seconds left: " + (i / 1000) + "." + ((i % 1000) / 100));
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ignored) {
                    }
                } else {
                    timeIsRunning = false;
                    setTimeBox("");
                    break;
                }
            }
            if (timeIsRunning) {
                timeIsRunning = false;
                setTimeBox("Time has run out");
            } else
                setTimeBox("");
            gameOver = false;
        };
        new Thread(thread).start();
    }

    /**
     * Reset the text fields of the upper panel.
     */
    public void reset() {
        this.turn.setText("");
        this.turnMessage.setText("");
        this.time.setText("");
        this.opponent.setText("");
    }
}
