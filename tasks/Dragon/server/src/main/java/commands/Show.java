package commands;

import exceptions.*;
import managers.CollectionManager;
import models.*;
import diagram.*;

import java.util.Collection;

/**
 * Команда 'show'
 * Выводит в стандартный поток вывода все элементы коллекции в строковом представлении
 */
public class Show extends Command {
    private CollectionManager collectionManager;

    public Show(CollectionManager collectionManager) {
        super("show", ": вывести в стандартный поток вывода все элементы коллекции в строковом представлении");
        this.collectionManager = collectionManager;
    }

    /**
     * Исполнить программу
     * @param request аргументы команды
     * @throws IllegalArgument неверные аргументы
     */
    @Override
    public Response execute(Request request) throws IllegalArgument {
        if (!request.getArgs().isBlank()) throw new IllegalArgument();
        Collection<Dragon> collection = collectionManager.getCollection();
        if (collection == null || collection.isEmpty()) {
            return new Response(Status.ERROR, "Коллекция не инициализирована");
        }
        return new Response(Status.OK, "Коллекция: ", collection);
    }
}