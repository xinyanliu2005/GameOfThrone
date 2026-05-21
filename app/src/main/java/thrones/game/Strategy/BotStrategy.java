package thrones.game.Strategy;
import ch.aplu.jcardgame.Card;
import thrones.game.PileInformation;
import java.util.List;
import java.util.Optional;

public interface BotStrategy {
    /**
     * Evaluates selected card.
     * @return A BotMove containing the chosen Card and Pile, or Optional.empty() if the strategy doesn't apply.
     */
    public Optional<BotMove> determineMove(Card selectedCard, int targetPileIndex, PileInformation boardInfo, int playerIdentifier);
}
