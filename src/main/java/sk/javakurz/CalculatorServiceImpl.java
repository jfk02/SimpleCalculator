package sk.javakurz;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Callable;

import static sk.javakurz.Flag.OPERATIONS_FLAGS;

enum Flag {
    OPERATOR, RESULT, MEMORY_RECALL;
    public static final EnumSet<Flag> OPERATIONS_FLAGS = EnumSet.of(Flag.OPERATOR, Flag.RESULT);
}

public class CalculatorServiceImpl implements CalculatorService {

    public CalculatorServiceImpl() {
    }

    //region Fields
    private String inputRegister = "0";
    private final LinkedList<BigDecimal> operandsQueue = new LinkedList<>();
    private String operatorRegister = "";
    private final EnumSet<Flag> statusRegister = EnumSet.noneOf(Flag.class);
    private BigDecimal memoryRegister = BigDecimal.ZERO;

    private final BigDecimal hundredPercent = new BigDecimal("100");
    private final MathContext mathContext = new MathContext(10);
    private final Map<String, Callable<BigDecimal>> mathOperations = Map.of(
            "+", () -> operandsQueue.removeFirst().add(operandsQueue.peekLast(), mathContext),
            "−", () -> operandsQueue.removeFirst().subtract(operandsQueue.peekLast(), mathContext),
            "×", () -> operandsQueue.removeFirst().multiply(operandsQueue.peekLast(), mathContext),
            "÷", () -> operandsQueue.removeFirst().divide(operandsQueue.peekLast(), mathContext),
            "+%", () -> {
                var firstOperand = operandsQueue.removeFirst();
                BigDecimal secondOperand = calculatePercentageOf(firstOperand, operandsQueue.peekLast());
                return firstOperand.add(secondOperand, mathContext);
            },
            "−%", () -> {
                var firstOperand = operandsQueue.removeFirst();
                BigDecimal secondOperand = calculatePercentageOf(firstOperand, operandsQueue.peekLast());
                return firstOperand.subtract(secondOperand, mathContext);
            },
            "×%", () -> operandsQueue.removeFirst()
                    .multiply(operandsQueue.peekLast(), mathContext)
                    .divide(hundredPercent, mathContext),
            "÷%", () -> operandsQueue.removeFirst()
                    .divide(operandsQueue.peekLast(), mathContext)
                    .multiply(hundredPercent, mathContext)
    );
//endregion

    //region Public methods
    @Override
    public String evaluateNumberInput(String pressedNumber) {
        clearAfterResultOrOperator();
        statusRegister.remove(Flag.MEMORY_RECALL);
        inputRegister += inputRegister.length() < 15 ? pressedNumber : "";
        inputRegister = removeLeadingZero(inputRegister);
        return inputRegister;
    }

    @Override
    public String evaluateOperatorInput(String operator) {
        String resultText = inputRegister;
        statusRegister.remove(Flag.MEMORY_RECALL);

        if (!statusRegister.contains(Flag.OPERATOR)) {
            operandsQueue.add(stringToBigDecimal(inputRegister));
            if (operandsQueue.size() > 2)
                operandsQueue.removeFirst();
            if (!statusRegister.contains(Flag.RESULT)) {
                if (operandsQueue.size() == 2) {
                    resultText = calculate();
                    operandsQueue.add(stringToBigDecimal(inputRegister));
                }
            } else {
                statusRegister.remove(Flag.RESULT);
            }
        }
        statusRegister.add(Flag.OPERATOR);
        operatorRegister = operator;
        return resultText;
    }

    @Override
    public void allClear() {
        operandsQueue.clear();
        statusRegister.clear();
        inputRegister = "0";
    }

    @Override
    public String addPeriod() {
        statusRegister.remove(Flag.MEMORY_RECALL);
        if (!statusRegister.contains(Flag.RESULT) && !statusRegister.contains(Flag.OPERATOR)) {
            inputRegister += !inputRegister.contains(".") ? "." : "";
        } else {
            clearAfterResultOrOperator();
            inputRegister = "0.";
        }
        return inputRegister;
    }

    @Override
    public String performCalculation(String pressedButton) {
        String resultText = inputRegister;
        statusRegister.remove(Flag.MEMORY_RECALL);
        var secondOperand = stringToBigDecimal(inputRegister);
        switch (operandsQueue.size()) {
            case 1 -> {
                operatorRegister += addPercentageOperator(pressedButton);

                if (statusRegister.contains(Flag.RESULT))
                    operandsQueue.addFirst(secondOperand);
                else
                    operandsQueue.add(secondOperand);

                resultText = calculate();
            }
            case 2 -> {
                if (statusRegister.contains(Flag.RESULT))
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
        statusRegister.remove(Flag.MEMORY_RECALL);
        inputRegister = inputRegister.startsWith("-") ? inputRegister.substring(1) : "-" + inputRegister;
        if (statusRegister.contains(Flag.RESULT)) {
            operandsQueue.removeLast();
            operandsQueue.add(stringToBigDecimal(inputRegister));
        }
        return inputRegister;
    }

    @Override
    public String clearLastNumber() {
        statusRegister.remove(Flag.MEMORY_RECALL);
        if (!statusRegister.contains(Flag.RESULT)) {
            inputRegister = inputRegister.substring(0, inputRegister.length() - 1);
        }
        return inputRegister;
    }

    @Override
    public String[] evaluateMemoryOperation(String memoryButton) {
        String[] result = new String[]{inputRegister, "M"};
        switch (memoryButton) {
            case "MRC" -> {
                if (statusRegister.contains(Flag.MEMORY_RECALL)) {
                    memoryRegister = BigDecimal.ZERO;
                } else {
                    statusRegister.add(Flag.MEMORY_RECALL);
                    result[0] = memoryRegister.toEngineeringString();
                    inputRegister = result[0];
                }
                if (memoryRegister.stripTrailingZeros().equals(BigDecimal.ZERO)) {
                    memoryRegister = BigDecimal.ZERO;
                    result[1] = "";
                }
            }
            case "M+" -> {
                statusRegister.remove(Flag.MEMORY_RECALL);
                memoryRegister = memoryRegister.add(stringToBigDecimal(inputRegister)).stripTrailingZeros();
                if (memoryRegister.equals(BigDecimal.ZERO)) {
                    memoryRegister = BigDecimal.ZERO;
                    result[1] = "";
                }
            }
            case "M−" -> {
                statusRegister.remove(Flag.MEMORY_RECALL);
                memoryRegister = memoryRegister.subtract(stringToBigDecimal(inputRegister)).stripTrailingZeros();
                if (memoryRegister.equals(BigDecimal.ZERO)) {
                    memoryRegister = BigDecimal.ZERO;
                    result[1] = "";
                }
            }
        }
        return result;
    }

    @Override
    public String calculateSquareRoot() {
        String resultText;
        statusRegister.remove(Flag.MEMORY_RECALL);
        try {
            inputRegister = new BigDecimal(inputRegister).sqrt(mathContext)
                    .stripTrailingZeros()
                    .toEngineeringString();
            resultText = inputRegister;
        } catch (ArithmeticException e) {
            resultText = "Error";
        }
        return resultText;
    }
//endregion

    //region Private methods
    private String calculate() {
        String resultText;
        statusRegister.clear();
        try {
            BigDecimal result = mathOperations.get(operatorRegister).call().stripTrailingZeros();
            inputRegister = result.toEngineeringString();
            resultText = inputRegister;
            statusRegister.add(Flag.RESULT);
        } catch (Exception e) {
            inputRegister = "0";
            resultText = "Error";
        }
        return resultText;
    }

    private void clearAfterResultOrOperator() {
        if (statusRegister.containsAll(OPERATIONS_FLAGS)) {
            statusRegister.clear();
            inputRegister = "";
        } else if (statusRegister.contains(Flag.OPERATOR)) {
            statusRegister.remove(Flag.OPERATOR);
            inputRegister = "";
        } else if (statusRegister.contains(Flag.RESULT)) {
            operandsQueue.clear();
            statusRegister.remove(Flag.RESULT);
            inputRegister = "";
        }
    }

    private String addPercentageOperator(String button) {
        return button.equals("%") && operatorRegister.length() == 1 ? "%" : "";
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

    private BigDecimal calculatePercentageOf(BigDecimal firstOperand, BigDecimal secondOperand) {
        var result = BigDecimal.ZERO;
        if (secondOperand != null) {
            result = secondOperand.divide(hundredPercent, mathContext)
                    .multiply(firstOperand, mathContext)
                    .stripTrailingZeros();
        }
        return result;
    }
//endregion
}