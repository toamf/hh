package commands;

import exceptions.*;
import managers.CollectionManager;
import models.*;
import diagram.*;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Команда 'remove_all_by_age'
 * Удаляет из коллекции все элементы, значение поля ages которого эквивалентно заданному
 */
public class RemoveByAge extends Command implements CollectionEditor {
    private CollectionManager collectionManager;

    public RemoveByAge(CollectionManager collectionManager) {
        super("remove_all_by_expelled_students", " expelledStudents : удалить из коллекции все элементы, значение поля expelledStudents которого эквивалентно заданному");
        this.collectionManager = collectionManager;
    }

    /**
     * Исполнить программу
     * @param request аргументы команды
     * @throws IllegalArgument неверные аргументы
     */
    @Override
    public Response execute(Request request) throws IllegalArgument {
        if (request.getArgs().isBlank()) throw new IllegalArgument();
        try {
            Long age = Long.parseLong(request.getArgs().trim());
            Collection<Dragon> toRemove = collectionManager.getCollection().stream()
                    .filter(Objects::nonNull)
                    .filter(dragon -> dragon.getAge() == age)
                    .collect(Collectors.toList());
            for (Dragon element:toRemove) {
                collectionManager.removeElement(element);
            }
            return new Response(Status.OK, "Удалены элементы с таким expelled_students");
        } catch (NumberFormatException exception) {
            return new Response(Status.ERROR, "expelled_students должно быть числом типа int");
        }
    }
}