import console.Console;
import utility.Client;
import utility.RuntimeManager;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Scanner;

public class App {
    private static final int port = 6094;
    private static Console console;
    public static void main(String[] args) {
        console = new Console();
        try {
            Client client = new Client(InetAddress.getLocalHost(), port, console);
            new RuntimeManager(console, client, new Scanner(System.in)).interactiveMode();
        } catch (IOException e) {
            console.println("Невозможно подключиться к серверу!");
        }
    }

}