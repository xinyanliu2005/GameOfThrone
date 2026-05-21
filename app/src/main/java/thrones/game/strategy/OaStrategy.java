package thrones.game.strategy;

import ch.aplu.jcardgame.Card;
import thrones.game.BotMove;
import thrones.game.PileInformation;

import thrones.game.Suit;
import java.util.Optional;

public class OaStrategy implements BotStrategy {

    /** If selected card is Clubs, and will increase enemy team Attack, pass.
     * Else deal it. */
    @Override
    public Optional<BotMove> determineMove(Card selectedCard, int targetPileIndex, PileInformation boardInfo, int playerIdentifier) {
        int opponentPileIndex = (playerIdentifier % 2 == 0) ? 1 : 0;
        Suit cardSuit = (Suit) selectedCard.getSuit();

        if (cardSuit == Suit.CLUBS && targetPileIndex == opponentPileIndex) {
            return Optional.empty();
        }
        return Optional.of(new BotMove(selectedCard, targetPileIndex));
    }
}
