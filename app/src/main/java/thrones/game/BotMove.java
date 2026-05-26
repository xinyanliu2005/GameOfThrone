package thrones.game;

import ch.aplu.jcardgame.Card;

/**
 * BotMove is an object representing a bot's chosen move
 * i.e. the card to play and the index of the pile to play it on
 * Used by both the smart and legal bot
 */
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
