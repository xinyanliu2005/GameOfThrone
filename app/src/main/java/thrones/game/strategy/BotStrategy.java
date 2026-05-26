package thrones.game.strategy;
import ch.aplu.jcardgame.Card;
import thrones.game.BotMove;
import thrones.game.PileInformation;

public interface BotStrategy {
    /**
     * Evaluates selected card. If it doesn't fall into the consideration, return false.
     * If it violate the consideration, return false.
     * @return whether we can play the card.
     */
    public boolean isSafeToPlay(Card selectedCard, int targetPileIndex, PileInformation boardInfo, int playerIdentifier);
}

