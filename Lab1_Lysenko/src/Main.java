import java.util.Scanner;

public class Main {
    private double pi = 0;
    private long terms = 0;
    private boolean running = true, paused = false;
    private Thread calcThread;
    private final Object lock = new Object();

    public static void main(String[] args) {
        new Main().start();
    }

    private void start() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n1. Продовжити обчислення ");
            System.out.println("2. Зупинити обчислення");
            System.out.println("3. Результат");
            System.out.println("4. Дізнатися сумарний час");
            System.out.println("5. Вихід");
            System.out.println("Ваш вибір:");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    resume();
                    break;
                case 2:
                    pause();
                    break;
                case 3:
                    showResult();
                    break;
                case 4:
                    showTime();
                    break;
                case 5:
                    stop();
                    scanner.close();
                    return;
            }
        }
    }

    private void resume() {
        synchronized (lock) {
            if (calcThread == null || !calcThread.isAlive()) {
                startCalculation();
            } else if (paused) {
                paused = false;
                lock.notify();
                System.out.println("Продовжено.");
            }
        }
    }

    private void pause() {
        synchronized (lock) {
            if (!paused) {
                paused = true;
                System.out.println("Призупинено.");
            }
        }
    }

    private void showResult() {
        System.out.printf("PI ≈ %.10f (похибка: %.10f)%n", pi, Math.abs(pi - Math.PI));
    }

    private void showTime() {
        System.out.println("Час: " + (terms * 10) + " мс.");
    }

    private void stop() {
        running = false;
        synchronized (lock) {
            paused = false;
            lock.notify();
        }
    }

    private void startCalculation() {
        running = true;
        paused = false;
        pi = 0;
        terms = 0;

        calcThread = new Thread(() -> {
            double sum = 0;
            int sign = 1;
            long denominator = 1;

            while (running) {
                synchronized (lock) {
                    while (paused && running) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                }
                if (!running) break;

                sum += sign * 4.0 / denominator;
                pi = sum;
                terms++;
                denominator += 2;
                sign = -sign;

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });

        calcThread.start();
        System.out.println("Початок обчислення");
    }
}