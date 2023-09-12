import java.util.Random;


/**
 * Класс, представляющий колоду карт.
 */
class Deck {
    private final Card[] cards;  // Массив карт, представляющий колоду

    /**
     * Конструктор класса Deck. Создает стандартную колоду карт.
     */
    public Deck() {
        String[] ranks = {"6", "7", "8", "9", "10", "Валет", "Дама", "Король", "Туз"};
        String[] suits = {"Пики", "Трефы", "Бубны", "Черви"};

        cards = new Card[36];

        int index = 0;
        for (int suitIndex = 0; suitIndex < 4; suitIndex++) {
            for (int rankIndex = 0; rankIndex < 9; rankIndex++) {
                cards[index] = new Card(rankIndex, suitIndex);
                index++;
            }
        }
    }

    /**
     * Перемешивает карты в колоде.
     */
    public void shuffle() {
        Random random = new Random();
        for (int i = 0; i < cards.length; i++) {
            int j = random.nextInt(cards.length);
            Card temp = cards[i];
            cards[i] = cards[j];
            cards[j] = temp;
        }
    }

    // Геттеры для работы с колодой
    public Card getCard(int index) {
        return cards[index];
    }

    public int getDeckSize() {
        return cards.length;
    }
}
