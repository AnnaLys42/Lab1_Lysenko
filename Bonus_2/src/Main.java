import java.lang.reflect.*;
public class Main {

    // Серілізація
    public static String serialize(Object obj) {

        Class<?> clazz = obj.getClass();
        StringBuilder sb = new StringBuilder();

        sb.append(clazz.getName());

        for (Field field : clazz.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                sb.append(";")
                        .append(field.getName())
                        .append("=")
                        .append(field.get(obj));
            } catch (Exception e) {
            }
        }

        return sb.toString();
    }

    // Десеріалізація
    public static Object deserialize(String data) throws Exception {

        String[] parts = data.split(";");

        Class<?> clazz = Class.forName(parts[0]);

        Object obj = clazz.getDeclaredConstructor().newInstance();

        for (int i = 1; i < parts.length; i++) {

            String[] pair = parts[i].split("=");

            String name = pair[0];
            String value = pair[1];

            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);

            field.set(obj, parse(field.getType(), value));
        }

        return obj;
    }

    // Парсинг
    private static Object parse(Class<?> type, String val) {

        if (type == int.class || type == Integer.class) return Integer.parseInt(val);
        if (type == double.class || type == Double.class) return Double.parseDouble(val);
        if (type == boolean.class || type == Boolean.class) return Boolean.parseBoolean(val);
        if (type == String.class) return val;

        return null;
    }

    // Тестовий клас
    static class Person {
        private int age;
        private String name;

        public Person() {}

        public Person(int age, String name) {
            this.age = age;
            this.name = name;
        }

        public String toString() {
            return "Person age: " + age + ", name:'" + name + "'";
        }
    }

    // Головний
    public static void main(String[] args) throws Exception {

        Person p = new Person(19, "Katya");

        // Сереалізація
        String data = serialize(p);
        System.out.println("Serialized: " + data);

        // Десеріалізаія
        Object restored = deserialize(data);
        System.out.println("Restored: " + restored);
    }
}