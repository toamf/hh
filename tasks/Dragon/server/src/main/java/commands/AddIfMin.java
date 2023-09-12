package commands;

import exceptions.*;
import managers.CollectionManager;
import models.*;
import diagram.*;

import java.util.Objects;

/**
 * Команда 'add_if_min'
 * Добавляет элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции
 */
public class AddIfMin extends Command implements CollectionEditor {
    private CollectionManager collectionManager;

    public AddIfMin(CollectionManager collectionManager) {
        super("add_if_min", " {element} : добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции");
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
        if (Objects.isNull(request.getObject())){
            return new Response(Status.ASK_OBJECT, "Для команды " + this.getName() + " требуется объект");
        }
        Dragon newElement = request.getObject();
        if (newElement.compareEl(collectionManager.getCollection().stream()
                .filter(Objects::nonNull)
                .min(Dragon::compareEl)
                .orElse(null)) >= 1)
        {
            collectionManager.addElement(newElement);
            return new Response(Status.OK, "Объект успешно добавлен");
        } else {
            return new Response(Status.ERROR,"Элемент больше максимального");
        }
    }
}