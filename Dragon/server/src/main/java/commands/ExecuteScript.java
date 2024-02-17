package commands;

import exceptions.*;
import diagram.*;


/**
 * Команда 'execute_script'
 * Считатывает и исполняет скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.
 */
public class ExecuteScript extends Command {
    //private final FileManager fileManager;
    //private final RuntimeManager runtimeManager;

    public ExecuteScript(){//FileManager fileManager, RuntimeManager runtimeManager) {
        super("execute_script", " file_name : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.");
        //this.fileManager = fileManager;
        //this.runtimeManager = runtimeManager;
    }

    /**
     * Исполнить программу
     * @param request аргументы команды
     * @throws IllegalArgument неверные аргументы
     */

    @Override
    public Response execute(Request request) throws IllegalArgument {
        if (request.getArgs() == null || request.getArgs().isEmpty()) {
            return new Response(Status.ERROR, "Файл не найден");
        }
        String fileName = (request.getArgs().trim());
        return new Response(Status.EXECUTE_SCRIPT, fileName);
    }
}