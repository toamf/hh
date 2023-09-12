package forms;

import console.BlankConsole;
import console.Printable;
import utility.ScannerManager;
import models.*;

import java.util.Scanner;

public class DragonCharacterForm extends Form<DragonCharacter> {
    private final Printable console;
    private final Scanner scanner = ScannerManager.getScanner();

    public DragonCharacterForm(Printable console) {
        this.console = (ScannerManager.fileMode())
                ? new BlankConsole()
                : console;
    }

    /**
     * Создает объект {@link DragonCharacter}
     * @return объект {@link DragonCharacter}
     */
    @Override
    public DragonCharacter build() {
        console.println("Доступные характеры:");
        console.println(DragonCharacter.names());
        while (true) {
            console.println("Введите характер:");
            String input = scanner.nextLine().trim();
            try{
                return DragonCharacter.valueOf(input);
            } catch (IllegalArgumentException exception){
                console.printError("Такого характера нет в списке");
                if (ScannerManager.fileMode()) return null;
            } catch (Throwable throwable) {
                console.printError("Непредвиденная ошибка!");
            }
        }
    }
}