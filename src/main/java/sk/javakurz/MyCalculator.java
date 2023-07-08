package sk.javakurz;

import javax.swing.*;
import java.awt.event.ActionListener;

public class MyCalculator {

    private JPanel panel1;

    public JPanel getPanel1() {
        return panel1;
    }

    private JTextField display;
    private JButton a4Button;
    private JButton a7Button;
    private JButton a1Button;
    private JButton periodButton;
    private JButton a8Button;
    private JButton a5Button;
    private JButton a2Button;
    private JButton a0Button;
    private JButton a9Button;
    private JButton plusButton;
    private JButton a6Button;
    private JButton minusButton;
    private JButton a3Button;
    private JButton multiplicationButton;
    private JButton equalButton;
    private JButton divideButton;
    private JButton cButton;
    private JButton backspaceButton;
    private JButton plusMinusButton;
    private JButton percentageButton;

    CalculatorService calculatorService;

    private ActionListener createNumbersListener() {
        return e -> display.setText(calculatorService.doNumbersAction(e.getActionCommand()));
    }

    private ActionListener createOperatorsListener() {
        return e -> display.setText(calculatorService.doOperatorsAction(e.getActionCommand()));
    }

    private ActionListener createClearButtonListener() {
        return e -> {
            display.setText("0");
            calculatorService.allClear();
        };
    }

    private ActionListener createPeriodButtonListener() {
        return e -> display.setText(calculatorService.addPeriod());
    }

    private ActionListener createPerformCalculationListener() {
        return e -> display.setText(calculatorService.performCalculation(e.getActionCommand()));
    }

    private ActionListener createPlusMinusListener() {
        return e -> display.setText(calculatorService.addMinusSign());
    }

    private ActionListener createBackspaceListener() {
        return e -> display.setText(calculatorService.doBackspace());
    }

    public MyCalculator() {

        calculatorService = new CalculatorServiceImpl();

        var numbersListener = createNumbersListener();
        a0Button.addActionListener(numbersListener);
        a1Button.addActionListener(numbersListener);
        a2Button.addActionListener(numbersListener);
        a3Button.addActionListener(numbersListener);
        a4Button.addActionListener(numbersListener);
        a5Button.addActionListener(numbersListener);
        a6Button.addActionListener(numbersListener);
        a7Button.addActionListener(numbersListener);
        a8Button.addActionListener(numbersListener);
        a9Button.addActionListener(numbersListener);

        var operatorsListener = createOperatorsListener();
        plusButton.addActionListener(operatorsListener);
        minusButton.addActionListener(operatorsListener);
        multiplicationButton.addActionListener(operatorsListener);
        divideButton.addActionListener(operatorsListener);

        var performCalculationListener = createPerformCalculationListener();
        equalButton.addActionListener(performCalculationListener);
        percentageButton.addActionListener(performCalculationListener);

        periodButton.addActionListener(createPeriodButtonListener());

        cButton.addActionListener(createClearButtonListener());

        plusMinusButton.addActionListener(createPlusMinusListener());

        backspaceButton.addActionListener(createBackspaceListener());

        display.setText("0");
    }
}
