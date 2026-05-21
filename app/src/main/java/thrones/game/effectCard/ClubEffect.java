package thrones.game.effectCard;

import thrones.game.Rank;
import thrones.game.Suit;

public class ClubEffect extends EffectCard {
    public ClubEffect(AffectedCharacter decoratedCharacter, Rank rank) {
        super(decoratedCharacter, rank);
    }

    @Override
    public int getAttack() {
        int multiplier = (this.rank.getScoreValue() == this.decoratedCharacter.getRank().getScoreValue() ? 2 : 1);
        return Math.max(this.decoratedCharacter.getAttack() + (this.rank.getScoreValue() * multiplier), 0);
    }


    @Override
    public Suit getLastSuit() {
        return Suit.CLUBS;
    }
}
