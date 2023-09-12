/**
 * Класс, представляющий последовательность карт у игрока.
 */
class CardSequence {
    private final Card[] cards;  // Массив карт, представляющий карты в руке у игрока

    /**
     * Конструктор класса CardSequence.
     * @param deckSize Размер колоды для определения максимального числа карт у игрока.
     */
    public CardSequence(int deckSize) {
        cards = new Card[deckSize];
    }

    /**
     * Добавляет карту в руку игрока на указанный индекс.
     * @param card Карта, которую нужно добавить.
     * @param index Индекс, на который нужно добавить карту.
     */
    public void addCard(Card card, int index) {
        cards[index] = card;
    }

    /**
     * Возвращает карту из руки игрока по указанному индексу.
     * @param index Индекс карты.
     * @return Карта на указанном индексе.
     */
    public Card getCard(int index) {
        return cards[index];
    }
    
    /**
     * Возвращает количество карт в руке игрока.
     * @return Количество карт в руке игрока.
     */
    public int getLength() {
        return cards.length;
    }

}
