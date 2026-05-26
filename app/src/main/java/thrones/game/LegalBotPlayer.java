package thrones.game;

import thrones.game.strategy.*;
import ch.aplu.jcardgame.Card;
import java.util.ArrayList;
import java.util.List;

public class LegalBotPlayer extends Player {
    private List<String> considerationCodes;

    private CompositeStrategy coreStrategy;
    private int pendingPileIndex = -1;


    public LegalBotPlayer(int playerIdentifier, List<String> considerationCodes) {
        super(playerIdentifier);
        this.considerationCodes = considerationCodes != null ? considerationCodes : new ArrayList<>();

        coreStrategy = buildStrategy(this.considerationCodes);
    }

    @Override
    public Card selectCardToPlay(PileInformation currentBoard, boolean isCharacterRound) {
        List<Card> validPlayableCards = getValidCards(isCharacterRound);
        if (validPlayableCards.isEmpty()) {
            return null;
        }

        // By checking board for the current play index
        int currentPlayIndex = 0;
        if (currentBoard != null) {
            currentPlayIndex = currentBoard.getPlayIndex();
        }

        // Get this turn play card
        Card selectedCard = playAutoCard(currentPlayIndex);
        if (selectedCard == null) {
            return null;
        }

        // Get the current turn target pile
        int autoCandidatePileIndex = getAutoPileIndex();

        // Determine whether it breaks any consideration
        BotMove validatedMove = coreStrategy.determineMove(selectedCard, autoCandidatePileIndex,
                currentBoard, getPlayerIdentifier());

        // Check whether the move is validated by strategy, also won't break game rule
        if (validatedMove != null && isMoveValid(selectedCard, autoCandidatePileIndex, currentBoard)) {
            this.pendingPileIndex = validatedMove.getTargetPileIndex();
            return selectedCard;
        }

        // The move violated one of the considerations, we will pass
        this.pendingPileIndex = -1;
        return null;
    }

    /* Add desire strategy into the composite based on configuration. */
    private CompositeStrategy buildStrategy(List<String> codes) {
        CompositeStrategy composite = new CompositeStrategy();
        for (String code : codes) {
            switch (code.toUpperCase()) {
                case "OA" -> composite.addStrategy(new OaStrategy());
                case "OD" -> composite.addStrategy(new OdStrategy());
                case "OM" -> composite.addStrategy(new OmStrategy());
                case "TA" -> composite.addStrategy(new TaStrategy());
                case "TD" -> composite.addStrategy(new TdStrategy());
                case "TM" -> composite.addStrategy(new TmStrategy());
            }
        }

        return composite;
    }

    @Override
    public int choosePileToPlayOn() {
        int chosenPile = pendingPileIndex;
        this.pendingPileIndex = -1;
        return chosenPile;
    }
}
