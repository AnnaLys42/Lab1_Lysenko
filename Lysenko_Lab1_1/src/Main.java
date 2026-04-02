import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;
import java.util.Scanner;

public class Main {
    // Метод аналізу
    public static String Description(String className) {
        // Перевірка на примітив
        Class<?> primitiveClass = getPrimitiveClass(className);
        if (primitiveClass != null) {
            return Description(primitiveClass);
        }

        // Якщо ні - завантажує клас
        try {
            Class<?> clazz = Class.forName(className);
            return Description(clazz);
        } catch (ClassNotFoundException e) {
            return "Клас не знайдено.";
        }
    }
    private static Class<?> getPrimitiveClass(String name) {
        switch (name) {
            case "int": return int.class;
            case "double": return double.class;
            case "boolean": return boolean.class;
            case "char": return char.class;
            case "byte": return byte.class;
            case "short": return short.class;
            case "long": return long.class;
            case "float": return float.class;
            case "void": return void.class;
            default: return null;
        }
    }
    public static String Description(Class<?> clazz) {
        StringBuilder result = new StringBuilder();

        // Інформація про пакет
        Package pkg = clazz.getPackage();
        result.append("Пакет: ").append(pkg != null ? pkg.getName() : "(немає пакету)").append("\n\n");

        // Модифікатори та ім'я класу
        result.append("Модифікатори: ").append(Modifier.toString(clazz.getModifiers())).append("\n");
        result.append("Ім'я класу: ").append(clazz.getSimpleName()).append("\n");

        // Базовий клас
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null && !superClass.equals(Object.class)) {
            result.append("Базовий клас: ").append(superClass.getName()).append("\n");
        } else if (clazz.isInterface()) {
            result.append("Тип: Інтерфейс\n");
        } else if (clazz.isPrimitive()) {
            result.append("Тип: Примітивний (").append(clazz.getName()).append(")\n");
            return result.toString();
        } else if (clazz.isArray()) {
            result.append("Тип: Масив ").append(clazz.getComponentType().getName()).append("[]\n");
            return result.toString();
        }

        // Реалізовані інтерфейси
        Class<?>[] interfaces = clazz.getInterfaces();
        if (interfaces.length > 0) {
            result.append("Реалізовані інтерфейси:\n");
            for (Class<?> iface : interfaces) {
                result.append("  - ").append(iface.getName()).append("\n");
            }
        }

        // Поля
        result.append("\nПОЛЯ:\n");
        Field[] fields = clazz.getDeclaredFields();
        if (fields.length == 0) {
            result.append("  (немає полів)\n");
        } else {
            for (Field field : fields) {
                result.append("  ").append(Modifier.toString(field.getModifiers()))
                        .append(" ").append(field.getType().getSimpleName())
                        .append(" ").append(field.getName()).append("\n");
            }
        }

        // Конструктори
        result.append("\nКОНСТРУКТОРИ:\n");
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        if (constructors.length == 0) {
            result.append("  (немає конструкторів)\n");
        } else {
            for (Constructor<?> constructor : constructors) {
                result.append("  ").append(Modifier.toString(constructor.getModifiers()))
                        .append(" ").append(clazz.getSimpleName()).append("(");

                Parameter[] params = constructor.getParameters();
                for (int i = 0; i < params.length; i++) {
                    if (i > 0) result.append(", ");
                    result.append(params[i].getType().getSimpleName())
                            .append(" ").append(params[i].getName());
                }
                result.append(")\n");
            }
        }

        // Методи
        result.append("\nМетоди:\n");
        Method[] methods = clazz.getDeclaredMethods();
        if (methods.length == 0) {
            result.append("  (немає методів)\n");
        } else {
            for (Method method : methods) {
                result.append("  ").append(Modifier.toString(method.getModifiers()))
                        .append(" ").append(method.getReturnType().getSimpleName())
                        .append(" ").append(method.getName()).append("(");

                Parameter[] params = method.getParameters();
                for (int i = 0; i < params.length; i++) {
                    if (i > 0) result.append(", ");
                    result.append(params[i].getType().getSimpleName())
                            .append(" ").append(params[i].getName());
                }
                result.append(")\n");
            }
        }

        return result.toString();
    }

    // Консоль
    private static void runConsoleMode() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введіть повне ім'я класу (наприклад: java.lang.String) або 'exit' для виходу:");
        while (true) {
            System.out.print("\n> ");
            String className = scanner.nextLine().trim();

            if (className.equalsIgnoreCase("exit") || className.equalsIgnoreCase("quit")) {
                break;
            }
            if (className.isEmpty()) {
                System.out.println("Введіть ім'я класу");
                continue;
            }
            System.out.println("\n" + Description(className));
            System.out.println("----------------------------------------");
        }
        scanner.close();
    }

    // Графічний режим
    private static class GUIClassAnalyzer extends JFrame {
        private JTextField inputField;
        private JTextArea resultArea;
        private JButton analyzeButton, clearButton, switchButton;
        private JComboBox<String> examplesBox;

        public GUIClassAnalyzer() {
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(800, 600);
            setLocationRelativeTo(null);

            // Створення компонентів
            JLabel label = new JLabel("Введіть повне ім'я класу:");
            inputField = new JTextField(30);
            analyzeButton = new JButton("Аналізувати");
            clearButton = new JButton("Очистити");
            switchButton = new JButton("Консольний режим");

            // Приклади для швидкого вибору
            String[] examples = {
                    "Оберіть приклад...",
                    "java.lang.String",
                    "java.util.ArrayList",
                    "java.lang.Thread",
                    "int",
                    "int[]",
                    "java.lang.Runnable"
            };
            examplesBox = new JComboBox<>(examples);
            resultArea = new JTextArea();
            resultArea.setEditable(false);
            resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

            // Панель для введення
            JPanel inputPanel = new JPanel(new FlowLayout());
            inputPanel.add(label);
            inputPanel.add(inputField);
            inputPanel.add(analyzeButton);
            inputPanel.add(clearButton);
            inputPanel.add(switchButton);

            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.add(inputPanel, BorderLayout.CENTER);
            topPanel.add(examplesBox, BorderLayout.SOUTH);

            // Скрол для текстової області
            JScrollPane scrollPane = new JScrollPane(resultArea);

            // Додавання компонентів
            setLayout(new BorderLayout());
            add(topPanel, BorderLayout.NORTH);
            add(scrollPane, BorderLayout.CENTER);

            // Обробники подій
            analyzeButton.addActionListener(e -> analyzeClass());
            clearButton.addActionListener(e -> {
                inputField.setText("");
                resultArea.setText("");
            });

            switchButton.addActionListener(e -> {
                dispose();
                showModeSelection();
            });

            inputField.addActionListener(e -> analyzeClass());

            examplesBox.addActionListener(e -> {
                if (examplesBox.getSelectedIndex() > 0) {
                    inputField.setText((String) examplesBox.getSelectedItem());
                    analyzeClass();
                }
            });
        }

        private void analyzeClass() {
            String className = inputField.getText().trim();
            if (!className.isEmpty()) {
                resultArea.setText(Description(className));
            } else {
                JOptionPane.showMessageDialog(this,
                        "Будь ласка, введіть ім'я класу",
                        "Помилка",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Меню
    private static void showModeSelection() {
        String[] options = {"Графічний режим", "Консольний режим", "Вийти"};

        int choice = JOptionPane.showOptionDialog(
                null,
                "Виберіть режим роботи програми:",
                "Аналізатор класів Java",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        switch (choice) {
            case 0:
                // Графічний режим
                SwingUtilities.invokeLater(() -> {
                    new GUIClassAnalyzer().setVisible(true);
                });
                break;
            case 1:
                // Консольний режим
                runConsoleMode();
                break;
            default:
                // Вихід
                System.out.println("Програма завершена.");
                System.exit(0);
        }
    }

    // Тестовий клас
    public static class TestClass extends Thread implements Runnable {
        private int id;
        public String name;
        protected static final double PI = 3.14159;

        public TestClass() {}

        public TestClass(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public void run() {
        }

        private int calculate(int x, int y) {
            return x + y;
        }

        protected void displayInfo() {
            System.out.println("ID: " + id + ", Name: " + name);
        }

        @Override
        public String toString() {
            return "TestClass{id=" + id + ", name='" + name + "'}";
        }
    }

    // Головний метод
    public static void main(String[] args) {
        // Перевірка аргументів командного рядка
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("-console") || args[0].equalsIgnoreCase("-c")) {
                runConsoleMode();
                return;
            } else if (args[0].equalsIgnoreCase("-gui") || args[0].equalsIgnoreCase("-g")) {
                SwingUtilities.invokeLater(() -> {
                    new GUIClassAnalyzer().setVisible(true);
                });
                return;
            }
        }
        // Якщо немає аргументів - показує вікно вибору
        showModeSelection();

    }
}