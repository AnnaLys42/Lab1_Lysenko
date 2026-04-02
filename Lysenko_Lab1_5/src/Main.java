import java.lang.reflect.*;

// Інтерфейс
// Метод для обчислення значення функції в точці x
interface Evaluatable {
    double evalf(double x);
}

// Клас, що реалізує функцію exp(-|a| * x) * sin(x)
class ExpSinFunction implements Evaluatable {
    private final double a;  // Параметр a у формулі
    // Ініціалізує параметр a
    public ExpSinFunction(double a) {
        this.a = a;
    }

    // Реалізація методу, що обчислює значення ф
    @Override
    public double evalf(double x) {
        return Math.exp(-Math.abs(a) * x) * Math.sin(x);
    }

    @Override
    public String toString() {
        return "Exp(-|" + a + "| * x) * sin(x)";
    }
}

// Клас, що реалізує функцію x^2
class SquareFunction implements Evaluatable {

    // Реалізація методу, який повертає квадрат x
    @Override
    public double evalf(double x) {
        return x * x;
    }

    @Override
    public String toString() {
        return "x * x";
    }
}

// Клас для вимірювання часу
class ProfilingHandler implements InvocationHandler {
    private final Object target;  // Реальний об'єкт, який ми обгортаємо

    public ProfilingHandler(Object target) {
        this.target = target;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        long start = System.nanoTime();
        Object result = method.invoke(target, args);
        long end = System.nanoTime();
        System.out.println("[" + target + "]." + method.getName()
                + " took " + (end - start) + " ns");
        return result;
    }
}

// Клас-обробник для трасування (виведення параметрів та результату)
class TracingHandler implements InvocationHandler {

    private final Object target;  // Реальний об'єкт, який ми обгортаємо

    // Конструктор приймає цільовий об'єкт
    public TracingHandler(Object target) {
        this.target = target;
    }

    // Основний метод, який викликається при зверненні до проксі
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Object result = method.invoke(target, args);

        // Виводимо інформацію
        System.out.print("[" + target + "]." + method.getName() + "(");

        // Вивід параметрів, якщо є:
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                if (i > 0) System.out.print(", ");
                System.out.print(args[i]);
            }
        }

        System.out.println(") = " + result);

        return result;
    }
}

// Головний клас
public class Main {
    public static void main(String[] args) {
        // Створюємо реальні об'єкти функцій
        Evaluatable f1 = new ExpSinFunction(2.5);  // Функція з параметром a=2.5
        Evaluatable f2 = new SquareFunction();      // Квадратична функція
        double x = 1.0;  // Точка, в якій обчислюємо значення

        // Без проксі - просто викликаємо методи напряму
        System.out.println("F1: " + f1.evalf(x));
        System.out.println("F2: " + f2.evalf(x));

        // Створюємо проксі для функції f1
        Evaluatable f1Profile = (Evaluatable) Proxy.newProxyInstance(
                Evaluatable.class.getClassLoader(),  // Завантажувач класів
                new Class[]{Evaluatable.class},      // Інтерфейси, які реалізує проксі
                new ProfilingHandler(f1)              // Обробник викликів
        );

        // Для функції f2
        Evaluatable f2Profile = (Evaluatable) Proxy.newProxyInstance(
                Evaluatable.class.getClassLoader(),
                new Class[]{Evaluatable.class},
                new ProfilingHandler(f2)
        );

        // Викликаємо методи через проксі - автоматично вимірюється час
        System.out.println("F1: " + f1Profile.evalf(x));
        System.out.println("F2: " + f2Profile.evalf(x));

        // Проксі для трасування функції f1
        Evaluatable f1Trace = (Evaluatable) Proxy.newProxyInstance(
                Evaluatable.class.getClassLoader(),
                new Class[]{Evaluatable.class},
                new TracingHandler(f1)
        );

        // Проксі для трасування функції f2
        Evaluatable f2Trace = (Evaluatable) Proxy.newProxyInstance(
                Evaluatable.class.getClassLoader(),
                new Class[]{Evaluatable.class},
                new TracingHandler(f2)
        );

        // Викликаємо методи через проксі - автоматично виводиться трасування
        System.out.println("F1: " + f1Trace.evalf(x));
        System.out.println("F2: " + f2Trace.evalf(x));
    }
}