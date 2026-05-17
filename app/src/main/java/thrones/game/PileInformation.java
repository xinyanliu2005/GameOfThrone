package thrones.game;

import ch.aplu.jcardgame.Card;
import java.util.List;
import java.util.Optional;

/** Act as the Facade that provides information about the game state. Allowing Player to access each pile's
 * Attack, Defence, and latest card dealt on the pile. */
public interface PileInformation {
    int getPileAttack(int pileIndex);
    int getPileDefence(int pileIndex);
    Optional<Card> getLastPlayedCard(int pileIndex);
    List<Card> getPileCards(int pileIndex);
    int getPlayerScore(int playerIndex);
}
