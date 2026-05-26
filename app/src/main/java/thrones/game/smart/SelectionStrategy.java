package thrones.game.smart;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import thrones.game.BotMove;
import thrones.game.PileInformation;
import thrones.game.effectCard.AffectedCharacter;
import thrones.game.effectCard.CharacterBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Strategy interface to be used by SmartBotPlayer
 * Each concrete strategy encapsulates the algorithm for one of the different
 * decision mode for the SmartBotPlayer
 */
public interface SelectionStrategy {
    BotMove selectMove(Hand hand, PileInformation board, int playerIdentifier);

    /**
     * Static helper to compute the attack changes for a proposed move by
     * creating a temporary CharacterBuilder chain
     * All 4 concrete strategies use this to evaluate potential moves
     * @param card - proposed card to be added to the Character/pile
     * @param board - the current board state
     * @param pileIndex - pile that the proposed chard is to be played upon
     * @return the change in the pile attack if the given card were played on the given pile
     */
    static int attackDelta(Card card, PileInformation board, int pileIndex) {
        List<Card> pileBefore = board.getPileCards(pileIndex);
        List<Card> pileAfter = new ArrayList<>(pileBefore);
        pileAfter.add(card);

        AffectedCharacter charBefore = CharacterBuilder.fromCards(pileBefore);
        int oldAttack = charBefore != null ? charBefore.getAttack() : 0;
        AffectedCharacter charAfter = CharacterBuilder.fromCards(pileAfter);
        int newAttack = charAfter != null ? charAfter.getAttack() : 0;

        return newAttack - oldAttack;
    }

    /**
     * Static helper to compute the defence changes for a proposed move by
     * creating a temporary CharacterBuilder chain
     * All 4 concrete strategies use this to evaluate potential moves
     * @param card - proposed card to be added to the Character/pile
     * @param board - the current board state
     * @param pileIndex - pile that the proposed chard is to be played upon
     * @return the change in the pile defence if the given card were played on the given pile
     */
    static int defenceDelta(Card card, PileInformation board, int pileIndex) {
        List<Card> pileBefore = board.getPileCards(pileIndex);
        List<Card> pileAfter = new ArrayList<>(pileBefore);
        pileAfter.add(card);

        AffectedCharacter charBefore = CharacterBuilder.fromCards(pileBefore);
        int oldDefence = charBefore != null ? charBefore.getDefence() : 0;
        AffectedCharacter charAfter = CharacterBuilder.fromCards(pileAfter);
        int newDefence = charAfter != null ? charAfter.getDefence() : 0;

        return newDefence - oldDefence;
    }
}
