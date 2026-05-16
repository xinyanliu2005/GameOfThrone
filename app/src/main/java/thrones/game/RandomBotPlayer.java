package thrones.game;

import ch.aplu.jcardgame.Card;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class RandomBotPlayer extends Player{

    private Random randomGenerator;

    public RandomBotPlayer(int playerIdentifier, Random randomGenerator) {
        super(playerIdentifier);
        this.randomGenerator = randomGenerator;
    }

    public RandomBotPlayer(int playerIdentifier) {
        super(playerIdentifier);
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

        if (validPlayableCards.isEmpty() || (!isCharacterRound && randomGenerator.nextInt(3) == 0)) {
            return Optional.empty();
        } else {
            int randomCardIndex = randomGenerator.nextInt(validPlayableCards.size());
            return Optional.of(validPlayableCards.get(randomCardIndex));
        }
    }

    @Override
    public int choosePileToPlayOn() {
        return randomGenerator.nextInt(2);
    }
}
