package thrones.game;

import ch.aplu.jcardgame.Card;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class LegalBotPlayer extends Player {

    private Random randomGenerator;
    private List<String> considerationCodes;

    public LegalBotPlayer(int playerIdentifier, Random randomGenerator, List<String> considerationCodes) {
        super(playerIdentifier);
        this.randomGenerator = randomGenerator;
        this.considerationCodes = considerationCodes != null ? considerationCodes : new ArrayList<>();
    }

    public List<String> getConsiderationCodes() {
        return considerationCodes;
    }

    @Override
    public Optional<Card> selectCardToPlay(PileInformation currentBoard, boolean isCharacterRound) {
        List<Card> validPlayableCards = new ArrayList<>();

        for (Card card : getPlayerHand().getCardList()) {
            Suit cardSuit = (Suit) card.getSuit();
            if (cardSuit.isCharacter() == isCharacterRound) {
                validPlayableCards.add(card);
            }
        }

        if (validPlayableCards.isEmpty()) {
            return Optional.empty();
        }

        // Randomly select a card (same as original random bot base behaviour)
        if (!isCharacterRound && randomGenerator.nextInt(3) == 0) {
            return Optional.empty();
        }

        int randomCardIndex = randomGenerator.nextInt(validPlayableCards.size());
        return Optional.of(validPlayableCards.get(randomCardIndex));
    }

    @Override
    public int choosePileToPlayOn() {
        return randomGenerator.nextInt(2);
    }
}
