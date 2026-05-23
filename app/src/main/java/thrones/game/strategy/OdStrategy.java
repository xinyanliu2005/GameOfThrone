package thrones.game.strategy;

import ch.aplu.jcardgame.Card;
import thrones.game.PileInformation;
import thrones.game.Suit;

public class OdStrategy implements BotStrategy {
    @Override
    public boolean isSafeToPlay(Card selectedCard, int targetPileIndex, PileInformation boardInfo, int playerIdentifier) {
        int opponentPileIndex = (playerIdentifier % 2 == 0) ? 1 : 0;
        Suit cardSuit = (Suit) selectedCard.getSuit();

        if (cardSuit == Suit.SPADES && targetPileIndex == opponentPileIndex) {
            return false;
        }
        return cardSuit == Suit.SPADES;
    }
}