package commands;

import exceptions.*;
import managers.CollectionManager;
import models.*;
import diagram.*;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Команда 'remove_greater'
 * Удаляет из коллекции все элементы, превышающие заданный
 */
public class RemoveGreater extends Command implements CollectionEditor {
    private CollectionManager collectionManager;

    public RemoveGreater(CollectionManager collectionManager) {
        super("remove_greater", " {element} : удалить из коллекции все элементы, превышающие заданный");
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
        class NoElements extends RuntimeException{
        }
        try {
            if (Objects.isNull(request.getObject())){
                return new Response(Status.ASK_OBJECT, "Для команды " + this.getName() + " требуется объект");
            }
            Dragon newElement = request.getObject();
            Collection<Dragon> toRemove = collectionManager.getCollection().stream()
                    .filter(Objects::nonNull)
                    .filter(dragon -> dragon.compareEl(newElement) >= 1)
                    .collect(Collectors.toList());
            for (Dragon element:toRemove) {
                collectionManager.removeElement(element);
            }
            return new Response(Status.OK, "Удалены элементы большие чем заданный");
        } catch (NoElements e){
            return new Response(Status.ERROR, "В коллекции нет элементов");
        }
    }
}