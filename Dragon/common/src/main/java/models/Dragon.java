package models;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.TreeSet;

public class Dragon implements Validatable, Comparable<Dragon>, Serializable {
    private int id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private static int nextId;
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private ZonedDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Long age; //Значение поля должно быть больше 0, Поле не может быть null
    private Color color; //Значение поля должно быть больше 0
    private DragonType type; //Значение поля должно быть больше 0
    private DragonCharacter character; //Поле не может быть null
    private DragonCave cave; //Поле может быть null

    public Dragon(String name, Coordinates coordinates, ZonedDateTime creationDate, Long age, Color color, DragonType type, DragonCharacter character, DragonCave cave) {
        this.id = incNextId();
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.age = age;
        this.color = color;
        this.type = type;
        this.character = character;
        this.cave = cave;
    }

    /**
     * Обновляет id для следующего элемента
     * @param collection Коллекция
     */
    public static void updateId(TreeSet<Dragon> collection){
        int maxId = 0;
        for (Dragon dragon: collection){
            if (Objects.isNull(dragon)) continue;
            maxId = Math.max(maxId, dragon.getId());
        }
        nextId = maxId + 1;
    }

    /**
     * Увеличивает id
     * @return id
     */
    public static int incNextId(){
        return nextId++;
    }
    public int getNextId() {
        return nextId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Long getAge() {
        return age;
    }

    public void setAge(Long age) {
        this.age = age;
    }


    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public DragonType getType() {
        return type;
    }

    public void setType(DragonType type) {
        this.type = type;
    }

    public DragonCharacter getCharacter() {
        return character;
    }

    public void setCharacter(DragonCharacter character) {
        this.character = character;
    }

    public DragonCave getCave() {
        return cave;
    }

    public void setCave(DragonCave cave) {
        this.cave = cave;
    }
    /**
     * Сравнивает элементы коллекции
     * @param o the object to be compared.
     * @return 0 if equal, negative if less than object, positive if greater than object
     */
    @Override
    public int compareTo(Dragon o) {
        if (Objects.isNull(o)) return 1;
        return Float.compare(this.getId(), o.getId());
    }

    public int compareEl(Dragon o) {
        if (Objects.isNull(o)) return 1;
        return Float.compare(this.getName().length(), o.getName().length());
    }

    /**
     * Проверяет корректность полей
     * @return Корректность полей
     */
    @Override
    public boolean validate() {
        if (this.id < 0) return false;
        if (this.name == null || this.name.isEmpty()) return false;
        if (this.coordinates == null) return false;
        if (this.creationDate == null) return false;
        if (this.age == null || this.age <= 0) return false;
        if (this.color == null) return false;
        if (this.type == null) return false;
        return (this.character != null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Dragon that = (Dragon) o;

        if (id != that.id) return false;
        if (!name.equals(that.name)) return false;
        if (!coordinates.equals(that.coordinates)) return false;
        if (!creationDate.equals(that.creationDate)) return false;
        if (!age.equals(that.age)) return false;
        if (color != that.color) return false;
        if (character != that.character) return false;
        if (type != that.type) return false;
        return Objects.equals(cave, that.cave);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + coordinates.hashCode();
        result = 31 * result + creationDate.hashCode();
        result = 31 * result + age.hashCode();
        result = 31 * result + color.hashCode();
        result = 31 * result + character.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + (cave != null ? cave.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "models.Dragon{" + '\n' +
                "id = " + id + '\n' +
                "name = " + name + '\n' +
                "coordinates = " + coordinates + '\n' +
                "creationDate = " + creationDate + '\n' +
                "age = " + age + '\n' + '\n' +
                "color = " + color + '\n' +
                "type = " + type + '\n' +
                "character = " + character + '\n' +
                "cave = " + cave + '\n' +
                '}';
    }
}