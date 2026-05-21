package thrones.game.Strategy;

import ch.aplu.jcardgame.Card;
import thrones.game.BotMove;
import thrones.game.PileInformation;
import thrones.game.Suit;
import java.util.Optional;


public class OmStrategy implements BotStrategy {

    /** If selected card is Diamond, and will decrease enemy Attack and Defence, deal it.
     * Else pass. */
    @Override
    public Optional<BotMove> determineMove(Card selectedCard, int targetPileIndex, PileInformation boardInfo, int playerIdentifier) {
        int opponentPileIndex = (playerIdentifier % 2 == 0) ? 1 : 0;
        Suit cardSuit = (Suit) selectedCard.getSuit();

        if (cardSuit == Suit.DIAMONDS && targetPileIndex == opponentPileIndex) {
            return Optional.of(new BotMove(selectedCard, targetPileIndex)); // Condition met -> Must play
        }
        return Optional.empty(); // Condition not met -> Pass
    }
}