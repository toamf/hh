package models;

import java.io.Serializable;

public enum DragonCharacter implements Serializable {
    EVIL,
    WISE,
    CHAOTIC_EVIL;

    public static String names() {
        StringBuilder nameList = new StringBuilder();
        for (var forms : values()) {
            nameList.append(forms.name());
            nameList.append("\n");
        }
        return nameList.substring(0, nameList.length() - 1);
    }
}
