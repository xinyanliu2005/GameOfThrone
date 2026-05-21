package thrones.game.strategy;

import ch.aplu.jcardgame.Card;
import thrones.game.PileInformation;
import thrones.game.Suit;


import java.util.Optional;

public class OdStrategy implements BotStrategy {

    /** If selected card is Spades, and will increase enemy team Defence, pass.
     * Else deal it. */
    @Override
    public Optional<BotMove> determineMove(Card selectedCard, int targetPileIndex, PileInformation boardInfo, int playerIdentifier) {
        int opponentPileIndex = (playerIdentifier % 2 == 0) ? 1 : 0;
        Suit cardSuit = (Suit) selectedCard.getSuit();

        if (cardSuit == Suit.SPADES && targetPileIndex == opponentPileIndex) {
            return Optional.empty();
        }
        return Optional.of(new BotMove(selectedCard, targetPileIndex));
    }
}