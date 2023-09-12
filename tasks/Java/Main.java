/**
 * Класс для демонстрации игры с картами.
 * Задача: создать колоду, перемешать её, раздать карты двум игрокам и вывести карты каждого игрока на экран.
 */
public class Main {
    public static void main(String[] args) {
        Deck deck = new Deck();

        // Выводим исходную колоду на экран
        System.out.println("Исходная колода:");
        for (int i = 0; i < deck.getDeckSize(); i++) {
            Card card = deck.getCard(i);
            System.out.println(card.getRank() + " " + card.getSuit());
        }

        deck.shuffle();

        // Выводим перемешанную колоду на экран
        System.out.println("\nПеремешанная колода:");
        for (int i = 0; i < deck.getDeckSize(); i++) {
            Card card = deck.getCard(i);
            System.out.println(card.getRank() + " " + card.getSuit());
        }

        Player player1 = new Player("Игрок 1", deck.getDeckSize() / 2);
        Player player2 = new Player("Игрок 2", deck.getDeckSize() / 2);

        // Раздаём карты игрокам
        for (int i = 0; i < deck.getDeckSize(); i++) {
            Card card = deck.getCard(i);
            if (i % 2 == 0) {
                player1.addCard(card, i / 2);
            } else {
                player2.addCard(card, i / 2);
            }
        }

        // Выводим карты каждого игрока на экран
        System.out.println("\nКарты игрока 1:");
        player1.showHand();

        System.out.println("\nКарты игрока 2:");
        player2.showHand();
    }
}
