package console;

import exceptions.IllegalArgument;

public interface Executable {
    void execute(String args) throws IllegalArgument;
}