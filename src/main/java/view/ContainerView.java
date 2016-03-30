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
        this.getContentPane().add(new JScrollPane(container), BorderLayout.CENTER);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
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

    public void showView(JPanel panel) {
        container.add(panel, BorderLayout.CENTER);
    }
}
