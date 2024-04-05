package forms;

import console.BlankConsole;
import console.Printable;
import utility.ScannerManager;
import models.*;


import java.time.ZonedDateTime;
import java.util.Scanner;

public class DragonForm extends Form<Dragon> {
    private final Printable console;
    private final Scanner scanner = ScannerManager.getScanner();

    public DragonForm(Printable console) {
        this.console = (ScannerManager.fileMode())
                ? new BlankConsole()
                : console;
    }

    /**
     * Запрашивает имя дракона
     * @return Имя дракона
     */
    private String askName() {
        String name;
        while (true){
            console.println("Введите имя:");
            name = scanner.nextLine().trim();
            if (name.isBlank()){
                console.printError("Имя не может быть пустым");
                if (ScannerManager.fileMode()) return "";
            }
            else{
                return name;
            }
        }
    }

    /**
     * Запрашивает координаты
     * @return объект {@link Coordinates}
     */
    private Coordinates askCoordinates() {
        return new CoordinatesForm(console).build();
    }

    /**
     * Запрашивает возраст
     * @return Возраст
     */
    private Long askAge() {
        while (true) {
            console.println("Введите возраст дракона:");
            String input = scanner.nextLine().trim();
            try {
                if (input == null || Long.parseLong(input) <= 0) {
                    console.printError("Возраст дракона должен быть положительным");
                    if (ScannerManager.fileMode()) return (long) -1;
                } else {
                    return Long.parseLong(input);
                }
            } catch (NumberFormatException exception) {
                console.printError("Возраст дракона должен быть числом типа long");
            } catch (Throwable throwable) {
                console.printError("Непредвиденная ошибка!");
            }
        }
    }




    private Color askColor(){
        return new ColorForm(console).build();
    }


    private DragonCave askCave(){
        return new DragonCaveForm(console).build();
    }

    private DragonCharacter askCharacter() {
        return new DragonCharacterForm(console).build();
    }

    private DragonType askType() {
        return new DragonTypeForm(console).build();
    }
    /**
     * Создает объект {@link Dragon}
     * @return объект {@link Dragon}
     */
    @Override
    public Dragon build() {
        return new Dragon(askName(),
                askCoordinates(),
                ZonedDateTime.now(),
                askAge(),
                askColor(),
                askType(),
                askCharacter(),
                askCave());
    }

}