package thrones.game.strategy;

import ch.aplu.jcardgame.Card;
import thrones.game.PileInformation;
import thrones.game.Suit;

public class OmStrategy implements BotStrategy {
    @Override
    public boolean isSafeToPlay(Card selectedCard, int targetPileIndex, PileInformation boardInfo, int playerIdentifier) {
        int opponentPileIndex = (playerIdentifier % 2 == 0) ? 1 : 0;
        return selectedCard.getSuit() == Suit.DIAMONDS && targetPileIndex == opponentPileIndex;
    }
}