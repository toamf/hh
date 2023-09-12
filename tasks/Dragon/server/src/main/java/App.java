import commands.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utility.Console;
import managers.CollectionManager;
import managers.CommandManager;
import managers.FileManager;
import utility.DatagramServer;
import utility.RequestHandler;
import java.io.IOException;
import java.net.*;

public class App {
    private Console console;
    public static int port = 6094;
    public static final int connection_timeout = 60 * 1000;
    public static Logger logger = LogManager.getLogger(App.class);
    public static void main(String[] args) {
        Console console = new Console();
        if(args.length != 1){
            console.printError("Поместите путь в аргументы командной строки!");
            return;
        }
        String fileName = args[0];

        FileManager fileManager = new FileManager(console, fileName);
        CollectionManager collectionManager = new CollectionManager(fileManager);
        CommandManager commandManager = new CommandManager(fileManager, collectionManager);

        commandManager.register(new Help(commandManager));
        commandManager.register(new Info(collectionManager));
        commandManager.register(new Show(collectionManager));
        commandManager.register(new Add(collectionManager));
        commandManager.register(new Update(collectionManager));
        commandManager.register(new RemoveById(collectionManager));
        commandManager.register(new Clear(collectionManager));
        commandManager.register(new ExecuteScript());//fileManager));
        commandManager.register(new Exit());
        commandManager.register(new AddIfMax(collectionManager));
        commandManager.register(new AddIfMin(collectionManager));
        commandManager.register(new RemoveGreater(collectionManager));
        commandManager.register(new RemoveByAge(collectionManager));
        commandManager.register(new AverageOfAge(collectionManager));
        commandManager.register(new PrintAscending(collectionManager));

        RequestHandler requestHandler = new RequestHandler(commandManager);
        DatagramServer server = null;
        try {
            server = new DatagramServer(InetAddress.getLocalHost(), port, connection_timeout, requestHandler, fileManager, collectionManager);
        } catch (UnknownHostException e) {
            logger.fatal("Неизвестный хост");
        } catch (SocketException e) {
            logger.fatal("Случилась ошибка сокета");
        }
        try {
            server.run();
        } catch (IOException e) {
            logger.fatal("Неизвестная ошибка ");
        }
    }
}