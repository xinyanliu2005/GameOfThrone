package thrones.game.effectCard;

import thrones.game.Rank;

public abstract class EffectCard implements AffectedCharacter {
    protected final AffectedCharacter decoratedCharacter;
    protected final Rank rank;

    protected EffectCard(AffectedCharacter decoratedCharacter, Rank rank) {
        this.decoratedCharacter = decoratedCharacter;
        this.rank = rank;
    }

    @Override
    public int getAttack() {
        return this.decoratedCharacter.getAttack();
    }

    @Override
    public int getDefence() {
        return this.decoratedCharacter.getDefence();
    }

    @Override
    public Rank getRank() {
        return this.rank;
    }
}
