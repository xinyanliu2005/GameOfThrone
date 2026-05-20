package thrones.game.Strategy;
import ch.aplu.jcardgame.Card;
import thrones.game.PileInformation;
import java.util.List;
import java.util.Optional;

public interface BotStrategy {
    /**
     * Evaluates the hand and board to determine a move.
     * @return A BotMove containing the chosen Card and Pile, or Optional.empty() if the strategy doesn't apply.
     */
    Optional<BotMove> determineMove(List<Card> validCards, PileInformation boardInfo, boolean isCharacterRound, int playerIdentifier);
}
