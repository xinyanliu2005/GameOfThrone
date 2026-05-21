package thrones.game;

import ch.aplu.jcardgame.Card;
import thrones.game.smart.*;

import java.util.Optional;

public class SmartBotPlayer extends Player {
    private final SelectionStrategy characterStrategy = new CharacterSelectionStrategy();
    private final SelectionStrategy attackingStrategy = new AttackingStrategy();
    private final SelectionStrategy defendingStrategy = new DefendingStrategy();
    private final SelectionStrategy minimalPlayStrategy = new MinimalPlayStrategy();



    private boolean justPassed = false;
    private int pendingPileIndex = -1;
    private int lastSeenPlayIndex = -1;

    public SmartBotPlayer(int playerIdentifier) {
        super(playerIdentifier);
    }

    @Override
    public Optional<Card> selectCardToPlay(PileInformation currentBoard, boolean isCharacterRound) {
        if (currentBoard.getPlayIndex() != lastSeenPlayIndex) {
            justPassed = false;
            lastSeenPlayIndex = currentBoard.getPlayIndex();
        }

        SelectionStrategy chosenStrategy = pickStrategy(currentBoard, isCharacterRound);
        Optional<BotMove> move = chosenStrategy.selectMove(getPlayerHand(), currentBoard, getPlayerIdentifier());

        if (move.isEmpty()) {
            justPassed = true;
            pendingPileIndex = -1;
            return Optional.empty();
        }

        if (chosenStrategy == minimalPlayStrategy) justPassed = false;
        pendingPileIndex = move.get().getTargetPileIndex();
        return Optional.of(move.get().getCard());
    }

    private SelectionStrategy pickStrategy(PileInformation currentBoard, boolean isCharacterRound) {
        if (isCharacterRound) return characterStrategy;
        if (justPassed) return minimalPlayStrategy;
        int playersAttack = currentBoard.getPileAttack(getPlayerIdentifier() % 2);
        int oppositionsDefence = currentBoard.getPileDefence(1 - getPlayerIdentifier() % 2);

        return playersAttack <= oppositionsDefence ? attackingStrategy : defendingStrategy;

    }

    @Override
    public int choosePileToPlayOn() {
        int pile = pendingPileIndex;
        pendingPileIndex = -1;
        return pile;
    }
}
