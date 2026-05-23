package thrones.game.effectCard;

import thrones.game.Rank;
import thrones.game.Suit;

/**
 * Concrete Component used for representing the Heart card, which is always the first card
 * of the pile.
 * Attack and defence both equal the card's rank score value.
 */
public class HeartCharacter implements AffectedCharacter {
    private final Rank rank;
    public HeartCharacter(Rank rank) {
        this.rank = rank;
    }

    /**
     * @return current rank of the character
     */
    public Rank getRank() {
        return this.rank;
    }

    /**
     * @return current attack score of the character
     */
    public int getAttack() {
        return this.rank.getScoreValue();
    }

    /**
     * @return current defence score of the character
     */
    public int getDefence() {
        return this.rank.getScoreValue();
    }

    /**
     * @return suit of the character (currently Hearts)
     */
    public Suit getLastSuit() {
        return Suit.HEARTS;
    }
}
