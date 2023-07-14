package sk.javakurz;

import org.javatuples.Pair;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

import static sk.javakurz.CalculatorView.*;

public class CalculatorView extends JPanel {

//region Constants
    protected static final Font NUMBER_BUTTON_FONT = new Font("Arial", Font.PLAIN, 28);
    protected static final Font SMALL_BUTTON_FONT = new Font("Cambria", Font.PLAIN, 20);
    protected static final Font BUTTON_FONT = new Font("Cambria", Font.PLAIN, 26);
//endregion

    public CalculatorView() {
        calculatorService = new CalculatorServiceImpl();
        createComponents();
    }

//region Fields
    private Display display;
    CalculatorService calculatorService;
    private final GridBagConstraints constraints = new GridBagConstraints();

    private final Pair<JButton, ActionListener>[][] calculatorBuutons = new Pair[][]{
            new Pair[]{
                    new Pair<>(new SmallButton("MR"), createMemoryListener()),
                    new Pair<>(new SmallButton("M+"), createMemoryListener()),
                    new Pair<>(new SmallButton("M−"), createMemoryListener()),
                    new Pair<>(new SmallButton("√"), createRootListener())
            },
            new Pair[]{
                    new Pair<>(new ClearButton(), createClearButtonListener()),
                    new Pair<>(new CalculatorButton("←"), createBackspaceListener()),
                    new Pair<>(new CalculatorButton("±"), createPlusMinusListener()),
                    new Pair<>(new CalculatorButton("÷"), createOperatorsListener())
            },
            new Pair[]{
                    new Pair<>(new NumberButton("7"), createNumbersListener()),
                    new Pair<>(new NumberButton("8"), createNumbersListener()),
                    new Pair<>(new NumberButton("9"), createNumbersListener()),
                    new Pair<>(new CalculatorButton("×"), createOperatorsListener())
            },
            new Pair[]{
                    new Pair<>(new NumberButton("4"), createNumbersListener()),
                    new Pair<>(new NumberButton("5"), createNumbersListener()),
                    new Pair<>(new NumberButton("6"), createNumbersListener()),
                    new Pair<>(new CalculatorButton("−"), createOperatorsListener())
            },
            new Pair[]{
                    new Pair<>(new NumberButton("1"), createNumbersListener()),
                    new Pair<>(new NumberButton("2"), createNumbersListener()),
                    new Pair<>(new NumberButton("3"), createNumbersListener()),
                    new Pair<>(new CalculatorButton("+"), createOperatorsListener())
            },
            new Pair[]{
                    new Pair<>(new CalculatorButton("%"), createPerformCalculationListener()),
                    new Pair<>(new NumberButton("0"), createNumbersListener()),
                    new Pair<>(new CalculatorButton("."), createPeriodButtonListener()),
                    new Pair<>(new EqualButton(), createPerformCalculationListener())
            }
    };
//endregion

//region Private methods
    private void createComponents() {
        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;

        constraints.weighty = 0.7;
        constraints.gridwidth = 4;
        constraints.insets = new Insets(0, 0, 10, 0);

        display = new Display();
        addComponent(display, 0, 0);
        display.setDisplayText("0");
        display.setIndicatorsText("");

        constraints.gridwidth = 1;
        constraints.insets = new Insets(0, 0, 0, 0);

        addButtons();
    }

    private void addButtons() {
        for (int y = 0; y < 6; y++) {
            constraints.weighty = y == 0 ? 0.4 : 0.5;
            for (int x = 0; x < 4; x++) {
                var button = calculatorBuutons[y][x].getValue0();
                addComponent(button, x, y + 1);
                button.addActionListener(calculatorBuutons[y][x].getValue1());
            }
        }
    }

    private void addComponent(Component component, int x, int y) {
        constraints.gridx = x;
        constraints.gridy = y;
        add(component, constraints);
        component.setPreferredSize(new Dimension(2, 2));
        component.setFocusable(false);
    }
//endregion

//region ActionListeners
    private ActionListener createNumbersListener() {
        return e -> display.setDisplayText(calculatorService.evaluateNumberInput(e.getActionCommand()));
    }

    private ActionListener createOperatorsListener() {
        return e -> display.setDisplayText(calculatorService.evaluateOperatorInput(e.getActionCommand()));
    }

    private ActionListener createClearButtonListener() {
        return e -> {
            display.setDisplayText("0");
            calculatorService.allClear();
        };
    }

    private ActionListener createPeriodButtonListener() {
        return e -> display.setDisplayText(calculatorService.addPeriod());
    }

    private ActionListener createPerformCalculationListener() {
        return e -> display.setDisplayText(calculatorService.performCalculation(e.getActionCommand()));
    }

    private ActionListener createPlusMinusListener() {
        return e -> display.setDisplayText(calculatorService.addMinusSign());
    }

    private ActionListener createBackspaceListener() {
        return e -> display.setDisplayText(calculatorService.clearLastNumber());
    }

    private ActionListener createMemoryListener() {
        return e -> {
            var displayText = calculatorService.evaluateMemoryOperation(e.getActionCommand());
            display.setDisplayText(displayText[0]);
            display.setIndicatorsText(displayText[1]);
        };
    }

    private ActionListener createRootListener() {
        return e -> display.setDisplayText(calculatorService.calculateSquareRoot());
    }
//endregion
}

class NumberButton extends JButton {
    public NumberButton(String number) {
        super(number);
        setFont(NUMBER_BUTTON_FONT);
    }
}

class CalculatorButton extends JButton {
    public CalculatorButton(String sign) {
        super(sign);
        setFont(BUTTON_FONT);
        setBackground(Color.decode("#E0E0E0"));
    }
}

class SmallButton extends CalculatorButton {
    public SmallButton(String sign) {
        super(sign);
        setFont(SMALL_BUTTON_FONT);
    }
}

class ClearButton extends CalculatorButton {
    public ClearButton() {
        super("AC");
        setForeground(Color.decode("#D00000"));
    }
}

class EqualButton extends CalculatorButton {
    public EqualButton() {
        super("=");
        setBackground(Color.decode("#C0F0C0"));
    }
}

class Display extends JComponent {
    JTextField display = new JTextField();
    JTextField indicators = new JTextField("M");

    public Display() {
        super();

        Font displayFont = new Font("Arial Narrow", Font.PLAIN, 38);
        Font indicatorsFont = new Font("Arial", Font.BOLD, 11);
        Color displayBackgroundColor = Color.decode("#F8F8F8");

        BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);

        setLayout(layout);
        add(indicators);
        add(display);
        setBorder(BorderFactory.createEtchedBorder(0));
        display.setEditable(false);
        display.setFocusable(true);
        display.setBackground(displayBackgroundColor);
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setFont(displayFont);
        display.setBorder(BorderFactory.createEmptyBorder(0, 3, 3, 3));

        indicators.setEditable(false);
        indicators.setFocusable(false);
        indicators.setBackground(displayBackgroundColor);
        indicators.setHorizontalAlignment(JTextField.RIGHT);
        indicators.setFont(indicatorsFont);
        indicators.setBorder(BorderFactory.createEmptyBorder(3, 3, 0, 3));
    }

    public void setDisplayText(String text) {
        display.setText(text);
    }

    public void setIndicatorsText(String text) {
        indicators.setText(text);
    }
}