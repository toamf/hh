package managers;

import commands.*;
import exceptions.*;
import diagram.*;

import java.util.Collection;
import java.util.HashMap;

/**
 * Класс для управления командами
 */
public class CommandManager {
    private final HashMap<String, Command> commands = new HashMap<>();
    private final FileManager fileManager;
    private final CollectionManager collectionManager;

    public CommandManager(FileManager fileManager, CollectionManager collectionManager) {
        this.fileManager = fileManager;
        this.collectionManager = collectionManager;
    }

    /**
     * Добавляет команду
     * @param command Команда
     */
    public void register(Command command){
        this.commands.put(command.getName(), command);
    }

    /**
     * @return Коллекция команд
     */
    public Collection<Command> getCommands(){
        return commands.values();
    }

    /**
     * Исполнение команды
     * @param request Аргументы
     * @throws IllegalArgument неверные аргументы
     * @throws NoSuchCommand нет такой команды
     */
    public Response execute(Request request) throws IllegalArgument, NoSuchCommand, CommandRuntimeError {
        Command command = commands.get(request.getCommandName());
        if (command == null) throw new NoSuchCommand();
        Response response = command.execute(request);
        if (command instanceof CollectionEditor) {
            fileManager.writeCollection(collectionManager.getCollection());
        }

        return response;
    }
}