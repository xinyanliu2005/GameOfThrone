package thrones.game.smart;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import thrones.game.*;

import java.util.*;

public class AttackingStrategy implements SelectionStrategy {

    public Optional<BotMove> selectMove(Hand hand, PileInformation board, int playerIdentifier) {

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

                if (attackChange > currentLargestEffect) {
                    currentLargestEffect = attackChange;
                    cardWithLargestEffect = currentCard;
                    targetedPileIndex = ownPileIndex;
                }
            } else if (suit.isMagic()) {
                int defenceChange = -SelectionStrategy.defenceDelta(currentCard, board, 1 - ownPileIndex);

                // always prefer diamond over club if == effect
                if (defenceChange > 0 && defenceChange >= currentLargestEffect) {
                    currentLargestEffect = defenceChange;
                    cardWithLargestEffect = currentCard;
                    targetedPileIndex = 1 - ownPileIndex;
                }
            }
        }

        if (cardWithLargestEffect == null) return Optional.empty();

        return Optional.of(new BotMove(cardWithLargestEffect, targetedPileIndex));
    }
}
