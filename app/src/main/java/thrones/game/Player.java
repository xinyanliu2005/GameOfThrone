package thrones.game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class Player {
    private int playerIdentifier;
    private Hand playerHand;
    private int currentScore;
    private MoveData moveData;

    /**
     * Data class for storing all auto-play move information for this player.
     * Stores moves as Rank-Suit-Deck strings (e.g., "2H-0") for each play index.
     */
    public class MoveData {
        private Map<Integer, List<String>> movesByPlay = new HashMap<>();
        private int currentIndex = 0;
        private int lastPileIndex = -1;

        /** @param playIndex Current round number
         * @param moves Moves to perform for the round */
        public void setMoves(int playIndex, List<String> moves) {
            movesByPlay.put(playIndex, new ArrayList<>(moves));
        }

        public String getNextMoveString(int playIndex) {
            List<String> moves = movesByPlay.get(playIndex);
            if (moves != null && currentIndex < moves.size()) {
                return moves.get(currentIndex++);
            }
            return null;
        }

        public void resetIndex() {
            currentIndex = 0;
        }
    }

    public Player(int playerIdentifier) {
        this.playerIdentifier = playerIdentifier;
        this.currentScore = 0;
        this.moveData = new MoveData(); // Initialized when the player is created
    }

    /**
     * Uses the stored MoveData to determine the next card to play in auto mode.
     * @param playIndex The current play (round) index.
     * @return An Optional containing the Card to play, or empty if no move is defined.
     */
    public Optional<Card> playAutoCard(int playIndex) {
        String moveStr = moveData.getNextMoveString(playIndex);
        if (moveStr == null) return Optional.empty();

        String[] parts = moveStr.split("-");
        String cardName = parts[0];
        moveData.lastPileIndex = Integer.parseInt(parts[1]);

        return Optional.ofNullable(findCardInHand(cardName));
    }

    /**
     * Retrieves the pile index corresponding to the last card returned by playAutoCard.
     * @return The pile index (0 for North, 1 for South).
     */
    public int getAutoPileIndex() {
        return moveData.lastPileIndex;
    }

    private Card findCardInHand(String cardName) {
        if (playerHand == null) return null;
        String rankStr = cardName.substring(0, cardName.length() - 1);
        String suitStr = cardName.substring(cardName.length() - 1);
        for (Card card : playerHand.getCardList()) {
            Rank r = (Rank) card.getRank();
            Suit s = (Suit) card.getSuit();
            if (String.valueOf(r.getShortHandValue()).equals(rankStr) &&
                s.getSuitShortHand().equals(suitStr)) {
                return card;
            }
        }
        return null;
    }

    public MoveData getMoveData() {
        return moveData;
    }

    public void setAutoMoves(int playIndex, List<String> moves) {
        moveData.setMoves(playIndex, moves);
    }

    public void resetMovementIndex() {
        moveData.resetIndex();
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
     *                     on each pile. (Not used by auto-play players)
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
