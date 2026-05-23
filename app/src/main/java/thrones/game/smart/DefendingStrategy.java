package thrones.game.smart;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import thrones.game.BotMove;
import thrones.game.PileInformation;
import thrones.game.Suit;

import java.util.*;

/**
 * Concrete strategy for defending mode. Picks the move that creates the largest effect by either increasing
 * the bot's team defence, or decreasing the opponent's attack
 */
public class DefendingStrategy implements SelectionStrategy {

    /**
     * Finds the move that will result in the largest effect by either increasing the bots teams defence (placing a
     * Spade card on their character pile), or decreasing the oppositions attack (placing a diamond on the opposition pile)
     * @param hand the players hand (i.e. to access all the cards they have)
     * @param board current state of the board
     * @param playerIdentifier index of the player
     * @return a possible BotMove, indicating the move that the player made
     */
    public Optional<BotMove> selectMove(Hand hand, PileInformation board, int playerIdentifier) {

        // create list of relevant cards (i.e. defence and magic cards)
        List<Card> effectCards = hand.getCardList()
                .stream()
                .filter(card -> {
                    Suit cardSuit = (Suit) card.getSuit();
                    return cardSuit.isDefence() || cardSuit.isMagic();
                })
                .toList();

        Card cardWithLargestEffect = null;
        int currentLargestEffect = 0;

        int ownPileIndex = playerIdentifier % 2;
        int targetedPileIndex = ownPileIndex;

        for (Card currentCard : effectCards) {
            Suit suit = (Suit) currentCard.getSuit();
            if (suit.isDefence()) {
                int defenceChange = SelectionStrategy.defenceDelta(currentCard, board, ownPileIndex);

                // if the effect is larger the previous largest effect, update largest effect, related card, and targeted pile
                if (defenceChange > currentLargestEffect) {
                    currentLargestEffect = defenceChange;
                    cardWithLargestEffect = currentCard;
                    targetedPileIndex = ownPileIndex;
                }
            } else if (suit.isMagic()) {
                int attackChange = -SelectionStrategy.attackDelta(currentCard, board, 1 - ownPileIndex);

                // if the effect is larger the previous largest effect, update largest effect, related card, and targeted pile
                // always prefer diamond over spade if == effect
                if (attackChange > 0 && attackChange >= currentLargestEffect) {
                    currentLargestEffect = attackChange;
                    cardWithLargestEffect = currentCard;
                    targetedPileIndex = 1 - ownPileIndex;
                }
            }
        }

        if (cardWithLargestEffect == null) return Optional.empty();

        return Optional.of(new BotMove(cardWithLargestEffect, targetedPileIndex));
    }
}
