package thrones.game.strategy;

import ch.aplu.jcardgame.Card;
import thrones.game.BotMove;
import thrones.game.PileInformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CompositeStrategy implements BotStrategy {
    private final List<BotStrategy> STRATEGIES = new ArrayList<>();

    /**
     * Adds a strategy to the composite strategy.
     */
    public void addStrategy(BotStrategy strategy) {
        this.STRATEGIES.add(strategy);
    }


    /** Determine whether we can perform the given move based on pre-configed strategies. */
    public Optional<BotMove> determineMove(Card selectedCard, int targetPileIndex, PileInformation boardInfo, int playerIdentifier) {
        if (STRATEGIES.isEmpty()) {
            return Optional.empty();
        }

        for (BotStrategy strategy : STRATEGIES) {
            if (strategy.isSafeToPlay(selectedCard, targetPileIndex, boardInfo, playerIdentifier)) {
                return Optional.of(new BotMove(selectedCard, targetPileIndex));
            }
        }
        return Optional.empty();
    }

    /** */
    @Override
    public boolean isSafeToPlay(Card selectedCard, int targetPileIndex, PileInformation boardInfo, int playerIdentifier) {
        return determineMove(selectedCard, targetPileIndex, boardInfo, playerIdentifier).isPresent();
    }
}