package commands;

import exceptions.*;
import managers.CollectionManager;
import models.*;
import diagram.*;

import java.util.Objects;

/**
 * Команда 'add'
 * Добавляет новый элемент в коллекцию
 */
public class Add extends Command implements CollectionEditor {
    private CollectionManager collectionManager;

    public Add(CollectionManager collectionManager) {
        super("add", " {element} : добавить новый элемент в коллекцию");
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
        } else{
            request.getObject().setId(Dragon.incNextId());
            collectionManager.addElement(request.getObject());
            return new Response(Status.OK, "Объект успешно добавлен");
        }
    }
}