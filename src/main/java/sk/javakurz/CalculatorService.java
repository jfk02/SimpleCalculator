package sk.javakurz.swing.calculator;

public interface CalculatorService {
    String doNumbersAction(String pressedNumber);

    String doOperatorsAction(String operator);

    void allClear();

    String addPeriod();

    String performCalculation(String pressedButton);

    String addMinusSign();

    String doBackspace();
}
