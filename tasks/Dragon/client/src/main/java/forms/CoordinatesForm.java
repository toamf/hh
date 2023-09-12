package forms;

import console.BlankConsole;
import console.Printable;
import utility.ScannerManager;
import models.Coordinates;

import java.util.Scanner;

/**
 * Форма для координат
 */
public class CoordinatesForm extends Form<Coordinates> {
    private final Printable console;
    private final Scanner scanner = ScannerManager.getScanner();

    public CoordinatesForm(Printable console) {
        this.console = (ScannerManager.fileMode())
                ? new BlankConsole()
                : console;
    }

    /**
     * Запрашивает координату X
     * @return Координата X
     */
    private Long askX() {
        while (true) {
            console.println("Введите координату X:");
            String input = scanner.nextLine().trim();
            try {
                if (Long.parseLong(input) <= -487) {
                    console.printError("Координата X должна быть больше -487");
                    if (ScannerManager.fileMode()) return (long) -500;
                } else {
                    return Long.parseLong(input);
                }
            } catch (NumberFormatException exception) {
                console.printError("X должно быть числом типа long");
            } catch (Throwable throwable) {
                console.printError("Непредвиденная ошибка!");
            }
        }
    }

    /**
     * Запрашивает координату Y
     * @return Координата Y
     */
    private int askY() {
        while (true) {
            console.println("Введите координату Y:");
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException exception) {
                console.printError("Y должно быть числом типа int");
            } catch (Throwable throwable) {
                console.printError("Непредвиденная ошибка!");
            }
        }
    }

    /**
     * Создает объект {@link Coordinates}
     * @return объект {@link Coordinates}
     */
    @Override
    public Coordinates build() {
        return new Coordinates(askX(), askY());
    }
}