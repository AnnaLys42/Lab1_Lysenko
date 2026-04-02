import java.lang.reflect.*;
import java.util.*;
public class Main {

    // Вивід стану об'єкта
    public static void printObjectState(Object obj) {
        if (obj == null) {
            System.out.println("Об'єкт = null");
            return;
        }

        Class<?> clazz = obj.getClass();
        System.out.println("\nСтан об'єкта (" + clazz.getName() + ")");

        // Виводимо поля з усієї ієрархії класів
        printFieldsRecursive(obj, clazz);
    }

    // Рекурсивний метод для виведення полів з урахуванням суперкласів
    private static void printFieldsRecursive(Object obj, Class<?> clazz) {
        if (clazz == null || clazz == Object.class) return;
        // Спочатку виводимо поля суперкласу
        printFieldsRecursive(obj, clazz.getSuperclass());

        // Потім поля поточного класу
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                System.out.println("  " + field.getName() + " = " + field.get(obj));
            } catch (Exception e) {
                System.out.println("  " + field.getName() + " = [Помилка: " + e.getMessage() + "]");
            }
        }
    }

    // Ввід параметрів з підтримкою різних типів
    public static Object[] readParams(Class<?>[] types, Scanner sc, String context) {
        if (types.length == 0) {
            System.out.println("(метод не має параметрів)");
            return new Object[0];
        }

        System.out.println("Введіть параметри для " + context + ":");
        Object[] params = new Object[types.length];

        for (int i = 0; i < types.length; i++) {
            System.out.print("Параметр " + (i + 1) + " (" + types[i].getSimpleName() + "): ");
            String input = sc.nextLine();
            params[i] = parseValue(types[i], input);
        }

        return params;
    }

    private static Object parseValue(Class<?> type, String val) {
        // Примітивні типи
        if (type == int.class || type == Integer.class) return Integer.parseInt(val);
        if (type == double.class || type == Double.class) return Double.parseDouble(val);
        if (type == boolean.class || type == Boolean.class) return Boolean.parseBoolean(val);
        if (type == long.class || type == Long.class) return Long.parseLong(val);
        if (type == float.class || type == Float.class) return Float.parseFloat(val);
        if (type == short.class || type == Short.class) return Short.parseShort(val);
        if (type == byte.class || type == Byte.class) return Byte.parseByte(val);
        if (type == char.class || type == Character.class) {
            if (val.length() > 0) return val.charAt(0);
            return '\0';
        }

        // Типи посилань
        if (type == String.class) return val;

        // Якщо тип не підтримується
        System.err.println("Тип " + type.getName() + " не підтримується.");
        return null;
    }

    // Допоміжний метод для безпечного виклику
    private static void waitForEnter(Scanner sc) {
        System.out.println("\nНатисніть Enter.");
        sc.nextLine();
    }

    // Головний
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        try {
            // 1. Введення імені класу
            System.out.print("Введіть повне ім'я класу (наприклад: java.util.ArrayList): ");
            String className = sc.nextLine();

            // Завантажуємо клас
            Class<?> clazz = Class.forName(className);
            System.out.println("\nКлас '" + clazz.getName() + "' завантажено.");

            // 2. Робота з конструкторами
            Constructor<?>[] constructors = clazz.getDeclaredConstructors();

            System.out.println("\nДоступні конструктори:");
            for (int i = 0; i < constructors.length; i++) {
                System.out.println(i + ": " + constructors[i]);
            }

            System.out.print("\nОберіть номер конструктора: ");
            int cIndex = Integer.parseInt(sc.nextLine());

            if (cIndex < 0 || cIndex >= constructors.length) {
                System.out.println("Неправильний номер.");
                return;
            }

            Constructor<?> constructor = constructors[cIndex];
            constructor.setAccessible(true);

            // Введення параметрів для конструктора
            Object[] cParams = readParams(constructor.getParameterTypes(), sc, "конструктора");

            // Створення об'єкта
            Object obj = constructor.newInstance(cParams);
            System.out.println("\nОб'єкт успішно створено!");

            // Виведення стану після створення
            printObjectState(obj);

            // 3. Робота з методами
            Method[] methods = clazz.getDeclaredMethods();

            System.out.println("\nДоступні методи.");
            for (int i = 0; i < methods.length; i++) {
                System.out.println(i + ": " + methods[i]);
            }

            System.out.print("\nОберіть номер методу: ");
            int mIndex = Integer.parseInt(sc.nextLine());

            if (mIndex < 0 || mIndex >= methods.length) {
                System.out.println("Неправильний номер.");
                return;
            }

            Method method = methods[mIndex];
            method.setAccessible(true);

            // Виведення стану перед викликом
            System.out.println("\nСтан об'єкта перед викликом методу.");
            printObjectState(obj);

            // Введення параметрів для методу
            Object[] mParams = readParams(method.getParameterTypes(), sc, "методу " + method.getName());

            // Виклик методу
            Object result = method.invoke(obj, mParams);
            System.out.println("\nРезультат виклику методу: " + result);

            // Виведення стану об'єкта після виклику
            System.out.println("\nСтан після виклику методу.");
            printObjectState(obj);

        } catch (ClassNotFoundException e) {
            System.err.println("Помилка: Клас не знайдено.");
        } catch (InstantiationException e) {
            System.err.println("Помилка: Неможливо створити " + e.getMessage());
        } catch (IllegalAccessException e) {
            System.err.println("Помилка: Немає доступу - " + e.getMessage());
        } catch (InvocationTargetException e) {
            System.err.println("Помилка виклику методу: " + e.getCause().getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Помилка: Неправильний формат числа - " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Помилка: " + e.getMessage());
            e.printStackTrace();
        } finally {
            sc.close();
        }
    }
}