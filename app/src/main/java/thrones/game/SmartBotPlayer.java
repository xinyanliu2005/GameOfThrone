package thrones.game;

import ch.aplu.jcardgame.Card;
import java.util.Optional;

public class SmartBotPlayer extends Player {

    public SmartBotPlayer(int playerIdentifier) {
        super(playerIdentifier);
    }

    @Override
    public Optional<Card> selectCardToPlay(PileInformation currentBoard, boolean isCharacterRound) {
        // TODO: Implement your evaluation function or search algorithm here.
        // Evaluate the valid cards in getPlayerHand().getCardList() against
        // the current state of the game board to find the optimal play.

        return Optional.empty(); // Placeholder to be replaced by your logic
    }

    @Override
    public int choosePileToPlayOn() {
        // TODO: Implement logic to determine the most advantageous pile.
        // This should score the impact of placing the previously selected
        // card on Pile.NORTH versus Pile.SOUTH to maximize points.

        return 0; // Placeholder to be replaced by your logic
    }
}
