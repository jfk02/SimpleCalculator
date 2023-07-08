package sk.javakurz;

import com.formdev.flatlaf.FlatIntelliJLaf;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {

        try {
            FlatIntelliJLaf myThemeLookAndFeel = new FlatIntelliJLaf();
            UIManager.setLookAndFeel(myThemeLookAndFeel);
        } catch (Exception e) {
            System.err.println("Nimbus is not available");
        }

        JFrame frame = new JFrame("MyCalculator");
        frame.setContentPane(new MyCalculator().getPanel1());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(350, 450);
        frame.setMinimumSize(new Dimension(350, 450));
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
    }
}
