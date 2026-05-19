import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Deck;
import ch.aplu.jcardgame.Hand;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import thrones.game.*;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import static junit.framework.TestCase.assertEquals;

public class PlayerFactoryTest {

    private PlayerFactory factory;

    @Before
    public void setUp() {
        PlayerFactory.reset();
        factory = PlayerFactory.init(new Random(1));
    }

    @Test
    public void singletonMustBeInitialisedBeforeUse() {
        PlayerFactory.reset();

        try {
            PlayerFactory.getInstance();
            Assert.fail("Expected getInstance() to fail before init()");
        } catch (IllegalStateException expected) {
            Assert.assertTrue(expected.getMessage().contains("not been initialised"));
        }
    }

    @Test
    public void singletonReturnsSameFactoryInstance() {
        PlayerFactory sameFactory = PlayerFactory.getInstance();

        Assert.assertSame(factory, sameFactory);
        Assert.assertSame(factory, PlayerFactory.init(new Random(99)));
    }

    @Test
    public void createsCorrectPlayerTypes() {
        Player human = factory.createPlayer(0, "human");
        Player random = factory.createPlayer(1, "random");
        Player legal = factory.createPlayer(2, "legal");
        Player smart = factory.createPlayer(3, "smart");

        Assert.assertTrue(human instanceof HumanPlayer);
        Assert.assertTrue(random instanceof RandomBotPlayer);
        Assert.assertTrue(legal instanceof LegalBotPlayer);
        Assert.assertTrue(smart instanceof SmartBotPlayer);

        Assert.assertEquals(0, human.getPlayerIdentifier());
        Assert.assertEquals(1, random.getPlayerIdentifier());
        Assert.assertEquals(2, legal.getPlayerIdentifier());
        Assert.assertEquals(3, smart.getPlayerIdentifier());
    }

    @Test
    public void createsHumanPlayerWhenConfigIsMissingOrUnknown() {
        Assert.assertTrue(factory.createPlayer(0, null) instanceof HumanPlayer);
        Assert.assertTrue(factory.createPlayer(1, "") instanceof HumanPlayer);
        Assert.assertTrue(factory.createPlayer(2, "unknown") instanceof HumanPlayer);
    }

    @Test
    public void createsLegalBotWithConfiguredConsiderations() {
        Player player = factory.createPlayer(1, "legal-oa,td,tm");

        Assert.assertTrue(player instanceof LegalBotPlayer);

        LegalBotPlayer legalBot = (LegalBotPlayer) player;

        Assert.assertEquals(3, legalBot.getConsiderationCodes().size());
        Assert.assertEquals("OA", legalBot.getConsiderationCodes().get(0));
        Assert.assertEquals("TD", legalBot.getConsiderationCodes().get(1));
        Assert.assertEquals("TM", legalBot.getConsiderationCodes().get(2));
    }

    @Test
    public void legalBotConfigIgnoresSpacesAndCase() {
        Player player = factory.createPlayer(2, "  LeGaL-oa, tm, Od  ");

        Assert.assertTrue(player instanceof LegalBotPlayer);

        LegalBotPlayer legalBot = (LegalBotPlayer) player;

        Assert.assertEquals(3, legalBot.getConsiderationCodes().size());
        Assert.assertEquals("OA", legalBot.getConsiderationCodes().get(0));
        Assert.assertEquals("TM", legalBot.getConsiderationCodes().get(1));
        Assert.assertEquals("OD", legalBot.getConsiderationCodes().get(2));
    }

    @Test
    public void createsAllPlayersFromProperties() {
        Properties properties = new Properties();
        properties.setProperty("players.0", "human");
        properties.setProperty("players.1", "random");
        properties.setProperty("players.2", "legal-oa,td");
        properties.setProperty("players.3", "smart");

        List<Player> players = factory.createAllPlayers(properties, 4);

        Assert.assertEquals(4, players.size());

        Assert.assertTrue(players.get(0) instanceof HumanPlayer);
        Assert.assertTrue(players.get(1) instanceof RandomBotPlayer);
        Assert.assertTrue(players.get(2) instanceof LegalBotPlayer);
        Assert.assertTrue(players.get(3) instanceof SmartBotPlayer);

        LegalBotPlayer legalBot = (LegalBotPlayer) players.get(2);
        Assert.assertEquals(2, legalBot.getConsiderationCodes().size());
        Assert.assertEquals("OA", legalBot.getConsiderationCodes().get(0));
        Assert.assertEquals("TD", legalBot.getConsiderationCodes().get(1));
    }

    @Test
    public void createsEmptyHandsForAllPlayers() {
        Deck deck = createDeck();

        Hand[] hands = factory.createHands(deck, 4);

        Assert.assertEquals(4, hands.length);

        for (Hand hand : hands) {
            Assert.assertNotNull(hand);
            Assert.assertEquals(0, hand.getNumberOfCards());
        }
    }

    @Test
    public void dealCardsGivesEachPlayerThreeHeartsAndNineEffectCards() {
        Deck deck = createDeck();
        Hand[] hands = factory.createHands(deck, 4);

        List<List<String>> initialHearts = createFullInitialHearts();
        List<List<String>> initialEffects = createFullInitialEffects();

        factory.dealCards(
                hands,
                deck,
                4,
                true,
                initialHearts,
                initialEffects
        );

        for (int i = 0; i < 4; i++) {
            Assert.assertEquals(12, hands[i].getNumberOfCards());
            Assert.assertEquals(3, countCardsWithSuit(hands[i], Suit.HEARTS));
            Assert.assertEquals(9, countEffectCards(hands[i]));
            Assert.assertEquals(0, countCardsWithRank(hands[i], Rank.ACE));
        }
    }

    @Test
    public void dealCardsUsesAutoConfiguredCards() {
        Deck deck = createDeck();
        Hand[] hands = factory.createHands(deck, 4);

        List<List<String>> initialHearts = createFullInitialHearts();
        List<List<String>> initialEffects = createFullInitialEffects();

        factory.dealCards(
                hands,
                deck,
                4,
                true,
                initialHearts,
                initialEffects
        );

        assertHandContainsCards(hands[0],
                "2H", "3H", "4H",
                "2C", "3C", "4C", "5C", "6C", "7C", "8C", "9C", "10C"
        );

        assertHandContainsCards(hands[1],
                "5H", "6H", "7H",
                "11C", "12C", "13C", "2S", "3S", "4S", "5S", "6S", "7S"
        );

        assertHandContainsCards(hands[2],
                "8H", "9H", "10H",
                "8S", "9S", "10S", "11S", "12S", "13S", "2D", "3D", "4D"
        );

        assertHandContainsCards(hands[3],
                "11H", "12H", "13H",
                "5D", "6D", "7D", "8D", "9D", "10D", "11D", "12D", "13D"
        );
    }

    @Test
    public void testLoadAutoPlayMovementsStoresMovesInsidePlayers() {
        Properties properties = new Properties();

        properties.setProperty("players.0", "human");
        properties.setProperty("players.1", "human");
        properties.setProperty("players.2", "human");
        properties.setProperty("players.3", "human");

        properties.setProperty("plays.0.players.0.cardsPlayed", "7H-0, 6C-1");
        properties.setProperty("plays.1.players.0.cardsPlayed", "8H-1");

        PlayerFactory.reset();
        PlayerFactory factory = PlayerFactory.init(new Random(1));

        List<Player> players = factory.createAllPlayers(properties, 4);
        factory.loadAutoPlayMovements(players, properties, 2);

        Player player0 = players.get(0);

        assertEquals("7H-0", player0.getMoveData().getNextMoveString(0));
        assertEquals("6C-1", player0.getMoveData().getNextMoveString(0));

        player0.getMoveData().resetIndex();

        assertEquals("8H-1", player0.getMoveData().getNextMoveString(1));
    }

    // ------------------------------------------------------------------
    // Helper methods
    // ------------------------------------------------------------------

    private Deck createDeck() {
        return new Deck(Suit.values(), Rank.values(), "cover");
    }

    private List<List<String>> createFullInitialHearts() {
        return Arrays.asList(
                Arrays.asList("2H", "3H", "4H"),
                Arrays.asList("5H", "6H", "7H"),
                Arrays.asList("8H", "9H", "10H"),
                Arrays.asList("11H", "12H", "13H")
        );
    }

    private List<List<String>> createFullInitialEffects() {
        return Arrays.asList(
                Arrays.asList("2C", "3C", "4C", "5C", "6C", "7C", "8C", "9C", "10C"),
                Arrays.asList("11C", "12C", "13C", "2S", "3S", "4S", "5S", "6S", "7S"),
                Arrays.asList("8S", "9S", "10S", "11S", "12S", "13S", "2D", "3D", "4D"),
                Arrays.asList("5D", "6D", "7D", "8D", "9D", "10D", "11D", "12D", "13D")
        );
    }

    private void assertHandContainsCards(Hand hand, String... expectedCards) {
        for (String expectedCard : expectedCards) {
            Assert.assertTrue(
                    "Expected hand to contain " + expectedCard + ", but hand was " + canonical(hand),
                    containsCard(hand, expectedCard)
            );
        }
    }

    private boolean containsCard(Hand hand, String expectedCard) {
        int expectedRank = Integer.parseInt(expectedCard.substring(0, expectedCard.length() - 1));
        String expectedSuit = expectedCard.substring(expectedCard.length() - 1);

        for (Card card : hand.getCardList()) {
            Rank rank = (Rank) card.getRank();
            Suit suit = (Suit) card.getSuit();

            if (rank.getShortHandValue() == expectedRank
                    && suit.getSuitShortHand().equals(expectedSuit)) {
                return true;
            }
        }

        return false;
    }

    private int countCardsWithSuit(Hand hand, Suit expectedSuit) {
        int count = 0;

        for (Card card : hand.getCardList()) {
            Suit suit = (Suit) card.getSuit();
            if (suit == expectedSuit) {
                count++;
            }
        }

        return count;
    }

    private int countCardsWithRank(Hand hand, Rank expectedRank) {
        int count = 0;

        for (Card card : hand.getCardList()) {
            Rank rank = (Rank) card.getRank();
            if (rank == expectedRank) {
                count++;
            }
        }

        return count;
    }

    private int countEffectCards(Hand hand) {
        int count = 0;

        for (Card card : hand.getCardList()) {
            Suit suit = (Suit) card.getSuit();
            if (!suit.isCharacter()) {
                count++;
            }
        }

        return count;
    }

    private String canonical(Hand hand) {
        StringBuilder builder = new StringBuilder();

        for (Card card : hand.getCardList()) {
            Rank rank = (Rank) card.getRank();
            Suit suit = (Suit) card.getSuit();

            if (builder.length() > 0) {
                builder.append(",");
            }

            builder.append(rank.getShortHandValue());
            builder.append(suit.getSuitShortHand());
        }

        return builder.toString();
    }
}