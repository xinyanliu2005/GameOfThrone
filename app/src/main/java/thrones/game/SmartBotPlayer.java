package thrones.game;

import ch.aplu.jcardgame.Card;
import thrones.game.smart.*;

/**
 * Context in the Strategy pattern for the Smart Bot feature.
 * Contains 1 instance of each strategy and selects which to use
 * based on the current game state
 */
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

    /**
     * Select the Card to play
     * @param currentBoard The latest board information about each pile's Attack, Defence Score, and the latest Card delt
     *                     on each pile. (Not used by autoplay players)
     * @param isCharacterRound True if the player MUST play a character (Heart) card.
     * @return the selected Card
     */
    @Override
    public Card selectCardToPlay(PileInformation currentBoard, boolean isCharacterRound) {
        if (currentBoard.getPlayIndex() != lastSeenPlayIndex) {
            justPassed = false;
            lastSeenPlayIndex = currentBoard.getPlayIndex();
        }

        // pick a selection strategy, and then use that to decide on a move to play
        SelectionStrategy chosenStrategy = pickStrategy(currentBoard, isCharacterRound);
        BotMove move = chosenStrategy.selectMove(getPlayerHand(), currentBoard, getPlayerIdentifier());

        // if move is null means we passed
        if (move == null) {
            justPassed = true;
            pendingPileIndex = -1;
            return null;
        }

        if (chosenStrategy == minimalPlayStrategy) justPassed = false;

        // set the pile being targeted on this move, and then return the card to be played
        pendingPileIndex = move.getTargetPileIndex();
        return move.getCard();
    }

    /**
     * Select the strategy to be used based on the current game state
     * @param currentBoard The latest board information about each pile's Attack, Defence Score, and the latest Card delt
     *                     on each pile. (Not used by autoplay players)
     * @param isCharacterRound True if the player MUST play a character (Heart) card.
     * @return the strategy to use given the current game state
     */
    private SelectionStrategy pickStrategy(PileInformation currentBoard, boolean isCharacterRound) {
        if (isCharacterRound) return characterStrategy;
        if (justPassed) return minimalPlayStrategy;

        // calculate the players attack value and the oppositions defence to work out whether attack/defend strategy
        int playersAttack = currentBoard.getPileAttack(getPlayerIdentifier() % 2);
        int oppositionsDefence = currentBoard.getPileDefence(1 - (getPlayerIdentifier() % 2));

        return playersAttack <= oppositionsDefence ? attackingStrategy : defendingStrategy;

    }

    /**
     * @return the pile being targeted and reset it to a sentinel value
     */
    @Override
    public int choosePileToPlayOn() {
        int pile = pendingPileIndex;
        pendingPileIndex = -1;
        return pile;
    }
}
