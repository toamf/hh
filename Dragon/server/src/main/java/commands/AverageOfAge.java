package commands;

import exceptions.*;
import managers.CollectionManager;
import models.*;
import diagram.*;

import java.util.Objects;

/**
 * Команда 'average_of_expelled_students'
 * Выводит среднее значение поля age для всех элементов коллекции
 */
public class AverageOfAge extends Command {
    private CollectionManager collectionManager;

    public AverageOfAge(CollectionManager collectionManager) {
        super("average_of_age", ": вывести среднее значение поля age для всех элементов коллекции");
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
        Double toSort = collectionManager.getCollection().stream()
                .filter(Objects::nonNull)
                .mapToLong(Dragon::getAge)
                .average().orElse(0);
        return new Response(Status.OK, "Среднее значение поля expelledStudents: " + toSort.toString());

    }
}