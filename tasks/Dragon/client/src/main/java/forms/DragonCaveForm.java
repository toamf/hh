package forms;

import console.BlankConsole;
import console.Printable;
import utility.ScannerManager;
import models.*;

import java.util.Scanner;

public class DragonCaveForm extends Form<DragonCave> {
    private final Printable console;
    private final Scanner scanner = ScannerManager.getScanner();

    public DragonCaveForm(Printable console) {
        this.console = (ScannerManager.fileMode())
                ? new BlankConsole()
                : console;
    }


    private double askDepth() {
        while (true) {
            console.println("Введите параметр depth:");
            String input = scanner.nextLine().trim();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException exception) {
                console.printError("Depth должно быть числом типа double");
            } catch (Throwable throwable) {
                console.printError("Непредвиденная ошибка!");
            }
        }
    }


    /**
     * Создает объект {@link DragonCave}
     * @return объект {@link DragonCave}
     */
    @Override
    public DragonCave build() {
        return new DragonCave(askDepth());
    }
}