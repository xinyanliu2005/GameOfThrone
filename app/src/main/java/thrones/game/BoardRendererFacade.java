package thrones.game;

import ch.aplu.jcardgame.CardGame;
import ch.aplu.jgamegrid.*;

import java.awt.*;

/**
 * Facade over the JGameGrid rendering API for the score labels and pile-status text on the game board
 *
 * extracted from GameOfThrones to reduce responsibilities of GameOfThrones
 */
public class BoardRendererFacade {
    private final CardGame game;
    private final Color bgColor;
    Font bigFont = new Font("Arial", Font.BOLD, 36);
    Font smallFont = new Font("Arial", Font.PLAIN, 10);

    private final Actor[] pileTextActors = {null, null};
    private final Actor[] scoreActors;

    private final Location[] scoreLocations = {
            new Location(575, 675),
            new Location(25, 575),
            new Location(25, 25),
            new Location(575, 125)
    };

    private final Location[] pileStatusLocations = {
            new Location(300, 200),
            new Location(300, 520)
    };

    public BoardRendererFacade(
            CardGame game,
            int nbPlayers,
            Color bgColor
    ) {
        this.game = game;
        this.bgColor = bgColor;
        this.scoreActors = new Actor[nbPlayers];
    }

    public void initScore() {
        for (int i = 0; i < scoreActors.length; i++) {
            String text = "P" + i + "-0";
            scoreActors[i] = new TextActor(text, Color.WHITE, bgColor, bigFont);
            game.addActor(scoreActors[i], scoreLocations[i]);
        }

        String text = "Attack: 0 - Defence: 0";
        for (int i = 0; i < pileTextActors.length; i++) {
            pileTextActors[i] = new TextActor(text, Color.WHITE, bgColor, smallFont);
            game.addActor(pileTextActors[i], pileStatusLocations[i]);
        }
    }

    public void updateScores(int[] scores) {
        for (int i = 0; i < scoreActors.length; i++) {
            game.removeActor(scoreActors[i]);
            String text = "P" + i + "-" + scores[i];
            scoreActors[i] = new TextActor(text, Color.WHITE, bgColor, bigFont);
            game.addActor(scoreActors[i], scoreLocations[i]);
        }
    }

    private void updatePileRankState(int pileIndex, int attackRank, int defenceRank) {
        TextActor currentPile = (TextActor) pileTextActors[pileIndex];
        game.removeActor(currentPile);
        String text = "Attack: " + attackRank + " - Defence: " + defenceRank;
        pileTextActors[pileIndex] = new TextActor(text, Color.WHITE, bgColor, smallFont);
        game.addActor(pileTextActors[pileIndex], pileStatusLocations[pileIndex]);
    }

    public void updatePileRanks(Board board) {
        for (int j = 0; j < pileTextActors.length; j++) {
            updatePileRankState(j, board.getPileAttack(j), board.getPileDefence(j));
        }
    }

}
