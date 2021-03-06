package view;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * The Class MenuView.
 */
public class MenuView extends JMenuBar implements View {
    public static final int DISCONNECT_FROM_SERVER = 1;
    public static final int SERVER_CONNECTION_SHOW = 4;
    public static final int RETURN_TO_LOBBY = 5;
    public static final int TOGGLE_AI = 6;
    public static final int SURRENDER = 7;
    public static final int CRASH_SERVER = 8;
    public static final int MANY_CLIENTS = 9;
    public static final int FALSE_MOVE = 10;
    public static final int SEND_MESSAGE = 11;
    public static final int SET_TURNTIME = 12;

    private static final long serialVersionUID = 1L;
    private JCheckBoxMenuItem playWithAIMenuItem;
    private ArrayList<ActionListener> actionListenerList = new ArrayList<>();

    /**
     * Instantiates a new menu view.
     */
    public MenuView() {
        JMenu start = new JMenu("Start");
        start.setMnemonic(KeyEvent.VK_S);
        JMenuItem menuItemConnect = new JMenuItem("Connect");
        menuItemConnect.setMnemonic(KeyEvent.VK_C);
        menuItemConnect.setToolTipText("Connect to a server");
        menuItemConnect.addActionListener(event -> processEvent(new ActionEvent(this, SERVER_CONNECTION_SHOW, null)));
        start.add(menuItemConnect);

        JMenuItem menuItemdisConnect = new JMenuItem("Disconnect");
        menuItemdisConnect.setMnemonic(KeyEvent.VK_D);
        menuItemdisConnect.setToolTipText("Disconnect from the current server");
        menuItemdisConnect.addActionListener(event -> processEvent(new ActionEvent(this, DISCONNECT_FROM_SERVER, null)));
        start.add(menuItemdisConnect);

        JMenuItem menuReturnToLobby = new JMenuItem("Return to lobby");
        menuReturnToLobby.setMnemonic(KeyEvent.VK_R);
        menuReturnToLobby.setToolTipText("Return to the lobby");
        menuReturnToLobby.addActionListener(event -> processEvent(new ActionEvent(this, RETURN_TO_LOBBY, null)));
        start.add(menuReturnToLobby);

        JMenuItem menuItemExit = new JMenuItem("Exit");
        menuItemExit.setMnemonic(KeyEvent.VK_E);
        menuItemExit.setToolTipText("Exit application");
        menuItemExit.addActionListener(event -> System.exit(0));
        start.add(menuItemExit);

        JMenu game = new JMenu("Game");
        game.setMnemonic(KeyEvent.VK_I);

        playWithAIMenuItem = new JCheckBoxMenuItem("Make AI play");
        playWithAIMenuItem.setMnemonic(KeyEvent.VK_E);
        playWithAIMenuItem.setToolTipText("When enabled, the AI will play moves for you");
        playWithAIMenuItem.addActionListener(event -> processEvent(new ActionEvent(this, TOGGLE_AI, null)));
        game.add(playWithAIMenuItem);
        
        JMenuItem surrenderMenuItem = new JMenuItem("Surrender");
        surrenderMenuItem.setMnemonic(KeyEvent.VK_S);
        surrenderMenuItem.setToolTipText("Surrender to the opponent");
        surrenderMenuItem.addActionListener(event -> processEvent(new ActionEvent(this, SURRENDER, null)));
        game.add(surrenderMenuItem);

        JMenuItem setTurnTime = new JMenuItem("Set turntime");
        setTurnTime.setMnemonic(KeyEvent.VK_T);
        setTurnTime.setToolTipText("Set the turntime");
        setTurnTime.addActionListener(event -> processEvent(new ActionEvent(this, SET_TURNTIME, null)));
        game.add(setTurnTime);

        JMenu foulPlay = new JMenu("Foul play");
        JMenuItem serverCrash = new JMenuItem("Crash Server");
        serverCrash.setToolTipText("Crash the server using the logout handler");
        serverCrash.addActionListener(event -> processEvent(new ActionEvent(this, CRASH_SERVER, null)));
        foulPlay.add(serverCrash);

        JMenuItem manyClients = new JMenuItem("Many Clients");
        manyClients.setToolTipText("Login as many clients");
        manyClients.addActionListener(event -> processEvent(new ActionEvent(this, MANY_CLIENTS, null)));
        foulPlay.add(manyClients);

        JMenuItem wrondMove = new JMenuItem("False Move");
        wrondMove.setToolTipText("Send a illegal move to the opponent");
        wrondMove.addActionListener(event -> processEvent(new ActionEvent(this, FALSE_MOVE, null)));
        foulPlay.add(wrondMove);

        JMenuItem message = new JMenuItem("Send message");
        message.setToolTipText("Send a illegal move to the opponent");
        message.addActionListener(event -> processEvent(new ActionEvent(this, SEND_MESSAGE, null)));
        foulPlay.add(message);

        this.add(start);
        this.add(game);
        this.add(foulPlay);
    }

    /**
     * Adds an action listener.
     *
     * @param actionListener the action listener
     */
    public void addActionListener(ActionListener actionListener) {
        actionListenerList.add(actionListener);
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
    }

    /**
     * Process event to listeners on this class.
     *
     * @param e the e
     */
    private void processEvent(ActionEvent e) {
        for (ActionListener l : actionListenerList)
            l.actionPerformed(e);
    }

    /**
     * Switch to the implemented AI of a game module.
     *
     * @param playWithAI true, if the AI needs to be activated
     */
    public void setPlayWithAI(boolean playWithAI) {
        playWithAIMenuItem.setState(playWithAI);
    }
}
