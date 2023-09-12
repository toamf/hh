package console;

/**
 * Класс для вывода в стандартную консоль
 */
public class Console implements Printable {

    @Override
    public void println(String s) {
        System.out.println(s);
    }

    @Override
    public void print(String s) {
        System.out.print(s);
    }

    @Override
    public void printError(String s) {
        System.out.println(s);
    }
}