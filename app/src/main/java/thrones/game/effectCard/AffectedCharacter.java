package thrones.game.effectCard;

import thrones.game.Rank;
import thrones.game.Suit;

/**
 * Component interface used to calculate attack and defence values for a character pile
 */
public interface AffectedCharacter {
    int getAttack();
    int getDefence();

    /**
     * @return the suit of the last non-Diamond card in the AffectedCharacter chain
     */
    Suit getLastSuit();

    /**
     * @return this card's rank to check for matching ranks and therefore doubling effect
     */
    Rank getRank();
}
