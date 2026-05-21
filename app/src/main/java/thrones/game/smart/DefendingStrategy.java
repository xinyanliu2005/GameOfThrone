package thrones.game.smart;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import thrones.game.BotMove;
import thrones.game.PileInformation;
import thrones.game.Suit;

import java.util.*;

public class DefendingStrategy implements SelectionStrategy {
    public Optional<BotMove> selectMove(Hand hand, PileInformation board, int playerIdentifier) {

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

                if (defenceChange > currentLargestEffect) {
                    currentLargestEffect = defenceChange;
                    cardWithLargestEffect = currentCard;
                    targetedPileIndex = ownPileIndex;
                }
            } else if (suit.isMagic()) {
                int attackChange = -SelectionStrategy.attackDelta(currentCard, board, 1 - ownPileIndex);

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
