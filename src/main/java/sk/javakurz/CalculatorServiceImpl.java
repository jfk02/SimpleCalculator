package sk.javakurz.swing.calculator;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Callable;

public class CalculatorServiceImpl implements CalculatorService {

    public CalculatorServiceImpl() {
    }

    private String displayCache = "0";
    private final LinkedList<BigDecimal> operandsQueue = new LinkedList<>();
    private String mathOperator = "";
    private boolean hasResult = false;
    private final BigDecimal hundredPercent = new BigDecimal("100");
    private final MathContext mathContext = new MathContext(10);
    private final HashMap<String, Callable<BigDecimal>> mathEvaluators = new HashMap<>() {
        {
            put("+", () -> operandsQueue.removeFirst().add(operandsQueue.peekLast(), mathContext));
            put("-", () -> operandsQueue.removeFirst().subtract(operandsQueue.peekLast(), mathContext));
            put("x", () -> operandsQueue.removeFirst().multiply(operandsQueue.peekLast(), mathContext));
            put("÷", () -> operandsQueue.removeFirst().divide(operandsQueue.peekLast(), mathContext));
            put("+%", () -> {
                var firstOperand = operandsQueue.removeFirst();
                var secondOperand = operandsQueue.peekLast().divide(hundredPercent, mathContext)
                        .multiply(firstOperand);
                return firstOperand.add(secondOperand, mathContext);
            });
            put("-%", () -> {
                var firstOperand = operandsQueue.removeFirst();
                var secondOperand = operandsQueue.peekLast().divide(hundredPercent, mathContext)
                        .multiply(firstOperand);
                return firstOperand.subtract(secondOperand, mathContext);
            });
            put("x%", () -> operandsQueue.removeFirst()
                    .multiply(operandsQueue.peekLast(), mathContext)
                    .divide(hundredPercent, mathContext));
            put("÷%", () -> operandsQueue.removeFirst()
                    .divide(operandsQueue.peekLast(), mathContext)
                    .multiply(hundredPercent, mathContext));
        }
    };

    @Override
    public String doNumbersAction(String pressedNumber) {
        clearAfterResult();
        displayCache += displayCache.length() < 15 ? pressedNumber : "";
        displayCache = removeLeadingZero(displayCache);
        return displayCache;
    }

    @Override
    public String doOperatorsAction(String operator) {
        String resultText = "0";

        if (!hasResult) {
            operandsQueue.clear();
            operandsQueue.add(stringToBigDecimal(displayCache));
            displayCache = "0";
        } else operandsQueue.removeFirst();

        if (operandsQueue.size() == 2) resultText = calculate();

        mathOperator = operator;
        return resultText;
    }

    @Override
    public void allClear() {
        operandsQueue.clear();
        hasResult = false;
        displayCache = "0";
    }

    @Override
    public String addPeriod() {
        if (!hasResult) {
            displayCache += !displayCache.contains(".") ? "." : "";
        } else {
            clearAfterResult();
            displayCache = "0.";
        }
        return displayCache;
    }

    @Override
    public String performCalculation(String pressedButton) {
        String resultText = displayCache;
        var secondOperand = stringToBigDecimal(displayCache);
        switch (operandsQueue.size()) {
            case 1 -> {
                mathOperator += addPercentageOperator(pressedButton);
                operandsQueue.add(secondOperand);
                resultText = calculate();
            }
            case 2 -> {
                if (hasResult)
                    operandsQueue.add(operandsQueue.removeFirst());
                else {
                    operandsQueue.removeFirst();
                    operandsQueue.add(secondOperand);
                }
                resultText = calculate();
            }
        }
        return resultText;
    }

    @Override
    public String addMinusSign() {
        displayCache = displayCache.startsWith("-") ? displayCache.substring(1) : "-" + displayCache;
        if (hasResult) {
            operandsQueue.removeLast();
            operandsQueue.add(stringToBigDecimal(displayCache));
        }
        return displayCache;
    }

    @Override
    public String doBackspace() {
        if (!hasResult) {
            displayCache = displayCache.substring(0, displayCache.length() - 1);
        }
        return displayCache;
    }

    private String calculate() {
        hasResult = false;
        String resultText = "0";
        try {
            BigDecimal result = mathEvaluators.get(mathOperator).call();
            displayCache = result.toString();
            operandsQueue.add(stringToBigDecimal(displayCache));
            resultText = displayCache;
            hasResult = true;
        } catch (Exception e) {
            displayCache = "0";
            resultText = "Error";
        }
        return resultText;
    }

    private void clearAfterResult() {
        if (hasResult) {
            hasResult = false;
            displayCache = "0";
        }
    }

    private String addPercentageOperator(String button) {
        return button.equals("%") && mathOperator.length() == 1 ? "%" : "";
    }

    private String removeLeadingZero(String number) {

        if (number.length() > 1)
            if (number.startsWith("0") && !number.startsWith("0.")) {
                number = number.substring(1);
            }
        return number;
    }

    private BigDecimal stringToBigDecimal(String number) {
        number = number == null ? "0" : number;
        return new BigDecimal(number);
    }
}
