package view;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class MenuView extends JMenuBar implements View {
    public static final int DiSCONNECT_FROM_SERVER = 1;
    public static final int PLAY_WITH_AI = 2;
    public static final int SERVER_CONNECTION_SHOW = 3;
    private static final long serialVersionUID = 1L;
    private ArrayList<ActionListener> actionListenerList = new ArrayList<>();

    public MenuView() {
        JMenu start = new JMenu("Start");
        start.setMnemonic(KeyEvent.VK_S);
        JMenuItem mennuItemConnect = new JMenuItem("Connect");
        mennuItemConnect.setMnemonic(KeyEvent.VK_C);
        mennuItemConnect.setToolTipText("Connect to a server");
        mennuItemConnect.addActionListener(event -> {
            processEvent(new ActionEvent(this, SERVER_CONNECTION_SHOW, null));
        });
        start.add(mennuItemConnect);

        JMenuItem mennuItemdisConnect = new JMenuItem("Disconnect");
        mennuItemdisConnect.setMnemonic(KeyEvent.VK_D);
        mennuItemdisConnect.setToolTipText("Disconnect from the current server");
        mennuItemdisConnect.addActionListener(event -> {
            processEvent(new ActionEvent(this, DiSCONNECT_FROM_SERVER, null));
        });
        start.add(mennuItemdisConnect);

        JMenuItem enuItemExit = new JMenuItem("Exit");
        enuItemExit.setMnemonic(KeyEvent.VK_E);
        enuItemExit.setToolTipText("Exit application");
        enuItemExit.addActionListener(event -> System.exit(0));
        start.add(enuItemExit);
        
        JMenu intelligence = new JMenu("Intelligence");
        intelligence.setMnemonic(KeyEvent.VK_I);
        JMenuItem mennuItemPlayWithAI = new JMenuItem("Play with AI");
        mennuItemPlayWithAI.setMnemonic(KeyEvent.VK_P);
        mennuItemPlayWithAI.setToolTipText("Activate AI to play for you");
        mennuItemPlayWithAI.addActionListener(event -> {
            //if gamemodule is loaded:
            if(true)
            	processEvent(new ActionEvent(this, PLAY_WITH_AI, null));
        });
        intelligence.add(mennuItemPlayWithAI);

        this.add(start);
        this.add(intelligence);
    }
    
    public void addActionListener(ActionListener actionListener) {
        actionListenerList.add(actionListener);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
    }
    
    private void processEvent(ActionEvent e) {
        for (ActionListener l : actionListenerList)
            l.actionPerformed(e);
    }
}
