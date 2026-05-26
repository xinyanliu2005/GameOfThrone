package thrones.game.effectCard;

import ch.aplu.jcardgame.Card;
import thrones.game.Rank;
import thrones.game.Suit;

import java.util.List;

/**
 * Builder for constructing the AffectedCharacter decorator chain from a list (i.e. pile) of cards in play order
 */
public final class CharacterBuilder {

    /**
     * Add a card to the AffectedCharacter
     * @param character AffectedCharacter to which to add the provided card
     * @param card Card to be added to the AffectedCharacter
     * @return the updated AffectedCharacter
     */
    public static AffectedCharacter addCard(AffectedCharacter character, Card card) {
        Suit suit = (Suit)card.getSuit();
        Rank rank = (Rank)card.getRank();

        return switch (suit) {
            case Suit.HEARTS -> new HeartCharacter(rank);
            case Suit.CLUBS -> new ClubEffect(character, rank);
            case Suit.SPADES -> new SpadeEffect(character, rank);
            case Suit.DIAMONDS -> new DiamondEffect(character, rank);
        };
    }

    /**
     * Given an ordered list (i.e. pile) of cards, create an AffectedCharacter from them
     * @param cards list of cards in order of which they should be applied in building a character (Hearts first)
     * @return return an Affected Character, or null if no character is built
     */
    public static AffectedCharacter fromCards(List<Card> cards) {
        if (cards == null || cards.isEmpty()) {
            return null;
        }

        AffectedCharacter character = null;
        for (Card card : cards) {
            character = addCard(character, card);
        }
        return character;
    }
}
