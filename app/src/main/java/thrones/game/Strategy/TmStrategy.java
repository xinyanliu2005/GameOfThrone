package thrones.game.Strategy;

import ch.aplu.jcardgame.Card;
import thrones.game.BotMove;
import thrones.game.PileInformation;
import thrones.game.Suit;
import java.util.Optional;

public class TmStrategy implements BotStrategy {

    /** If selected card is Diamond, which will decrease own team Attack or Defence, pass.
     * Else deal it. */
    @Override
    public Optional<BotMove> determineMove(Card selectedCard, int targetPileIndex, PileInformation boardInfo, int playerIdentifier) {
        int ownPileIndex = (playerIdentifier % 2 == 0) ? 0 : 1;
        Suit cardSuit = (Suit) selectedCard.getSuit();

        if (cardSuit == Suit.DIAMONDS && targetPileIndex == ownPileIndex) {
            return Optional.empty();
        }
        return Optional.of(new BotMove(selectedCard, targetPileIndex));
    }
}