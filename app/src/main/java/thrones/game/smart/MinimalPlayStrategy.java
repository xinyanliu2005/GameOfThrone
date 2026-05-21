package thrones.game.smart;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import thrones.game.BotMove;
import thrones.game.PileInformation;
import thrones.game.Suit;

import java.util.*;

public class MinimalPlayStrategy implements SelectionStrategy {
    public Optional<BotMove> selectMove(Hand hand, PileInformation board, int playerIdentifier) {
        int ownPileIndex = playerIdentifier % 2;
        int oppositionPileIndex = 1 - ownPileIndex;

        Card cardWithSmallestEffect = null;
        int currentSmallestEffect = Integer.MAX_VALUE;
        int targetedPileIndex = ownPileIndex;
        int priority = Integer.MAX_VALUE;

        final int DIAMOND_PRIORITY = 0;
        final int CLUBS_PRIORITY = 1;
        final int SPADES_PRIORITY = 2;

        for (Card currentCard : hand.getCardList()) {
            Suit suit = (Suit) currentCard.getSuit();
            // not interested in character cards here
            if (suit.isCharacter()) continue;

            if (suit.isMagic()) {
                int attackDrop = -SelectionStrategy.attackDelta(currentCard, board, oppositionPileIndex);
                int defenceDrop = -SelectionStrategy.defenceDelta(currentCard, board, oppositionPileIndex);

                // one of attackDrop or defenceDrop will be 0 (i.e. diamond only effects one of them)
                int relevantChange = Math.max(attackDrop, defenceDrop);

                if (relevantChange > 0 && (relevantChange < currentSmallestEffect || (relevantChange == currentSmallestEffect && DIAMOND_PRIORITY < priority))) {
                    cardWithSmallestEffect = currentCard;
                    priority = DIAMOND_PRIORITY;
                    currentSmallestEffect = relevantChange;
                    targetedPileIndex = oppositionPileIndex;
                }
            } else if (suit.isAttack()) {
                int attackIncrease = SelectionStrategy.attackDelta(currentCard, board, ownPileIndex);
                if (attackIncrease > 0 && (attackIncrease < currentSmallestEffect || (attackIncrease == currentSmallestEffect && CLUBS_PRIORITY < priority))) {
                    cardWithSmallestEffect = currentCard;
                    priority = CLUBS_PRIORITY;
                    currentSmallestEffect = attackIncrease;
                    targetedPileIndex = ownPileIndex;
                }
            } else if (suit.isDefence()) {
                int defenceIncrease = SelectionStrategy.defenceDelta(currentCard, board, ownPileIndex);

                if (defenceIncrease > 0 && defenceIncrease < currentSmallestEffect) {
                    currentSmallestEffect = defenceIncrease;
                    priority = SPADES_PRIORITY;
                    cardWithSmallestEffect = currentCard;
                    targetedPileIndex = ownPileIndex;
                }
            }
        }

        if (cardWithSmallestEffect == null) return Optional.empty();

        return Optional.of(new BotMove(cardWithSmallestEffect, targetedPileIndex));
    }
}
