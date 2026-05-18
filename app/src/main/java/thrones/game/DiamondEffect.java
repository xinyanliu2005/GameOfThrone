package thrones.game;

public class DiamondEffect extends EffectCard {
    public DiamondEffect(AffectedCharacter decoratedCharacter, Rank rank) {
        super(decoratedCharacter, rank);
    }

    @Override
    public int getAttack() {
        if (this.decoratedCharacter.getLastSuit() != Suit.CLUBS) return this.decoratedCharacter.getAttack();

        int multiplier = (this.rank.getScoreValue() == this.decoratedCharacter.getRank().getScoreValue() ? 2 : 1);
        return Math.max(this.decoratedCharacter.getAttack() - (this.rank.getScoreValue() * multiplier), 0);
    }

    @Override
    public int getDefence() {
        if (this.decoratedCharacter.getLastSuit() != Suit.SPADES) return this.decoratedCharacter.getDefence();

        int multiplier = (this.rank.getScoreValue() == this.decoratedCharacter.getRank().getScoreValue() ? 2 : 1);
        return Math.max(this.decoratedCharacter.getDefence() - (this.rank.getScoreValue() * multiplier), 0);
    }

    // getLastSuit() is used by Diamond Effects to determine how it should apply its changes
    // Diamonds are determined by the last effect card other than a diamond, so don't store/return Diamond
    @Override
    public Suit getLastSuit() {
        return this.decoratedCharacter.getLastSuit();
    }

}
