package thrones.game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Deck;
import ch.aplu.jcardgame.Hand;

import java.util.*;

/**
 * Singleton factory responsible for creating players, creating hands, dealing
 * cards, and loading configured autoplay movements into Player objects. This class centralises
 * object creation logic that was previously spread through GameOfThrones. GameOfThrones only
 * needs to request players and hands from this factory, rather than knowing how each concrete
 * Player subclass is constructed or how cards are dealt
 */
public class PlayerFactory {

    private static PlayerFactory instance;
    private Random random;

    // Constants for dealing
    private static final int NB_HEART_CARDS_PER_PLAYER = 3;
    private static final int NB_EFFECT_CARDS_PER_PLAYER = 9;

    private PlayerFactory(Random random) {
        this.random = random;
    }

    /**
     * Initialise the singleton with a shared Random instance.
     * Must be called once before getInstance().
     * @param random the Random instance shared by the game
     * @return the single player factory instance
     */
    public static synchronized PlayerFactory init(Random random) {
        if (instance == null) {
            instance = new PlayerFactory(random);
        }
        return instance;
    }

    /**
     * Get the singleton instance. Throws if init() hasn't been called.
     * @return the initialised player factory instance
     */
    public static synchronized PlayerFactory getInstance() {
        if (instance == null) {
            throw new IllegalStateException(
                    "PlayerFactory has not been initialised. Call init(Random) first.");
        }
        return instance;
    }

    /**
     * Reset the singleton
     */
    public static synchronized void reset() {
        instance = null;
    }

    /**
     * Creates a Player from the properties configuration string
     * @param playerIndex the player index
     * @param configString the raw string from properties
     * @return a fully configured Player instance
     */
    public Player createPlayer(int playerIndex, String configString) {
        if (configString == null || configString.trim().isEmpty()) {
            return new HumanPlayer(playerIndex);
        }

        String trimmed = configString.trim().toLowerCase();

        // separate player type from optional config
        String playerTypeStr;
        String configPart = null;
        int dashIndex = trimmed.indexOf('-');
        if (dashIndex >= 0) {
            playerTypeStr = trimmed.substring(0, dashIndex);
            configPart = trimmed.substring(dashIndex + 1);
        } else {
            playerTypeStr = trimmed;
        }

        switch (playerTypeStr) {
            case "human":
                return new HumanPlayer(playerIndex);
            case "random":
                return new RandomBotPlayer(playerIndex, random);
            case "legal":
                return createLegalBot(playerIndex, configPart);
            case "smart":
                return new SmartBotPlayer(playerIndex);
            default:
                System.err.println("Unknown player type: " + playerTypeStr
                        + ". Defaulting to human.");
                return new HumanPlayer(playerIndex);
        }
    }

    /**
     * Creates all four players from a Properties object
     * @param properties the game configuration properties
     * @param nbPlayers number of players to create
     * @return a list containing all created player objects
     */
    public List<Player> createAllPlayers(Properties properties, int nbPlayers) {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < nbPlayers; i++) {
            String configString = properties.getProperty("players." + i);
            players.add(createPlayer(i, configString));
        }
        return players;
    }

    /**
     * Loads autoplay movement data from properties into each Player MoveData.
     *
     * Reads "plays.{playIndex}.players.{playerIndex}.cardsPlayed" for each play
     * and player, parsing the comma-separated move strings (e.g. "7H-0,6C-0,7C-0")
     * and storing them via Player.setAutoMoves().
     *
     * This should be called after createAllPlayers() when isAuto is true,
     * so that the autoplay information lives with each Player rather than
     * in GameOfThrones global lists.
     *
     * @param players the list of Player objects to populate
     * @param properties the game properties containing cardsPlayed entries
     * @param nbPlays the number of plays (rounds) in the game
     */
    public void loadAutoPlayMovements(List<Player> players, Properties properties, int nbPlays) {
        for (Player player : players) {
            // Each player owns its own auto-play movement data, so look up moves
            // using that player's identifier rather than storing all moves globally
            int playerIndex = player.getPlayerIdentifier();

            for (int playIndex = 0; playIndex < nbPlays; playIndex++) {
                String key = "plays." + playIndex + ".players." + playerIndex + ".cardsPlayed";
                String value = properties.getProperty(key);
                if (value != null && !value.trim().isEmpty()) {
                    List<String> moves = Arrays.asList(value.split(","));
                    player.setAutoMoves(playIndex, moves);
                }
            }
        }
    }


    /**
     * Creates a LegalBotPlayer and parses its optional consideration codes.
     *
     * @param playerIndex the player identifier
     * @param considerationsStr comma-separated legal bot consideration codes, or null
     * @return a configured LegalBotPlayer
     */
    private LegalBotPlayer createLegalBot(int playerIndex, String considerationsStr) {
        List<String> considerationCodes = new ArrayList<>();
        if (considerationsStr != null && !considerationsStr.isEmpty()) {
            String[] parts = considerationsStr.split(",");
            for (String part : parts) {
                String code = part.trim().toUpperCase();
                if (!code.isEmpty()) {
                    considerationCodes.add(code);
                }
            }
        }
        return new LegalBotPlayer(playerIndex, considerationCodes);
    }


    /**
     * Creates empty Hand objects for each player using the given deck
     *
     * @param deck the game Deck to create hands from
     * @param nbPlayers number of players
     * @return array of empty Hand objects, one per player
     */
    public Hand[] createHands(Deck deck, int nbPlayers) {
        Hand[] hands = new Hand[nbPlayers];
        for (int i = 0; i < nbPlayers; i++) {
            hands[i] = new Hand(deck);
        }
        return hands;
    }

    /**
     * Deals cards from the deck into the player hands, respecting auto-mode
     * card assignments from the properties file.
     *
     * This method handles the full dealing sequence:
     * 1. Creates a pack from the deck and removes aces
     * 2. Deals 3 heart cards to each player (auto-assigned or random)
     * 3. Deals 9 effect cards to each player (auto-assigned or random)
     * 4. Sorts each hand
     *
     * @param hands the Hand array to deal into
     * @param deck the game Deck
     * @param nbPlayers number of players
     * @param isAuto whether auto mode is enabled
     * @param initialHeartStrings heart card strings for this play, per player
     * @param initialCardStrings effect card strings for this play, per player
     */
    public void dealCards(Hand[] hands, Deck deck, int nbPlayers,
                          boolean isAuto,
                          List<List<String>> initialHeartStrings,
                          List<List<String>> initialCardStrings) {

        Hand pack = deck.toHand(false);
        assert pack.getNumberOfCards() == 52 : " Starting pack is not 52 cards.";

        // Remove 4 Aces
        List<Card> aceCards = pack.getCardsWithRank(Rank.ACE);
        for (Card card : aceCards) {
            card.removeFromHand(false);
        }
        assert pack.getNumberOfCards() == 48 : " Pack without aces is not 48 cards.";

        // Deal 3 heart cards to each player
        dealHeartCards(hands, pack, nbPlayers, isAuto, initialHeartStrings);
        assert pack.getNumberOfCards() == 36 : " Pack without aces and hearts is not 36 cards.";

        // Deal 9 effect cards to each player
        dealEffectCards(hands, pack, nbPlayers, isAuto, initialCardStrings);

        // Sort each hand
        for (int j = 0; j < nbPlayers; j++) {
            sortHand(hands[j]);
            assert hands[j].getNumberOfCards() == 12 : " Hand does not have twelve cards.";
        }
    }

    /**
     * Deals the required heart character cards to each player.
     *
     * @param hands the Hand array to deal into
     * @param nbPlayers number of players
     * @param pack
     * @param isAuto whether auto mode is enabled
     * @param initialHeartStrings heart card strings for this play
     */
    private void dealHeartCards(Hand[] hands, Hand pack, int nbPlayers,
                                boolean isAuto, List<List<String>> initialHeartStrings) {
        List<Card> heartCards = new ArrayList<>(pack.getCardsWithSuit(Suit.HEARTS));
        for (int i = 0; i < nbPlayers; i++) {
            int remaining = NB_HEART_CARDS_PER_PLAYER;

            if (isAuto && initialHeartStrings != null
                    && i < initialHeartStrings.size()
                    && !initialHeartStrings.get(i).isEmpty()) {
                for (String heartString : initialHeartStrings.get(i)) {
                    Card card = getCardFromList(heartCards, heartString);
                    assert card != null;
                    card.removeFromHand(false);
                    hands[i].insert(card, false);
                    heartCards.remove(card);
                    remaining--;
                }
            }

            for (int j = 0; j < remaining; j++) {
                int x = random.nextInt(heartCards.size());
                Card randomCard = heartCards.get(x);
                randomCard.removeFromHand(false);
                hands[i].insert(randomCard, false);
                heartCards.remove(randomCard);
            }
        }
    }

    /**
     * Deals the required non-heart effect cards to each player.
     *
     * @param hands the Hand array to deal into
     * @param pack
     * @param nbPlayers the number of players
     * @param isAuto whether auto mode is on
     * @param initialCardStrings effect card string for this play
     */
    private void dealEffectCards(Hand[] hands, Hand pack, int nbPlayers,
                                 boolean isAuto, List<List<String>> initialCardStrings) {
        for (int i = 0; i < nbPlayers; i++) {
            int remaining = NB_EFFECT_CARDS_PER_PLAYER;

            if (isAuto && initialCardStrings != null
                    && i < initialCardStrings.size()
                    && !initialCardStrings.get(i).isEmpty()) {
                for (String effectString : initialCardStrings.get(i)) {
                    Card card = getCardFromList(pack.getCardList(), effectString);
                    assert card != null;
                    card.removeFromHand(false);
                    hands[i].insert(card, false);
                    remaining--;
                }
            }

            for (int j = 0; j < remaining; j++) {
                assert !pack.isEmpty() : " Pack has prematurely run out of cards.";
                int x = random.nextInt(pack.getNumberOfCards());
                Card dealt = pack.get(x);
                dealt.removeFromHand(false);
                hands[i].insert(dealt, false);
            }
        }
    }

    /**
     * Converts a card string into its Rank value.
     *
     * @param cardName the compact card string
     * @return the matching Rank
     */
    private Rank getRankFromString(String cardName) {
        String rankString = cardName.substring(0, cardName.length() - 1);
        int rankValue = Integer.parseInt(rankString);
        for (Rank rank : Rank.values()) {
            if (rank.getShortHandValue() == rankValue) {
                return rank;
            }
        }
        return Rank.ACE;
    }

    /**
     * Converts a card string into its Suit value.
     *
     * @param cardName the compact card string
     * @return the matching Suit
     */
    private Suit getSuitFromString(String cardName) {
        String suitString = cardName.substring(cardName.length() - 1);
        for (Suit suit : Suit.values()) {
            if (suit.getSuitShortHand().equals(suitString)) {
                return suit;
            }
        }
        return Suit.CLUBS;
    }

    /**
     * Finds the Card object in a list that matches a compact card string.
     *
     * @param cards the list of cards to search
     * @param cardName the compact card string
     * @return the matching Card, or null if no match is found
     */
    private Card getCardFromList(List<Card> cards, String cardName) {
        Rank existingRank = getRankFromString(cardName);
        Suit existingSuit = getSuitFromString(cardName);
        for (Card card : cards) {
            Suit suit = (Suit) card.getSuit();
            Rank rank = (Rank) card.getRank();
            if (suit.getSuitShortHand().equals(existingSuit.getSuitShortHand())
                    && rank.getShortHandValue() == existingRank.getShortHandValue()) {
                return card;
            }
        }
        return null;
    }

    /**
     * Sorts a hand by suit first, then by rank value.
     *
     * @param hand the hand to sort
     */
    private void sortHand(Hand hand) {
        List<Card> cards = hand.getCardList();
        cards.sort((o1, o2) -> {
            Suit suit1 = (Suit) o1.getSuit();
            Suit suit2 = (Suit) o2.getSuit();
            Rank rank1 = (Rank) o1.getRank();
            Rank rank2 = (Rank) o2.getRank();
            if (suit1.ordinal() != suit2.ordinal()) {
                return suit1.ordinal() - suit2.ordinal();
            }
            return rank1.getShortHandValue() - rank2.getShortHandValue();
        });
    }
}