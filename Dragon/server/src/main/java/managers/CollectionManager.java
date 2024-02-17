package managers;

import exceptions.InvalidForm;
import models.Dragon;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

/**
 * Класс для работы с коллекцией
 */
public class CollectionManager {
    private TreeSet<Dragon> collection = new TreeSet<Dragon>( new Comparator<Dragon>() {
        @Override
        public int compare(Dragon s1, Dragon s2) {
            return s1.compareTo(s2);
        }
    });
    private final FileManager fileManager;
    /**
     * Дата создания коллекции
     */
    private LocalDateTime lastInitTime;
    static final Logger collectionManagerLogger = LogManager.getLogger(CollectionManager.class);

    public CollectionManager(FileManager fileManager) {
        this.fileManager = fileManager;
        this.lastInitTime = LocalDateTime.now();

        loadCollection();
    }

    /**
     * @return Коллекция
     */
    public TreeSet<Dragon> getCollection() {
        return collection;
    }

    /**
     * @return Время последней инициализации
     */
    public String getLastInitTime() {
        return lastInitTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }


    /**
     * @return Тип коллекции
     */
    public String collectionType() {
        return collection.getClass().getName();
    }

    /**
     * @return Размер коллекции
     */
    public int collectionSize() {
        return collection.size();
    }

    /**
     * Очищает коллекцию, обновляет время инициализации
     */
    public void clear(){
        this.collection.clear();
        lastInitTime = LocalDateTime.now();
        collectionManagerLogger.info("Коллекция очищена");
    }

    /**
     * Добавляет элемент в коллекцию
     * @param dragon Элемент
     */
    public void addElement(Dragon dragon) {
        collection.add(dragon);
        collectionManagerLogger.info("Добавлен объект в коллекцию", dragon);
    }

    public void addElements(Collection<Dragon> collection) throws InvalidForm{
        if (collection == null) return;
        for (Dragon dragon:collection){
            this.addElement(dragon);
        }
    }

    /**
     * @param id id
     * @return Существование элемента с заданным id
     */
    public boolean checkExist(int id) {
        return collection.stream()
                .anyMatch((x) -> x.getId() == id);
    }

    /**
     * @param id id
     * @return Элемент с таким id или null
     */
    public Dragon getById(int id) {
        for (Dragon element : collection) {
            if (element.getId() == id) return element;
        }
        return null;
    }

    /**
     * Удаляет элемент из коллекции
     * @param dragon Элемент
     */
    public void removeElement(Dragon dragon){
        collection.remove(dragon);
    }

    /**
     * Изменяиет элемент коллекции по id
     * @param id id
     * @param newElement Новый элемент
     * @throws InvalidForm Элемент отсутствует
     */
    public void editById(int id, Dragon newElement) {//throws InvalidForm{
        Dragon pastElement = this.getById(id);
        this.removeElement(pastElement);
        newElement.setId(id);
        this.addElement(newElement);
        Dragon.updateId(this.getCollection());
        collectionManagerLogger.info("Объект с айди " + id + " изменен", newElement);
    }

    /**
     * Загружает коллекцию из файла
     */
    private void loadCollection() {
        collection = (TreeSet<Dragon>) fileManager.readCollection();
        lastInitTime = LocalDateTime.now();
    }
}