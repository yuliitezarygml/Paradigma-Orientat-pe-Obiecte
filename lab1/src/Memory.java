// Класс для работы с памятью калькулятора (MC, MR, M+, M-, MS)
// Принцип ООП: Инкапсуляция (переменные private, доступ через методы)
public class Memory {

    // Скрытое значение памяти
    private double memoryValue = 0.0;
    
    // Флаг: сохранено ли что-то в памяти
    private boolean hasValue = false;

    // MC - очистить память
    public void clear() {
        memoryValue = 0.0;
        hasValue = false;
    }

    // MR - прочитать значение из памяти
    public double recall() {
        return memoryValue;
    }

    // MS - записать число в память
    public void store(double value) {
        memoryValue = value;
        hasValue = true;
    }

    // M+ - прибавить к памяти
    public void add(double value) {
        memoryValue += value;
        hasValue = true;
    }

    // M- - вычесть из памяти
    public void subtract(double value) {
        memoryValue -= value;
        hasValue = true;
    }

    // Проверка, есть ли число в памяти (для индикатора "M" на экране)
    public boolean hasValue() {
        return hasValue;
    }
}

