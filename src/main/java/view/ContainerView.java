package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class ContainerView extends JFrame implements View {
    private static final long serialVersionUID = 1L;
    JPanel container;
    ArrayList<JButton> buttons = new ArrayList<>();

    public ContainerView() {
        container = new JPanel();
        container.setLayout(new BorderLayout(0, 0));
        this.getContentPane().add(new JScrollPane(container), BorderLayout.CENTER);
        setFullScreen();
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(400, 300));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub

    }

    public ArrayList<JButton> getButtons() {
        return buttons;
    }

    public void showView(Component component) {
        container.add(component, BorderLayout.CENTER);
    }
    
    public void setFullScreen(){
    	this.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
}
