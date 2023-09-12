package forms;

import console.BlankConsole;
import console.Printable;
import utility.ScannerManager;
import models.*;

import java.util.Scanner;


public class DragonTypeForm extends Form<DragonType> {
    private final Printable console;
    private final Scanner scanner = ScannerManager.getScanner();

    public DragonTypeForm(Printable console) {
        this.console = (ScannerManager.fileMode())
                ? new BlankConsole()
                : console;
    }

    /**
     * Создает объект {@link DragonCharacter}
     * @return объект {@link DragonCharacter}
     */
    @Override
    public DragonType build() {
        console.println("Доступные характеры:");
        console.println(DragonType.names());
        while (true) {
            console.println("Введите характер:");
            String input = scanner.nextLine().trim();
            try{
                return DragonType.valueOf(input);
            } catch (IllegalArgumentException exception){
                console.printError("Такого характера нет в списке");
                if (ScannerManager.fileMode()) return null;
            } catch (Throwable throwable) {
                console.printError("Непредвиденная ошибка!");
            }
        }
    }
}