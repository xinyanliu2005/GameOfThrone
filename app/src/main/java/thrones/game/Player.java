package thrones.game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import java.util.ArrayList;
import java.util.List;
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
     * Filters the player's hand for cards that are valid to play in the current round type.
     * @param isCharacterRound True if searching for character cards, false for non-character cards.
     * @return A list of valid cards.
     */
    public List<Card> getValidCards(boolean isCharacterRound) {
        List<Card> validCards = new ArrayList<>();
        for (Card card : getPlayerHand().getCardList()) {
            Suit cardSuit = (Suit) card.getSuit();
            if (cardSuit.isCharacter() == isCharacterRound) {
                validCards.add(card);
            }
        }
        return validCards;
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
