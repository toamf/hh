package commands;

import exceptions.*;
import managers.CollectionManager;
import diagram.*;

/**
 * Команда 'info'
 * Выводит в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)
 */
public class Info extends Command {
    private CollectionManager collectionManager;
    public Info(CollectionManager collectionManager) {
        super("info", ": вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)");
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
        String lastInitTime = (collectionManager.getLastInitTime() == null)
                ? "Коллекция не инициализирована"
                : collectionManager.getLastInitTime().toString();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Информация о коллекции: \n");
        stringBuilder.append("Тип: " + collectionManager.collectionType() + "\n");
        stringBuilder.append("Количество элементов: " + collectionManager.collectionSize() + "\n");
        stringBuilder.append("Дата инициализации: " + lastInitTime + "\n");
        return new Response(Status.OK, stringBuilder.toString());
    }
}