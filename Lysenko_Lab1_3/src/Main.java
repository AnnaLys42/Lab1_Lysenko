import java.lang.reflect.*;
import java.util.*;

class FunctionNotFoundException extends Exception {
    public FunctionNotFoundException(String message) {
        super(message);
    }
}

// Основний
public class Main {
    // Примітиви
    private static Class<?> wrap(Class<?> type) {
        if (!type.isPrimitive()) return type;
        if (type == int.class) return Integer.class;
        if (type == double.class) return Double.class;
        if (type == boolean.class) return Boolean.class;
        if (type == char.class) return Character.class;
        if (type == byte.class) return Byte.class;
        if (type == short.class) return Short.class;
        if (type == long.class) return Long.class;
        if (type == float.class) return Float.class;

        return type;
    }

    // Виклик
    public static Object invokeMethod(Object obj, String methodName, Object... args)
            throws FunctionNotFoundException {

        if (obj == null)
            throw new FunctionNotFoundException("Об'єкт = null");

        Class<?> clazz = obj.getClass();
        Method[] methods = clazz.getDeclaredMethods();

        Method bestMethod = null;
        int bestScore = -1;

        for (Method method : methods) {

            if (!method.getName().equals(methodName)) continue;

            Class<?>[] paramTypes = method.getParameterTypes();

            if (paramTypes.length != args.length) continue;

            int score = 0;
            boolean match = true;

            for (int i = 0; i < paramTypes.length; i++) {

                if (args[i] == null) {
                    if (paramTypes[i].isPrimitive()) {
                        match = false;
                        break;
                    }
                    continue;
                }

                Class<?> expected = wrap(paramTypes[i]);
                Class<?> actual = args[i].getClass();

                if (expected.equals(actual)) {
                    score += 2; // точний підрахунок
                } else if (expected.isAssignableFrom(actual)) {
                    score += 1;
                } else {
                    match = false;
                    break;
                }
            }

            if (match && score > bestScore) {
                bestScore = score;
                bestMethod = method;
            }
        }

        if (bestMethod == null) {
            throw new FunctionNotFoundException("Метод не знайдено.");
        }

        try {
            bestMethod.setAccessible(true);

            // Вивід як у методичці
            System.out.println("Типи: " +
                    Arrays.toString(bestMethod.getParameterTypes()));
            System.out.println("Значення: " +
                    Arrays.toString(args));

            Object result = bestMethod.invoke(obj, args);

            System.out.println("Результат: " + result);

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Помилка: " + e.getMessage(), e);
        }
    }

    // Тест
    static class Test {

        public double f(double x) {
            return Math.exp(-Math.abs(x)) * Math.sin(x);
        }

        public double f(double x, int n) {
            return Math.exp(-Math.abs(x)) * Math.sin(x);
        }
    }

    public static void main(String[] args) {

        Test t = new Test();

        try {
            System.out.println("TestClass");

            invokeMethod(t, "f", 1.0);
            System.out.println();

            invokeMethod(t, "f", 1.0, 1);

        } catch (FunctionNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}