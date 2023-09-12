package forms;

import console.BlankConsole;
import console.Printable;
import utility.ScannerManager;
import models.*;

import java.util.Scanner;

public class ColorForm extends Form<Color> {
    private final Printable console;
    private final Scanner scanner = ScannerManager.getScanner();

    public ColorForm(Printable console) {
        this.console = (ScannerManager.fileMode())
                ? new BlankConsole()
                : console;
    }

    /**
     * Создает объект {@link Color}
     * @return объект {@link Color}
     */
    @Override
    public Color build() {
        console.println("Доступные цвета:");
        console.println(Color.names());
        while (true) {
            console.println("Введите цвет:");
            String input = scanner.nextLine().trim();
            try{
                return Color.valueOf(input);
            } catch (IllegalArgumentException exception){
                console.printError("Такого цвета нет в списке");
                if (ScannerManager.fileMode()) return null;
            } catch (Throwable throwable) {
                console.printError("Непредвиденная ошибка!");
            }
        }
    }
}