package thrones.game.strategy;

import ch.aplu.jcardgame.Card;
import thrones.game.BotMove;
import thrones.game.PileInformation;
import thrones.game.Suit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CompositeStrategy implements BotStrategy {
    private final List<BotStrategy> strategies = new ArrayList<>();

    public void addStrategy(BotStrategy strategy) {
        this.strategies.add(strategy);
    }

    public Optional<BotMove> determineMove(Card selectedCard, int targetPileIndex, PileInformation boardInfo, int playerIdentifier) {
        if (strategies.isEmpty()) {
            return Optional.empty();
        }

        for (BotStrategy strategy : strategies) {
            if (strategy.isSafeToPlay(selectedCard, targetPileIndex, boardInfo, playerIdentifier)) {
                return Optional.of(new BotMove(selectedCard, targetPileIndex));
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean isSafeToPlay(Card selectedCard, int targetPileIndex, PileInformation boardInfo, int playerIdentifier) {
        return determineMove(selectedCard, targetPileIndex, boardInfo, playerIdentifier).isPresent();
    }
}