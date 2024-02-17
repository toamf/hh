package console;

/**
 * Класс для вывода в пустую консоль
 */
public class BlankConsole implements Printable{
    @Override
    public void println(String s) {

    }

    @Override
    public void print(String s) {

    }

    @Override
    public void printError(String s) {
        System.out.println(s);
    }
}