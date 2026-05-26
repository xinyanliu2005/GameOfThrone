package thrones.game.smart;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import thrones.game.BotMove;
import thrones.game.PileInformation;
import thrones.game.Suit;

import java.util.List;

/**
 * Concrete strategy for attacking mode. Picks the move that creates the largest effect by either increasing
 * the bot's team attack, or decreasing the opponent's defence
 */
public class AttackingStrategy implements SelectionStrategy {

    /**
     * Finds the move that will result in the largest effect by either increasing the bots teams attack (placing a
     * Club card on their character pile), or decreasing the oppositions defence (placing a diamond on the opposition pile)
     * @param hand the players hand (i.e. to access all the cards they have)
     * @param board current state of the board
     * @param playerIdentifier index of the player
     * @return a possible BotMove, indicating the move that the player made
     */
    public BotMove selectMove(Hand hand, PileInformation board, int playerIdentifier) {

        // create list of relevant cards (i.e. attack and magic cards)
        List<Card> effectCards = hand.getCardList()
                .stream()
                .filter(card -> {
                    Suit cardSuit = (Suit) card.getSuit();
                    return cardSuit.isAttack() || cardSuit.isMagic();
                })
                .toList();

        Card cardWithLargestEffect = null;
        int currentLargestEffect = 0;

        int ownPileIndex = playerIdentifier % 2;
        int targetedPileIndex = ownPileIndex;

        for (Card currentCard : effectCards) {
            Suit suit = (Suit) currentCard.getSuit();
            if (suit.isAttack()) {
                int attackChange = SelectionStrategy.attackDelta(currentCard, board, ownPileIndex);

                // if the effect is larger the previous largest effect, update largest effect, related card, and targeted pile
                if (attackChange > currentLargestEffect) {
                    currentLargestEffect = attackChange;
                    cardWithLargestEffect = currentCard;
                    targetedPileIndex = ownPileIndex;
                }
            } else if (suit.isMagic()) {
                int defenceChange = -SelectionStrategy.defenceDelta(currentCard, board, 1 - ownPileIndex);

                // if the effect is larger the previous largest effect, update largest effect, related card, and targeted pile
                // always prefer diamond over club if the effects are the same
                if (defenceChange > 0 && defenceChange >= currentLargestEffect) {
                    currentLargestEffect = defenceChange;
                    cardWithLargestEffect = currentCard;
                    targetedPileIndex = 1 - ownPileIndex;
                }
            }
        }

        if (cardWithLargestEffect == null) return null;

        return new BotMove(cardWithLargestEffect, targetedPileIndex);
    }
}
