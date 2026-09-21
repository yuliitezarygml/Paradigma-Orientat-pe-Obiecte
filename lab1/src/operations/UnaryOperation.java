package operations;

// Абстрактный класс для операций с одним числом (корень, sin, cos, log и т.д.)
// Принцип ООП: Наследование и Абстракция
public abstract class UnaryOperation implements Operation {

    // Метод, который каждый конкретный класс реализует по-своему (Полиморфизм)
    public abstract double calculate(double a) throws Exception;
}

