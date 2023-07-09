package sk.javakurz;

import org.javatuples.Triplet;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class CalculatorView extends JPanel {

    private final Font numberButtonFont = new Font("Arial", Font.BOLD, 24);
    private final Font buttonFont = new Font("Arial", Font.PLAIN, 24);
    private final Font displayFont = new Font("Arial Narrow", Font.PLAIN, 38);
    private final Color operatorButtonColor = Color.decode("#D0D0D0");
    private final Color equalButtonColor = Color.decode("#C0F0C0");
    private final Color clearButtonColor = Color.decode("#D00000");

    public CalculatorView() {
        calculatorService = new CalculatorServiceImpl();
        createComponents();
    }

    private JTextField display;
    CalculatorService calculatorService;
    private final GridBagConstraints constraints = new GridBagConstraints();
    private final Triplet<String, Integer, Integer>[] numberButtons = new Triplet[]{
            new Triplet<>("7", 0, 2),
            new Triplet<>("8", 1, 2),
            new Triplet<>("9", 2, 2),
            new Triplet<>("4", 0, 3),
            new Triplet<>("5", 1, 3),
            new Triplet<>("6", 2, 3),
            new Triplet<>("1", 0, 4),
            new Triplet<>("2", 1, 4),
            new Triplet<>("3", 2, 4),
            new Triplet<>("0", 1, 5)
    };

    private final Triplet<String, Integer, Integer>[] operatorButtons = new Triplet[]{
            new Triplet<>("÷", 3, 1),
            new Triplet<>("x", 3, 2),
            new Triplet<>("-", 3, 3),
            new Triplet<>("+", 3, 4)
    };

    private void createComponents() {
        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.weighty = 0.7;
        constraints.gridwidth = 4;

        constraints.insets = new Insets(0, 0, 10, 0);

        display = new JTextField();
        addComponent(display, 0, 0);
        display.setEditable(false);
        display.setFocusable(true);
        display.setBackground(Color.white);
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setFont(displayFont);

        constraints.gridwidth = 1;
        constraints.weighty = 0.5;
        constraints.insets = new Insets(0, 0, 0, 0);

        createNumberButtons();
        createOperatorButtons();

        JButton cButton = new JButton("AC");
        addComponent(cButton, 0, 1);
        cButton.setForeground(clearButtonColor);
        cButton.addActionListener(createClearButtonListener());

        JButton backspaceButton = new JButton("<");
        addComponent(backspaceButton, 1, 1);
        backspaceButton.addActionListener(createBackspaceListener());

        JButton plusMinusButton = new JButton("+/-");
        addComponent(plusMinusButton, 2, 1);
        plusMinusButton.addActionListener(createPlusMinusListener());

        JButton percentageButton = new JButton("%");
        addComponent(percentageButton, 0, 5);

        JButton equalButton = new JButton("=");
        addComponent(equalButton, 3, 5);
        equalButton.setBackground(equalButtonColor);

        var performCalculationListener = createPerformCalculationListener();
        equalButton.addActionListener(performCalculationListener);
        percentageButton.addActionListener(performCalculationListener);


        JButton periodButton = new JButton(".");
        addComponent(periodButton, 2, 5);
        periodButton.addActionListener(createPeriodButtonListener());
    }

    private void createNumberButtons() {
        var numbersListener = createNumbersListener();
        Arrays.stream(numberButtons).forEach(button -> {
            var numButton = new JButton(button.getValue0());
            addComponent(numButton, button.getValue1(), button.getValue2());
            numButton.setFont(numberButtonFont);
            numButton.addActionListener(numbersListener);
        });
    }

    private void createOperatorButtons() {
        var operatorsListener = createOperatorsListener();
        Arrays.stream(operatorButtons).forEach(button -> {
            var operButton = new JButton(button.getValue0());
            addComponent(operButton, button.getValue1(), button.getValue2());
            operButton.setBackground(operatorButtonColor);
            operButton.addActionListener(operatorsListener);
        });
    }

    private void addComponent(Component component, int x, int y) {
        constraints.gridx = x;
        constraints.gridy = y;
        add(component, constraints);
        component.setPreferredSize(new Dimension(2, 2));
        component.setFocusable(false);
        component.setFont(buttonFont);
    }

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
}
