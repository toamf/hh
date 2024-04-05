package utility;

import console.Console;
import console.Printable;
import exceptions.IllegalArgument;
import exceptions.InvalidForm;
import exceptions.NoSuchCommand;
import exceptions.*;
import forms.*;
import models.*;
import diagram.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.PortUnreachableException;
import java.util.*;

/**
 * Класс для работы с пользовательским вводом
 */
public class RuntimeManager {
    private final Scanner userScanner;
    private final Printable console;
    private final Client client;

    private static List<File> scriptStack = new LinkedList<>();

    public RuntimeManager(Console console, Client client, Scanner scanner) {
        this.console = console;
        this.client = client;
        this.userScanner = scanner;
    }

    /**
     * Работа с пользователем и выполнение команд
     */
    public void interactiveMode() {
        Scanner userScanner = ScannerManager.getScanner();
        while (true) {
            try {
                if (!userScanner.hasNext()) {
                    console.println("До свидания");
                    System.exit(0);
                }
                String[] userCommand = (userScanner.nextLine().trim() + " ").split(" ", 2);

                Response response = client.sendAndAskResponse(new Request(userCommand[0].trim(), userCommand[1].trim()));
                this.printResponse(response);
                switch (response.getStatus()){
                    case ASK_OBJECT -> {
                        Dragon dragon = new DragonForm(console).build();
                        if(!dragon.validate()) throw new InvalidForm();
                        Response newResponse = client.sendAndAskResponse(
                                new Request(
                                        userCommand[0].trim(),
                                        userCommand[1].trim(),
                                        dragon));
                        if (newResponse.getStatus() != Status.OK){
                            console.printError(newResponse.getResponse());
                        }
                        else {
                            this.printResponse(newResponse);
                        }
                    }
                    case EXIT -> System.exit(0);
                    case EXECUTE_SCRIPT -> {
                        ScannerManager.setFileMode();
                        this.scriptMode(response.getResponse());
                        ScannerManager.setUserMode();
                    }
                    default -> {}
                }
            } catch (NoSuchElementException exception) {
                console.printError("Пользовательский ввод не обнаружен");
            } catch (NoSuchCommand e) {
                console.printError("Такой команды нет в списке");
            } catch (IllegalArgument e) {
                console.printError("Введены неправильные аргументы команды");
            } catch (InvalidForm e) {
                console.printError("Поля не валидны! Объект не создан");
            } catch (PortUnreachableException e) {
                console.printError("Ошибка подключения, сервер недоступен");
            }catch (IOException e) {
                console.printError("Неизвестная ошибкаа " + e);
            }
        }
    }

    public void scriptMode(String fileName) {
        scriptStack.add(new File(fileName));
        try (Scanner scriptScanner = new Scanner(new File(fileName))) {
            if (!scriptScanner.hasNext()) throw new NoSuchElementException();
            Scanner tmpScanner = ScannerManager.getScanner();
            ScannerManager.setScanner(scriptScanner);
            ScannerManager.setFileMode();
            do {
                String[] userCommand = (scriptScanner.nextLine().trim() + " ").split(" ", 2);
                while (scriptScanner.hasNextLine() && userCommand[0].isEmpty()) {
                    userCommand = (scriptScanner.nextLine().trim() + " ").split(" ", 2);
                }
                if (userCommand[0].equals("execute_script")) {
                    for (File script : scriptStack) {
                        if (new File(userCommand[1].trim()).equals(script)) {
                            throw new Recursion();
                        }
                    }
                }
                console.println("$ " + String.join(" ", userCommand));
                Response response = client.sendAndAskResponse(new Request(userCommand[0].trim(), userCommand[1].trim()));
                this.printResponse(response);
                switch (response.getStatus()){
                    case ASK_OBJECT -> {
                        Dragon dragon;
                        try{
                            dragon = new DragonForm(console).build();
                            if (!dragon.validate()) throw new InvalidForm();
                        } catch (InvalidForm err){
                            console.printError("Поля в файле не валидны! Объект не создан");
                            continue;
                        }
                        Response newResponse = client.sendAndAskResponse(new Request(userCommand[0].trim(), userCommand[1].trim(), dragon));
                        if (newResponse.getStatus() != Status.OK){
                            console.printError(newResponse.getResponse());
                        }
                        else {
                            this.printResponse(newResponse);
                        }
                    }
                    case EXIT -> System.exit(0);
                    case EXECUTE_SCRIPT -> {
                        this.scriptMode(response.getResponse());
                    }
                    default -> {}
                }
            } while (scriptScanner.hasNextLine());

            ScannerManager.setScanner(tmpScanner);
            ScannerManager.setUserMode();

        } catch (FileNotFoundException e) {
            console.printError("Файл не обнаружен");
        } catch (NoSuchElementException e) {
            console.printError("Файл пустой");
        } catch (Recursion e) {
            console.printError("Скрипт не может вызываться рекурсивно");
        } catch (NoSuchCommand e) {
            console.printError("Такой команды нет в списке");
        } catch (IllegalArgument e) {
            console.printError("Введены неправильные аргументы команды");
        } catch (IOException e) {
            console.printError("Неизвестная ошибка");
        }

    }


    public void printResponse(Response response) {
        switch (response.getStatus()){
            case OK -> {
                if ((Objects.isNull(response.getCollection()))) {
                    console.println(response.getResponse());
                } else {
                    console.println(response.getResponse() + "\n" + response.getCollection().toString());
                }
            }
            case ERROR -> console.printError(response.getResponse());
            case WRONG_ARGUMENTS -> console.printError("Неверное использование команды!");
            default -> {}
        }
    }
}