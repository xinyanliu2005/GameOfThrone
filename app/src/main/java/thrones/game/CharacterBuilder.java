package thrones.game;

import ch.aplu.jcardgame.Card;

import java.util.List;
import java.util.Optional;

// Builder design pattern for building and adding effects to a Character
public final class CharacterBuilder {
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

    public static Optional<AffectedCharacter> fromCards(List<Card> cards) {
        if (cards == null || cards.isEmpty()) {
            return Optional.empty();
        }

        AffectedCharacter character = null;
        for (Card card : cards) {
            character = addCard(character, card);
        }
        return Optional.of(character);
    }
}
