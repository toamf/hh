package utility;

import exceptions.*;
import managers.CommandManager;
import diagram.*;

public class RequestHandler {
    private CommandManager commandManager;

    public RequestHandler(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    public Response handle(Request request) {
        try {
            return commandManager.execute(request);
        } catch (IllegalArgument e) {
            return new Response(Status.WRONG_ARGUMENTS, "Неверное использование аргументов команды");
        } catch (CommandRuntimeError e) {
            return new Response(Status.ERROR, "Ошибка при исполнении программы");
        } catch (NoSuchCommand e) {
            return new Response(Status.ERROR, "Такой команды нет в списке");
        }
    }
}