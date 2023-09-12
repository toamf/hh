package commands;

import exceptions.*;
import managers.CollectionManager;
import models.*;
import diagram.*;

import java.util.Objects;

/**
 * Команда 'update'
 * Обновляет значение элемента коллекции, id которого равен заданному
 */
public class Update extends Command implements CollectionEditor {
    private CollectionManager collectionManager;

    public Update(CollectionManager collectionManager) {
        super("update", " id {element} : обновить значение элемента коллекции, id которого равен заданному");
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
        class NoSuchId extends RuntimeException{
        }
        try {
            int id = Integer.parseInt(request.getArgs().trim());
            if (!collectionManager.checkExist(id)) throw new NoSuchId();
            if (Objects.isNull(request.getObject())){
                return new Response(Status.ASK_OBJECT, "Для команды " + this.getName() + " требуется объект");
            }
            Dragon newDragon = request.getObject();
            collectionManager.editById(id, newDragon);
            return new Response(Status.OK, "Объект успешно обновлен");
        } catch (NoSuchId err) {
            return new Response(Status.ERROR, "В коллекции нет элемента с таким id");
        } catch (NumberFormatException exception) {
            return new Response(Status.ERROR,"id должно быть числом типа int");
        }
    }
}