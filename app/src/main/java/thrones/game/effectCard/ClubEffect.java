package thrones.game.effectCard;

import thrones.game.Rank;
import thrones.game.Suit;

/**
 * Concrete Decorator for Clubs (i.e. attack) cards.
 * Adds the card's rank score to the wrapped character's attack value
 * if this card's rank matches the most recently wrapped card's rank,
 * then the effect is doubled.
 * Defence doesn't change.
 */
public class ClubEffect extends EffectCard {
    public ClubEffect(AffectedCharacter decoratedCharacter, Rank rank) {
        super(decoratedCharacter, rank);
    }

    /**
     * Add this cards rank to the wrapped characters attack value
     * If this card's rank matches most recently wrapped card's rank, double the effect
     * @return the new attack score
     */
    @Override
    public int getAttack() {
        int multiplier = (this.rank.getScoreValue() == this.decoratedCharacter.getRank().getScoreValue() ? 2 : 1);
        return Math.max(this.decoratedCharacter.getAttack() + (this.rank.getScoreValue() * multiplier), 0);
    }


    /**
     * @return Clubs as the most recent suit
     */
    @Override
    public Suit getLastSuit() {
        return Suit.CLUBS;
    }
}
