package thrones.game;

import ch.aplu.jcardgame.Card;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LegalBotPlayer extends Player {

    public LegalBotPlayer(int playerIdentifier) {
        super(playerIdentifier);
    }

    @Override
    public Optional<Card> selectCardToPlay(PileInformation currentBoard, boolean isCharacterRound) {
        List<Card> validPlayableCards = getValidCards(isCharacterRound);

        if (validPlayableCards.isEmpty()) {
            return Optional.empty(); // Pass if no valid cards are held
        } else {
            // A basic legal move: simply return the first valid card found.
            // If implementing the Composite Pattern later, this selection
            // logic can be delegated to a composite rule evaluator.
            return Optional.of(validPlayableCards.get(0));
        }
    }

    @Override
    public int choosePileToPlayOn() {
        // Any pile is technically a legal move, so it defaults to the first pile.
        return 0;
    }
}