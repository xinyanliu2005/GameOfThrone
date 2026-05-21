package thrones.game.strategy;

import ch.aplu.jcardgame.Card;
import thrones.game.BotMove;
import thrones.game.PileInformation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CompositeStrategy implements BotStrategy {
    private final List<BotStrategy> strategies = new ArrayList<>();

    public void addStrategy(BotStrategy strategy) {
        this.strategies.add(strategy);
    }

    @Override
    public Optional<BotMove> determineMove(Card selectedCard, int targetPileIndex, PileInformation boardInfo, int playerIdentifier) {
        // If there are no configured strategies, default to dealing it safely
        if (strategies.isEmpty()) {
            return Optional.of(new BotMove(selectedCard, targetPileIndex));
        }

        // Evaluate the card across ALL active strategies sequentially
        for (BotStrategy strategy : strategies) {
            Optional<BotMove> evaluationResult = strategy.determineMove(selectedCard, targetPileIndex, boardInfo, playerIdentifier);

            // If ANY active consideration rule broke,
            // then it fails our overall strict constraints, and we must pass.
            if (evaluationResult.isEmpty()) {
                return Optional.empty();
            }
        }

        // The move successfully satisfied or survived every consideration check
        return Optional.of(new BotMove(selectedCard, targetPileIndex));
    }
}
