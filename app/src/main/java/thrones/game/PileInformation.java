package thrones.game;

import ch.aplu.jcardgame.Card;
import java.util.List;

/** Act as the Facade that provides information about the game state. Allowing Player to access each pile's
 * Attack, Defence, and latest card dealt on the pile. */
public interface PileInformation {
    int getPileAttack(int pileIndex);
    int getPileDefence(int pileIndex);
    Card getLastPlayedCard(int pileIndex);
    List<Card> getPileCards(int pileIndex);
    int getScore(int pileIndex);
    int getPlayIndex();
}
