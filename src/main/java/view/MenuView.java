package view;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class MenuView extends JMenuBar implements View {
    private static final long serialVersionUID = 1L;
    private ArrayList<ActionListener> actionListenerList = new ArrayList<>();
    public static final int SERVER_CONNECTION_SET = 1;
    public static final int DISCONNECTED_FROM_SERVER = 2;

    public MenuView() {
        JMenu file = new JMenu("Start");
        file.setMnemonic(KeyEvent.VK_S);
        JMenuItem mennuItemConnect = new JMenuItem("Connect");
        mennuItemConnect.setMnemonic(KeyEvent.VK_C);
        mennuItemConnect.setToolTipText("Connect to a server");
        mennuItemConnect.addActionListener(event -> {
            //todo: extend this to support other things.
            LoginBox loginBox = new LoginBox();
            loginBox.setVisible(true);
            
            //if we are now connected to a server:
            if(true)
            	processEvent(new ActionEvent(this, SERVER_CONNECTION_SET,null));
        });
        file.add(mennuItemConnect);

        JMenuItem mennuItemdisConnect = new JMenuItem("Disconnect");
        mennuItemdisConnect.setMnemonic(KeyEvent.VK_D);
        mennuItemdisConnect.setToolTipText("Disconnect from the current server");
        mennuItemdisConnect.addActionListener(event -> {
            //todo: implement disconnection from server.
            
            //if we are now disconnected from a server:
            if(true)
            	processEvent(new ActionEvent(this, DISCONNECTED_FROM_SERVER,null));
        });
        file.add(mennuItemdisConnect);

        JMenuItem enuItemExit = new JMenuItem("Exit");
        enuItemExit.setMnemonic(KeyEvent.VK_E);
        enuItemExit.setToolTipText("Exit application");
        enuItemExit.addActionListener(event -> System.exit(0));
        file.add(enuItemExit);

        this.add(file);
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
