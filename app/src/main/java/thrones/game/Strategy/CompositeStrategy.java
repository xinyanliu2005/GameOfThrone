package thrones.game.Strategy;

import ch.aplu.jcardgame.Card;
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
    public Optional<BotMove> determineMove(List<Card> validCards, PileInformation boardInfo, boolean isCharacterRound, int playerIdentifier) {
        for (BotStrategy strategy : strategies) {
            Optional<BotMove> move = strategy.determineMove(validCards, boardInfo, isCharacterRound, playerIdentifier);

            // If the strategy found a valid move, return it immediately
            if (move.isPresent()) {
                return move;
            }
        }

        // No strategies yielded a move (falls back to random or pass)
        return Optional.empty();
    }
}
