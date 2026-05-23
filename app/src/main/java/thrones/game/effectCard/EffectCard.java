package thrones.game.effectCard;

import thrones.game.Rank;

/**
 * Abstract Decorator for wrapping an AffectedCharacter and modifying its attack/defence values
 * Concrete decorators only override the methods relevant to their effects, otherwise
 * the default methods, e.g. getAttack(), delegate to their wrapped character
 */
public abstract class EffectCard implements AffectedCharacter {
    protected final AffectedCharacter decoratedCharacter;
    protected final Rank rank;

    protected EffectCard(AffectedCharacter decoratedCharacter, Rank rank) {
        this.decoratedCharacter = decoratedCharacter;
        this.rank = rank;
    }

    /**
     * @return current attack score of the character
     */
    @Override
    public int getAttack() {
        return this.decoratedCharacter.getAttack();
    }

    /**
     * @return current defence score of the character
     */
    @Override
    public int getDefence() {
        return this.decoratedCharacter.getDefence();
    }

    /**
     * @return the current rank of the character
     */
    @Override
    public Rank getRank() {
        return this.rank;
    }
}
