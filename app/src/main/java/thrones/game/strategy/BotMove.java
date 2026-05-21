package thrones.game.strategy;

import ch.aplu.jcardgame.Card;

public class BotMove {
    private final Card card;
    private final int targetPileIndex;

    public BotMove(Card card, int targetPileIndex) {
        this.card = card;
        this.targetPileIndex = targetPileIndex;
    }

    public Card getCard() {
        return card;
    }

    public int getTargetPileIndex() {
        return targetPileIndex;
    }
}
