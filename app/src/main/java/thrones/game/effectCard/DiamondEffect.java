package thrones.game.effectCard;

import thrones.game.Rank;
import thrones.game.Suit;

/**
 * Concrete Decorator for Diamonds (i.e. magic) cards.
 * Subtracts the card's rank score from the wrapped character's attack value
 * if the wrapped character's last suit is a Club, or from its defence value
 * if the wrapped character's last suit is a Spade.
 * Has no effect if the last suit was a Heart.
 * If this card's rank matches the most recently wrapped card's rank,
 * then the effect is doubled.
 * Defence doesn't change.
 */
public class DiamondEffect extends EffectCard {
    public DiamondEffect(AffectedCharacter decoratedCharacter, Rank rank) {
        super(decoratedCharacter, rank);
    }

    /**
     * If most recent wrapped card is an attack card, subtract this cards rank from the wrapped
     * characters attack value
     * If this card's rank matches most recently wrapped card's rank, double the effect
     * @return the new attack score
     */
    @Override
    public int getAttack() {
        if (this.decoratedCharacter.getLastSuit() != Suit.CLUBS) return this.decoratedCharacter.getAttack();

        int multiplier = (this.rank.getScoreValue() == this.decoratedCharacter.getRank().getScoreValue() ? 2 : 1);
        return Math.max(this.decoratedCharacter.getAttack() - (this.rank.getScoreValue() * multiplier), 0);
    }

    /**
     * If most recent wrapped card is a defence card, subtract this cards rank from the wrapped
     * characters defence value
     * If this card's rank matches most recently wrapped card's rank, double the effect
     * @return the new defence score
     */
    @Override
    public int getDefence() {
        if (this.decoratedCharacter.getLastSuit() != Suit.SPADES) return this.decoratedCharacter.getDefence();

        int multiplier = (this.rank.getScoreValue() == this.decoratedCharacter.getRank().getScoreValue() ? 2 : 1);
        return Math.max(this.decoratedCharacter.getDefence() - (this.rank.getScoreValue() * multiplier), 0);
    }

    /**
     * getLastSuit() is used by Diamond Effects to determine how it should apply its changes
     * Diamonds are determined by the last effect card other than a diamond, so don't store/return Diamond
     */
    @Override
    public Suit getLastSuit() {
        return this.decoratedCharacter.getLastSuit();
    }

}
