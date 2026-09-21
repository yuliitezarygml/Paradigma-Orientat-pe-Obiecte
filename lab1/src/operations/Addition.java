package operations;

// Класс сложения (a + b)
public class Addition extends BinaryOperation {
    @Override
    public String getName() {
        return "+";
    }

    @Override
    public double calculate(double a, double b) {
        return a + b;
    }
}

