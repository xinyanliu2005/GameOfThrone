package thrones.game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import java.util.Optional;

public abstract class Player {
    private int playerIdentifier;
    private Hand playerHand;
    private int currentScore;

    public Player(int playerIdentifier) {
        this.playerIdentifier = playerIdentifier;
        this.currentScore = 0;
    }

    public int getPlayerIdentifier() {
        return playerIdentifier;
    }

    public void assignInitialHand(Hand initialHand) {
        this.playerHand = initialHand;
    }

    public Hand getPlayerHand() {
        return playerHand;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public void addPointsToScore(int pointsEarned) {
        this.currentScore += pointsEarned;
    }

    /**
     * Determines which card the player will play this turn.
     * @param currentBoard The latest board information about each pile's Attack, Defence Score, and the latest Card delt
     *                     on each pile.
     * @param isCharacterRound True if the player MUST play a character (Heart) card.
     * @return An Optional containing the selected Card, or empty if passing.
     */
    public abstract Optional<Card> selectCardToPlay(PileInformation currentBoard, boolean isCharacterRound);

    /**
     * Determines which pile the player will place their selected card on.
     * @return The integer index of the selected pile.
     */
    public abstract int choosePileToPlayOn();
}
