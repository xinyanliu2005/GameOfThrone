package thrones.game;

import thrones.game.strategy.*;
import ch.aplu.jcardgame.Card;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public Optional<Card> selectCardToPlay(PileInformation currentBoard, boolean isCharacterRound) {
        List<Card> validPlayableCards = getValidCards(isCharacterRound);
        if (validPlayableCards.isEmpty()) {
            return Optional.empty();
        }

        // By checking board for the current play index
        int currentPlayIndex = 0;
        if (currentBoard != null) {
            currentPlayIndex = currentBoard.getPlayIndex();
        }

        // Get this turn play card
        Optional<Card> autoCandidateCard = playAutoCard(currentPlayIndex);
        if (autoCandidateCard.isEmpty()) {
            return Optional.empty();
        }

        // Get the current turn desired card and target pile
        Card selectedCard = autoCandidateCard.get();
        int autoCandidatePileIndex = getAutoPileIndex();

        // Determine whether it breaks any consideration
        Optional<BotMove> validatedMove = coreStrategy.determineMove(selectedCard, autoCandidatePileIndex,
                currentBoard, getPlayerIdentifier());

        if (validatedMove.isPresent()) {
            this.pendingPileIndex = validatedMove.get().getTargetPileIndex();
            return Optional.of(selectedCard);
        }

        // The move violated one of the considerations, we will pass
        this.pendingPileIndex = -1;
        return Optional.empty();
    }

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
