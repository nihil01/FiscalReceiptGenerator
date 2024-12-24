package az.horosho;

import javax.swing.*;
import java.awt.*;

import az.horosho.UI.SwingCustomUtilities;

import static az.horosho.UI.HelperMethods.createComponentMap;
public class Main extends SwingCustomUtilities{

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 650);
        frame.setLayout(new BorderLayout());

        JTextArea textArea = new JTextArea();
        JButton jButtonVariables = new JButton("Список переменных");

        jButtonVariables.addActionListener(e -> openVariableWindow());

        JPanel lowerPanel = generateLowerPanel(jButtonVariables);
        lowerPanel.setName("lowerPanel");
        JPanel upperPanel = generateUpperPanel(textArea);
        upperPanel.setName("upperPanel");

        frame.add(upperPanel, BorderLayout.NORTH);
        frame.add(lowerPanel, BorderLayout.SOUTH);

        createComponentMap(frame.getComponents());
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
