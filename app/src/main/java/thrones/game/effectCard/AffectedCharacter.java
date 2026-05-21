package thrones.game.effectCard;

import thrones.game.Rank;
import thrones.game.Suit;

public interface AffectedCharacter {
    int getAttack();
    int getDefence();
    Suit getLastSuit();
    Rank getRank();
}
