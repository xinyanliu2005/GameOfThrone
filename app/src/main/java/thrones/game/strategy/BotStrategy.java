package thrones.game.strategy;
import ch.aplu.jcardgame.Card;
import thrones.game.BotMove;
import thrones.game.PileInformation;

import java.util.Optional;

public interface BotStrategy {
    /**
     * Evaluates selected card.
     * @return A BotMove containing the chosen Card and Pile, or Optional.empty() if the strategy doesn't apply.
     */
    public Optional<BotMove> determineMove(Card selectedCard, int targetPileIndex, PileInformation boardInfo, int playerIdentifier);
}
