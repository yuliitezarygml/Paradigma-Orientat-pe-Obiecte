package operations;

// Абстрактный класс для операций с двумя числами (сложение, вычитание и т.д.)
// Принцип ООП: Наследование и Абстракция
public abstract class BinaryOperation implements Operation {
    
    // Метод, который каждый конкретный класс должен реализовать сам (Полиморфизм)
    public abstract double calculate(double a, double b) throws Exception;
}

