package models;

import java.io.Serializable;
import java.util.Objects;

public class DragonCave implements Validatable, Serializable {
    private Double depth; //Поле не может быть null

    public DragonCave( Double depth) {
        this.depth = depth;
    }


    public Double getDepth() {
        return depth;
    }

    public void setDepth(Double depth) {
        this.depth = depth;
    }


    /**
     * Проверяет корректность полей
     * @return Корректность полей
     */
    @Override
    public boolean validate() {
        return !(this.depth == null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DragonCave dragonCave = (DragonCave) o;
        return depth.equals(dragonCave.depth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(depth);
    }

    @Override
    public String toString() {
        return "models.DragonCave{" + '\n' +
                "depth = " + depth + '\n' +
                '}';
    }
}
