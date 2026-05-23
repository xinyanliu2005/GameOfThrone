package thrones.game.strategy;
import ch.aplu.jcardgame.Card;
import thrones.game.BotMove;
import thrones.game.PileInformation;

import java.util.Optional;

public interface BotStrategy {
    /**
     * Evaluates selected card.
     * @return whether we can play the card.
     */
    public boolean isSafeToPlay(Card selectedCard, int targetPileIndex, PileInformation boardInfo, int playerIdentifier);
}

