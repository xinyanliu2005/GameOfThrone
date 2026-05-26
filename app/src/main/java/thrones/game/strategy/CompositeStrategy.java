package thrones.game.strategy;

import ch.aplu.jcardgame.Card;
import thrones.game.BotMove;
import thrones.game.PileInformation;

import java.util.ArrayList;
import java.util.List;

public class CompositeStrategy implements BotStrategy {
    private final List<BotStrategy> STRATEGIES = new ArrayList<>();

    /**
     * Adds a strategy to the composite strategy.
     */
    public void addStrategy(BotStrategy strategy) {
        this.STRATEGIES.add(strategy);
    }


    /** Determine whether we can perform the given move based on pre-configed strategies. */
    public BotMove determineMove(Card selectedCard, int targetPileIndex, PileInformation boardInfo, int playerIdentifier) {
        if (STRATEGIES.isEmpty()) {
            return null;
        }

        for (BotStrategy strategy : STRATEGIES) {
            if (strategy.isSafeToPlay(selectedCard, targetPileIndex, boardInfo, playerIdentifier)) {
                return new BotMove(selectedCard, targetPileIndex);
            }
        }
        return null;
    }

    /** */
    @Override
    public boolean isSafeToPlay(Card selectedCard, int targetPileIndex, PileInformation boardInfo, int playerIdentifier) {
        return determineMove(selectedCard, targetPileIndex, boardInfo, playerIdentifier) != null;
    }
}