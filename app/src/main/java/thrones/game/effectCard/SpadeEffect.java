package thrones.game.effectCard;

import thrones.game.Rank;
import thrones.game.Suit;

public class SpadeEffect extends EffectCard {
    public SpadeEffect(AffectedCharacter decoratedCharacter, Rank rank) {
        super(decoratedCharacter, rank);
    }

    @Override
    public int getDefence() {
        int multiplier = (this.rank.getScoreValue() == this.decoratedCharacter.getRank().getScoreValue() ? 2 : 1);
        return Math.max(this.decoratedCharacter.getDefence() + (this.rank.getScoreValue() * multiplier), 0);
    }

    @Override
    public Suit getLastSuit() {
        return Suit.SPADES;
    }

}
