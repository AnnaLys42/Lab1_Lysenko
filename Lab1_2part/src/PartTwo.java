import java.lang.reflect.*;
import java.util.*;

public class PartTwo {
    public static void analyzeObject(Object obj) {
// отримання класу
        Class<?> clazz = obj.getClass();
        System.out.println("Тип об'єкта: " + clazz.getName());
        // Поля
        System.out.println("\nПоля:");
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            try {
                field.setAccessible(true); // Отримання полей (та приватних)
                System.out.println(
                        Modifier.toString(field.getModifiers()) + " "
                                + field.getType().getSimpleName() + " "
                                + field.getName() + " = "
                                + field.get(obj)
                );
            } catch (Exception e) {
                System.out.println(field.getName() + "не знайдено");
            }
        }

        // Методи
        System.out.println("\nPublic методи без параметрів:");

        Method[] methods = clazz.getDeclaredMethods(); // отримання всіх методів
        List<Method> callable = new ArrayList<>();

        int index = 1;
// фільтрує, залишає лише без параметрів та public
        for (Method m : methods) {
            if (Modifier.isPublic(m.getModifiers()) && m.getParameterCount() == 0) {

                callable.add(m); // додає у список

                System.out.println(index + ". "
                        + m.getReturnType().getSimpleName()
                        + " "
                        + m.getName() + "()");
                index++;
            }
        }

        if (callable.isEmpty()) {
            System.out.println("Немає доступних методів для виклику.");
            return;
        }

        Scanner scanner = new Scanner(System.in);

        System.out.print("\nВиберіть метод для виклику: ");
        int choice = scanner.nextInt();

        if (choice > 0 && choice <= callable.size()) {
            try {

                Method method = callable.get(choice - 1);

                Object result = method.invoke(obj);

                System.out.println("\nРезультат виклику: " + result);

            } catch (Exception e) {
                System.out.println("Помилка виклику " + e.getMessage());
            }
        }
    }

    // Тест
    static class Person {

        private int age = 20;
        public String name = "Anna";

        public String sayHello() {
            return "Hello!";
        }

        public void printInfo() {
            System.out.println("Name: " + name + ", Age: " + age);
        }

        private void secret() {
        }
    }

    public static void main(String[] args) {
        Person person = new Person();
        analyzeObject(person);
    }
}