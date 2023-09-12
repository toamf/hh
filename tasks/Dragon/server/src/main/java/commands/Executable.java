package commands;

import exceptions.IllegalArgument;
import diagram.*;

/**
 * Интерфейс для выполняемых команд
 */
public interface Executable {
    Response execute(Request request) throws IllegalArgument;
}