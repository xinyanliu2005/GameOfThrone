package thrones.game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Deck;
import ch.aplu.jcardgame.Hand;
import thrones.game.effectCard.AffectedCharacter;
import thrones.game.effectCard.CharacterBuilder;

import java.util.List;

/**
 * Board owns the game state (e.g. piles, player scores, play index) and implements the PileInformation facade
 * which players use to query game state
 */
public class Board implements PileInformation {

    private final int NORTH = 0;
    private final int SOUTH = 1;
    private final Hand[] piles = new Hand[2];
    private final int[] scores;
    private int playIndex = 0;

    public Board(int playerSize, Deck deck) {
        this.scores = new int[playerSize];
        piles[0] = new Hand(deck);
        piles[1] = new Hand(deck);
    }

    /**
     * Clear both piles. Called at the start of each play phase
     */
    public void resetPiles() {
        for (Hand pile : piles) {
            pile.removeAll(true);
        }
    }

    public Hand[] getPiles() {
        return piles;
    }

    @Override
    public int getPlayIndex() {
        return playIndex;
    }

    /** Carry out the fight
     * @return FightResult Contains information of whether each team successfully attack enemy team */
    public FightResult executeFight() {
        int northAttack = getPileAttack(NORTH);
        int northDefence = getPileDefence(NORTH);

        int southAttack = getPileAttack(SOUTH);
        int southDefence = getPileDefence(SOUTH);

        boolean northAttackSucceeded;
        boolean southAttackSucceeded;

        // North attacks South
        if (northAttack > southDefence) {
            // North wins attack, gets South's heart value
            updateScore(NORTH, ((Rank) piles[SOUTH].get(0).getRank()).getScoreValue());
            northAttackSucceeded = true;
        } else {
            // South wins defense, gets North's heart value
            updateScore(SOUTH, ((Rank) piles[SOUTH].get(0).getRank()).getScoreValue());
            northAttackSucceeded = false;
        }

        // South attacks North
        if (southAttack > northDefence) {
            // South wins attack, gets North's heart value
            updateScore(SOUTH, ((Rank) piles[NORTH].get(0).getRank()).getScoreValue());
            southAttackSucceeded = true;
        } else {
            // North wins defense, gets South's heart value
            updateScore(NORTH, ((Rank) piles[NORTH].get(0).getRank()).getScoreValue());
            southAttackSucceeded = false;
        }

        playIndex++;

        return new FightResult(northAttackSucceeded, southAttackSucceeded);
    }

    /* Automatically update score after each play's fight */
    private void updateScore(int targetPile, int score) {
        // targetPile is a team index (e.g. 0 = NORTH, 1 = SOUTH),
        // but should give score to both teammates (i and i+2)
        if (targetPile >= 0 && targetPile < scores.length / 2) {
            scores[targetPile] += score;
            scores[targetPile + 2] += score;
        }
    }

    /**
     * @param pileIndex pile to calculate the current attack value of
     * @return current attack value of the given pile, utilising the decorator chain
     */
    @Override
    public int getPileAttack(int pileIndex) {
        AffectedCharacter character = CharacterBuilder.fromCards(getPileCards(pileIndex));
        return character != null ? character.getAttack() : 0;
    }

    /**
     * @param pileIndex pile to calculate the current defence value of
     * @return current defence value of the given pile, utilising the decorator chain
     */
    @Override
    public int getPileDefence(int pileIndex) {
        AffectedCharacter character = CharacterBuilder.fromCards(getPileCards(pileIndex));
        return character != null ? character.getDefence() : 0;
    }

    /**
     * @param pileIndex pile to calculate the current defence value of
     * @return card played most recently on the given pile
     */
    @Override
    public Card getLastPlayedCard(int pileIndex) {
        if (piles == null || pileIndex >= piles.length || piles[pileIndex].isEmpty()) {
            return null;
        }
        return piles[pileIndex].getLast();
    }

    /**
     * @param pileIndex index of the pile being queried
     * @return a List of Cards in the indicated pile
     */
    @Override
    public List<Card> getPileCards(int pileIndex) {
        if (piles == null || pileIndex >= piles.length || piles[pileIndex].isEmpty()) {
            return List.of();
        }
        return piles[pileIndex].getCardList();
    }

    /**
     * @param pileIndex index of the pile being queried
     * @return get the score associated with the given pile
     */
    @Override
    public int getScore(int pileIndex) {
        if (pileIndex >= 0 && pileIndex < scores.length) {
            return scores[pileIndex];
        }
        return 0;
    }

    public int[] getScores() {
        return scores;
    }
}