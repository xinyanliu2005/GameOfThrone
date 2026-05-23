package thrones.game.strategy;

import ch.aplu.jcardgame.Card;
import thrones.game.PileInformation;
import thrones.game.Suit;

public class TmStrategy implements BotStrategy {
    @Override
    public boolean isSafeToPlay(Card selectedCard, int targetPileIndex, PileInformation boardInfo, int playerIdentifier) {
        int ownPileIndex = (playerIdentifier % 2 == 0) ? 0 : 1;
        Suit cardSuit = (Suit) selectedCard.getSuit();

        if (cardSuit == Suit.DIAMONDS && targetPileIndex == ownPileIndex) {
            return false;
        }
        return cardSuit == Suit.DIAMONDS;
    }
}