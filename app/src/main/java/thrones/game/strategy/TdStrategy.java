package thrones.game.strategy;

import ch.aplu.jcardgame.Card;
import thrones.game.PileInformation;
import thrones.game.Suit;
import java.util.Optional;

public class TdStrategy implements BotStrategy {

    /** If selected card is Spades, and will increase own pile Attack, deal it.
     * Else pass. */
    @Override
    public Optional<BotMove> determineMove(Card selectedCard, int targetPileIndex, PileInformation boardInfo, int playerIdentifier) {
        int ownPileIndex = (playerIdentifier % 2 == 0) ? 0 : 1;
        Suit cardSuit = (Suit) selectedCard.getSuit();

        if (cardSuit == Suit.SPADES && targetPileIndex == ownPileIndex) {
            return Optional.of(new BotMove(selectedCard, targetPileIndex));
        }
        return Optional.empty();
    }
}