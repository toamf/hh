/**
 * Класс, представляющий карту.
 */
class Card {
    private final int rank;  // Ранг карты (6,7,8,...)
    private final int suit;  // Масть карты (пики, трефы, бубны, черви)

    /**
     * Конструктор класса Card.
     * @param rank Ранг карты.
     * @param suit Масть карты.
     */
    public Card(int rank, int suit) {
        this.rank = rank;
        this.suit = suit;
    }

    // Геттеры для ранга и масти карты
    public int getRank() {
        return rank;
    }

    public int getSuit() {
        return suit;
    }
}
