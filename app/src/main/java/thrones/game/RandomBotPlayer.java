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
        this.randomGenerator = (randomGenerator != null) ? randomGenerator : new Random();
    }

    public RandomBotPlayer(int playerIdentifier) {
        this(playerIdentifier, new Random());
    }

    @Override
    public Optional<Card> selectCardToPlay(PileInformation currentBoard, boolean isCharacterRound) {
        List<Card> validPlayableCards = getValidCards(isCharacterRound);

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
