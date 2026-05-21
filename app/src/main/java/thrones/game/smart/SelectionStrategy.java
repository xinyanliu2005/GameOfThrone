package thrones.game.smart;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import thrones.game.AffectedCharacter;
import thrones.game.CharacterBuilder;
import thrones.game.PileInformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface SelectionStrategy {
    Optional<BotMove> selectMove(Hand hand, PileInformation board, int playerIdentifier);

    static int attackDelta(Card card, PileInformation board, int pileIndex) {
        List<Card> pileBefore = board.getPileCards(pileIndex);
        List<Card> pileAfter = new ArrayList<>(pileBefore);
        pileAfter.add(card);

        int oldAttack = CharacterBuilder.fromCards(pileBefore).map(AffectedCharacter::getAttack).orElse(0);
        int newAttack = CharacterBuilder.fromCards(pileAfter).map(AffectedCharacter::getAttack).orElse(0);

        return newAttack - oldAttack;
    }

    static int defenceDelta(Card card, PileInformation board, int pileIndex) {
        List<Card> pileBefore = board.getPileCards(pileIndex);
        List<Card> pileAfter = new ArrayList<>(pileBefore);
        pileAfter.add(card);

        int oldDefence = CharacterBuilder.fromCards(pileBefore).map(AffectedCharacter::getDefence).orElse(0);
        int newDefence = CharacterBuilder.fromCards(pileAfter).map(AffectedCharacter::getDefence).orElse(0);

        return newDefence - oldDefence;
    }
}
