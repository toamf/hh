/**
 * Класс, представляющий игрока.
 */
class Player {
    private final String name;           // Имя игрока
    private final CardSequence hand;     // Последовательность карт у игрока

    /**
     * Конструктор класса Player.
     * @param name Имя игрока.
     * @param deckSize Размер колоды для создания последовательности карт.
     */
    public Player(String name, int deckSize) {
        this.name = name;
        this.hand = new CardSequence(deckSize);
    }

    /**
     * Добавляет карту в руку игрока.
     * @param card Карта, которую нужно добавить.
     * @param index Индекс, на который нужно добавить карту.
     */
    public void addCard(Card card, int index) {
        hand.addCard(card, index);
    }

    /**
     * Выводит на экран карты в руке игрока.
     */
    public void showHand() {
        System.out.println(name + ":");
        for (int i = 0; i < hand.getLength(); i++) {
            Card card = hand.getCard(i);
            if (card != null) {
                System.out.println(card.getRank() + " " + card.getSuit());
            }
        }
    }
}
