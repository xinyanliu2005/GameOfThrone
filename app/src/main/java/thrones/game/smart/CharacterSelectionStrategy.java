package thrones.game.smart;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import thrones.game.BotMove;
import thrones.game.PileInformation;
import thrones.game.Rank;
import thrones.game.Suit;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CharacterSelectionStrategy implements SelectionStrategy {
    Random random;

    public CharacterSelectionStrategy() {
        this.random = new Random();
    }

    public Optional<BotMove> selectMove(Hand hand, PileInformation board, int playerIdentifier) {
        // Group cards by their rank
        Map<Rank, List<Card>> cardsGroupedByRank = hand.getCardList()
                .stream()
                .collect(Collectors.groupingBy(
                    card -> (Rank) card.getRank(),
                    () -> new EnumMap<>(Rank.class),
                    Collectors.toList()
                ));

        // predicate to check the group contains a heart card
        Predicate<List<Card>> containsHeart = group -> group.stream().anyMatch(card -> card.getSuit() == Suit.HEARTS);

        // size of the largest group containing a heart card
        int maxSize = cardsGroupedByRank.values()
                .stream()
                .filter(containsHeart)
                .mapToInt(List::size)
                .max()
                .orElse(0);

        // all the groups that have a heart card and are the size of the largest group
        List<Rank> largestGroups = cardsGroupedByRank.entrySet()
                .stream()
                .filter(entry -> containsHeart.test(entry.getValue()))
                .filter(entry -> entry.getValue().size() == maxSize)
                .map(Map.Entry::getKey)
                .toList();

        int highestScore = largestGroups
                .stream()
                .mapToInt(Rank::getScoreValue)
                .max()
                .orElse(0);

        // find the group with the highest rank and all heart cards in that group
        List<Card> winners = largestGroups
                .stream()
                .flatMap(rank -> cardsGroupedByRank.get(rank).stream())
                .filter(card -> card.getSuit() == Suit.HEARTS)
                .filter(card -> ((Rank) card.getRank()).getScoreValue() == highestScore)
                .toList();

        // realistically winners shouldn't be empty if being called in the correct place...
        // player should always have a heart/character card...
        if (winners.isEmpty()) return Optional.empty();

        // choose random character card from remaining...could be more than one, as is based on their score value...
        // e.g. [10H, KH]
        Card chosenCharacter = winners.get(this.random.nextInt(winners.size()));

        int ownPileIndex = playerIdentifier % 2;
        return Optional.of(new BotMove(chosenCharacter, ownPileIndex));
    }
}
