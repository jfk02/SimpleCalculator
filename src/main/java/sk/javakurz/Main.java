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
            System.err.println("Theme for calculator is not available!");
        }

        JFrame frame = new JFrame("Simple Calculator");
        frame.setSize(350, 450);
        frame.setContentPane(new CalculatorView()); //CalculatorView view created with code
        //frame.setContentPane(new MyCalculator().getPanel1()); //MyCalculator form created with IntelliJ IDEA GUI Designer
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(350, 450));
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
    }
}
