package sk.javakurz;

public interface CalculatorService {
    String evaluateNumberInput(String pressedNumber);

    String evaluateOperatorInput(String operator);

    void allClear();

    String addPeriod();

    String performCalculation(String pressedButton);

    String addMinusSign();

    String clearLastNumber();

    String[] evaluateMemoryOperation(String memoryButton);

    String calculateSquareRoot();
}
