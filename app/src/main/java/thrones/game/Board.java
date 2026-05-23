package thrones.game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Deck;
import ch.aplu.jcardgame.Hand;
import thrones.game.effectCard.AffectedCharacter;
import thrones.game.effectCard.CharacterBuilder;

import java.util.List;
import java.util.Optional;

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

    public void resetPiles() {
        for (Hand pile : piles) {
            pile.removeAll(true);
        }
    }

    public Hand[] getPiles() {
        return piles;
    }

    public void setPlayIndex(int playIndex) {
        this.playIndex = playIndex;
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
        if (targetPile >= 0 && targetPile < scores.length / 2) {
            scores[targetPile] += score;
            scores[targetPile + 2] += score;
        }
    }

    @Override
    public int getPileAttack(int pileIndex) {
        // Implement logic to compute the attack value for the pile
        return CharacterBuilder.fromCards(getPileCards(pileIndex)).map(AffectedCharacter::getAttack).orElse(0);
    }

    @Override
    public int getPileDefence(int pileIndex) {
        // Implement logic to compute the defence value for the pile
        return CharacterBuilder.fromCards(getPileCards(pileIndex)).map(AffectedCharacter::getDefence).orElse(0);
    }

    @Override
    public Optional<Card> getLastPlayedCard(int pileIndex) {
        if (piles == null || pileIndex >= piles.length || piles[pileIndex].isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(piles[pileIndex].getLast());
    }

    @Override
    public List<Card> getPileCards(int pileIndex) {
        if (piles == null || pileIndex >= piles.length || piles[pileIndex].isEmpty()) {
            return List.of();
        }
        return piles[pileIndex].getCardList();
    }

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