package managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import models.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * Класс для работы с файлами
 */
public class FileManager {
    private final String fileName;
    private final utility.Console console;
    //private final RuntimeManager runtimeManager;
    static final Logger fileManagerLogger = LogManager.getLogger(FileManager.class);

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeAdapter())
            .create();

    private static List<File> scriptStack = new LinkedList<>();

    public FileManager(utility.Console console, String fileName) {//, RuntimeManager runtimeManager) {
        //this.runtimeManager = runtimeManager;
        this.fileName = fileName;
        this.console = console;
    }

    /**
     * Сохраняет коллекцию в файл
     * @param collection Коллекция
     */
    public void writeCollection(Collection<Dragon> collection) {
        try(PrintWriter printWriter = new PrintWriter(new File(fileName)))
        {
            printWriter.println(gson.toJson(collection));
            fileManagerLogger.info("Коллекция сохранена в файл " + fileName);
        }
        catch(IOException e){
            fileManagerLogger.info("Коллекция не сохранена");
        }
    }

    /**
     * Читает коллекцию из файла
     * @return Коллекция
     */
    public Collection<Dragon> readCollection() {
        if (fileName != null && !fileName.isEmpty()) {
            try (var fileReader = new InputStreamReader(new FileInputStream(fileName))) {
                Type collectionType = new TypeToken<TreeSet<Dragon>>(){}.getType();
                var reader = new BufferedReader(fileReader);
                StringBuilder jsonString = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (!line.equals("")) {
                        jsonString.append(line);
                    }
                }

                if (jsonString.length() == 0) {
                    jsonString = new StringBuilder("[]");
                }

                TreeSet<Dragon> tmpCollection = gson.fromJson(jsonString.toString(), collectionType);
                TreeSet<Dragon> collection = new TreeSet<Dragon>();
                List<Integer> tmpIdOfEach = new ArrayList<>();
                List<Integer> idOfEach = new ArrayList<>();
                tmpCollection.forEach(p -> {
                    if (!p.validate()) {
                        fileManagerLogger.info("Элемент с id = " + p.getId() + " имеет невалидные поля");
                    } else {
                        if (tmpIdOfEach.isEmpty()) {
                            idOfEach.add(p.getId());
                            collection.add(p);
                        } else {
                            for (int el : tmpIdOfEach) {
                                if (p.getId() == el) {
                                    fileManagerLogger.info("Элемент с id = " + p.getId() + " уже находится в коллекции");
                                } else {
                                    idOfEach.add(p.getId());
                                    collection.add(p);
                                }
                            }
                        }
                    }
                    for (int el : idOfEach) {
                        boolean test = false;
                        for (int tmpEl : tmpIdOfEach) {
                            if (el == tmpEl) {
                                test = true;
                            }
                        }
                        if (!test) {
                            tmpIdOfEach.add(el);
                        }
                    }
                });
                fileManagerLogger.info("Коллекция загружена из файла");
                Dragon.updateId(collection);
                return collection;
            } catch (FileNotFoundException exception) {
                fileManagerLogger.info("Загрузочный файл не найден");
            } catch (NoSuchElementException exception) {
                fileManagerLogger.info("Загрузочный файл пуст");
            }catch (IOException ex) {
                fileManagerLogger.info("Непредвиденная ошибка!");
            }
        } else {
            fileManagerLogger.info("Файл не обнаружен");
        }
        return new TreeSet<>();
    }

    /**
     * Режим исполнения скрипта
     * @param fileName Имя скрипта
     */
}