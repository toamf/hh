package commands;

import exceptions.IllegalArgument;
import managers.CollectionManager;
import models.*;
import diagram.*;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Команда 'print_ascending'
 * Выводит элементы коллекции в порядке возрастания
 */
public class PrintAscending extends Command {
    private CollectionManager collectionManager;

    public PrintAscending(CollectionManager collectionManager) {
        super("print_ascending", ": вывести элементы коллекции в порядке возрастания");
        this.collectionManager = collectionManager;
    }

    /**
     * Исполнить программу
     * @param request аргументы команды
     * @throws IllegalArgument неверные аргументы
     */
    @Override
    public Response execute(Request request) {
        Collection<Dragon> collection = collectionManager.getCollection().stream()
                .sorted(Dragon::compareEl)
                //.forEach(p -> p.toString())
                .collect(Collectors.toList());
        return new Response(Status.OK, "Коллекция в порядке возрастания: ", collection);
    }
}