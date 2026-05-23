package thrones.game.effectCard;

import thrones.game.Rank;
import thrones.game.Suit;

/**
 * Concrete Decorator for Spades (i.e. defence) cards.
 * Adds the card's rank score to the wrapped character's defence value
 * if this card's rank matches the most recently wrapped card's rank,
 * then the effect is doubled.
 * Attack doesn't change.
 */
public class SpadeEffect extends EffectCard {
    public SpadeEffect(AffectedCharacter decoratedCharacter, Rank rank) {
        super(decoratedCharacter, rank);
    }

    /**
     * Add this cards rank to the wrapped characters defence value
     * If this card's rank matches most recently wrapped card's rank, double the effect
     * @return the new defence score
     */
    @Override
    public int getDefence() {
        int multiplier = (this.rank.getScoreValue() == this.decoratedCharacter.getRank().getScoreValue() ? 2 : 1);
        return Math.max(this.decoratedCharacter.getDefence() + (this.rank.getScoreValue() * multiplier), 0);
    }

    /**
     * @return Spades as the most recent suit
     */
    @Override
    public Suit getLastSuit() {
        return Suit.SPADES;
    }

}
