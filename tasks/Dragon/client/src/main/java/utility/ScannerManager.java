package utility;

import java.util.Scanner;

/**
 * Класс для работы с режимами ввода
 */
public class ScannerManager {
    private static Scanner userScanner = new Scanner(System.in);
    private static boolean fileMode = false;

    /**
     * @return Сканер
     */
    public static Scanner getScanner() {
        return userScanner;
    }

    /**
     * Устанавливает сканер
     * @param userScanner Сканер
     */
    public static void setScanner(Scanner userScanner) {
        ScannerManager.userScanner = userScanner;
    }

    /**
     * @return Работа с файлом
     */
    public static boolean fileMode() {
        return fileMode;
    }

    /**
     * Устанавливает работу с консолью
     */
    public static void setUserMode() {
        ScannerManager.fileMode = false;
    }

    /**
     * Устанавливает работу с файлом
     */
    public static void setFileMode() {
        ScannerManager.fileMode = true;
    }
}