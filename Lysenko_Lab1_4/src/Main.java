import java.lang.reflect.Array;

public class Main {

    // ===== Створення 1D масиву =====
    public static Object createArray(Class<?> type, int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Розмір масиву не може бути від'ємним: " + size);
        }
        return Array.newInstance(type, size);
    }

    // ===== Створення 2D масиву =====
    public static Object createMatrix(Class<?> type, int rows, int cols) {
        if (rows < 0 || cols < 0) {
            throw new IllegalArgumentException("Розміри матриці не можуть бути від'ємними");
        }
        Object matrix = Array.newInstance(type, rows, cols);

        // Ініціалізуємо кожен рядок окремим масивом
        for (int i = 0; i < rows; i++) {
            Array.set(matrix, i, Array.newInstance(type, cols));
        }

        return matrix;
    }

    // ===== Resize 1D =====
    public static Object resizeArray(Object array, int newSize) {
        if (!array.getClass().isArray()) {
            throw new IllegalArgumentException("Об'єкт не є масивом");
        }
        if (newSize < 0) {
            throw new IllegalArgumentException("Новий розмір не може бути від'ємним: " + newSize);
        }

        int oldSize = Array.getLength(array);
        Class<?> type = array.getClass().getComponentType();

        Object newArray = Array.newInstance(type, newSize);

        // Копіюємо існуючі елементи
        int copyLength = Math.min(oldSize, newSize);
        System.arraycopy(array, 0, newArray, 0, copyLength);

        // Для примітивних типів нові елементи вже мають значення за замовчуванням
        // Для об'єктних типів нові елементи будуть null

        return newArray;
    }

    // ===== Resize 2D =====
    public static Object resizeMatrix(Object matrix, int newRows, int newCols) {
        if (!matrix.getClass().isArray()) {
            throw new IllegalArgumentException("Об'єкт не є масивом");
        }

        Class<?> componentType = matrix.getClass().getComponentType();
        if (!componentType.isArray()) {
            throw new IllegalArgumentException("Об'єкт не є двовимірним масивом");
        }

        Class<?> elementType = componentType.getComponentType();
        int oldRows = Array.getLength(matrix);

        // Створюємо нову матрицю
        Object newMatrix = Array.newInstance(elementType, newRows, newCols);

        // Ініціалізуємо всі рядки
        for (int i = 0; i < newRows; i++) {
            Array.set(newMatrix, i, Array.newInstance(elementType, newCols));
        }

        // Копіюємо існуючі дані
        int rowsToCopy = Math.min(oldRows, newRows);

        for (int i = 0; i < rowsToCopy; i++) {
            Object oldRow = Array.get(matrix, i);
            if (oldRow != null) {
                int oldCols = Array.getLength(oldRow);
                Object newRow = Array.get(newMatrix, i);
                int colsToCopy = Math.min(oldCols, newCols);
                System.arraycopy(oldRow, 0, newRow, 0, colsToCopy);
            }
        }
        return newMatrix;
    }

    public static String arrayToString(Object array) {
        if (array == null) {
            return "null";
        }

        if (!array.getClass().isArray()) {
            return array.toString();
        }

        StringBuilder sb = new StringBuilder();

        // Визначає тип та розмірності
        Class<?> type = array.getClass();
        int dimensions = 0;

        while (type.isArray()) {
            dimensions++;
            type = type.getComponentType();
        }

        // Додає тип
        sb.append(type.getName());

        // Додає розмірності
        Object temp = array;
        for (int d = 0; d < dimensions; d++) {
            int length = Array.getLength(temp);
            sb.append("[").append(length).append("]");
            if (d < dimensions - 1 && Array.getLength(temp) > 0) {
                temp = Array.get(temp, 0);
            }
        }

        sb.append(" = ");
        buildString(array, sb);

        return sb.toString();
    }

    private static void buildString(Object array, StringBuilder sb) {
        if (array == null) {
            sb.append("null");
            return;
        }

        if (!array.getClass().isArray()) {
            sb.append(array);
            return;
        }

        sb.append("{");
        int length = Array.getLength(array);

        for (int i = 0; i < length; i++) {
            if (i > 0) {
                sb.append(", ");
            }

            Object element = Array.get(array, i);

            if (element != null && element.getClass().isArray()) {
                buildString(element, sb);
            } else {
                sb.append(element);
            }
        }

        sb.append("}");
    }
    // Заповнення матриці
    public static void fillMatrix(Object matrix) {
        if (!matrix.getClass().isArray()) return;

        int rows = Array.getLength(matrix);
        for (int i = 0; i < rows; i++) {
            Object row = Array.get(matrix, i);
            if (row != null && row.getClass().isArray()) {
                int cols = Array.getLength(row);
                for (int j = 0; j < cols; j++) {
                    Array.set(row, j, i * 10 + j);
                }
            }
        }
    }

    // Заповнення масиву
    public static void fillArray(Object array, int startValue) {
        if (!array.getClass().isArray()) return;

        int length = Array.getLength(array);
        for (int i = 0; i < length; i++) {
            Array.set(array, i, startValue + i);
        }
    }

    public static void main(String[] args) {
        System.out.println("1D Масиви\n");

        // 1D масиви
        Object a1 = createArray(int.class, 2);
        System.out.println(arrayToString(a1));

        Object a2 = createArray(String.class, 3);
        System.out.println(arrayToString(a2));

        Object a3 = createArray(Double.class, 5);
        System.out.println(arrayToString(a3));

        System.out.println("\n2D Масиви\n");

        // 2D масив
        Object m = createMatrix(int.class, 3, 5);
        fillMatrix(m);
        System.out.println(arrayToString(m));

        // resize збільшення рядків і стовпців
        m = resizeMatrix(m, 4, 6);
        System.out.println(arrayToString(m));

        // resize збільшення стовпців
        m = resizeMatrix(m, 3, 7);
        System.out.println(arrayToString(m));

        // resize зменшення
        m = resizeMatrix(m, 2, 2);
        System.out.println(arrayToString(m));

        System.out.println("\nТестування\n");

        // Різні типи
        Object strMatrix = createMatrix(String.class, 2, 3);
        System.out.println("Матриця String: " + arrayToString(strMatrix));

        // Заповнює матрицю рядками
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                Array.set(Array.get(strMatrix, i), j, "elem_" + i + "_" + j);
            }
        }
        System.out.println("Заповнена матриця String: " + arrayToString(strMatrix));

        // Тест з boolean
        Object boolArray = createArray(boolean.class, 4);
        System.out.println("Boolean масив: " + arrayToString(boolArray));
    }
}