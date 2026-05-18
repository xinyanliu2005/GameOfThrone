package thrones.game;

// Oh_Heaven.java

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;
import thrones.game.utility.Logger;

import java.awt.Color;
import java.awt.Font;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("serial")
public class GameOfThrones extends CardGame {

    /*
    Canonical String representations of Suit, Rank, Card, and Hand
    */
    String canonical(Suit s) { return s.toString().substring(0, 1); }

    String canonical(Rank r) {
        switch (r) {
            case ACE: case KING: case QUEEN: case JACK: case TEN:
                return r.toString().substring(0, 1);
            default:
                return String.valueOf(r.getShortHandValue());
        }
    }

    String canonical(Card c) { return canonical((Rank) c.getRank()) + canonical((Suit) c.getSuit()); }

    String canonical(Hand h) {
        return "[" + h.getCardList().stream().map(this::canonical).collect(Collectors.joining(",")) + "]";
    }

    static Random random = new Random(30006);

    private boolean isAuto = false;
    public final int nbPlayers = 4;
    // Dealing constants moved to PlayerFactory
    public final int nbPlays = 2;
    public final int nbRounds = 3;
    private Deck deck = new Deck(Suit.values(), Rank.values(), "cover");
    private final Location[] handLocations = {
            new Location(350, 625),
            new Location(75, 350),
            new Location(350, 75),
            new Location(625, 350)
    };

    private final Location[] scoreLocations = {
            new Location(575, 675),
            new Location(25, 575),
            new Location(25, 25),
            new Location(575, 125)
    };
    private final Location[] pileLocations = {
            new Location(350, 280),
            new Location(350, 430)
    };
    private final Location[] pileStatusLocations = {
            new Location(300, 200),
            new Location(300, 520)
    };

    // ====================================================================
    // Players created via PlayerFactory (Singleton + Factory Method pattern)
    // Replaces the old PlayerType enum and manual if/else instantiation.
    // ====================================================================
    private List<Player> players;

    private final Actor[] pileTextActors = { null, null };
    private final Actor[] scoreActors = {null, null, null, null};
    private final int watchingTime = 5000;
    private Hand[] hands;
    private Hand[] piles;

    private int nextStartingPlayer = random.nextInt(nbPlayers);

    private int[] scores = new int[nbPlayers];
    private Logger logger = new Logger();

    Font bigFont = new Font("Arial", Font.BOLD, 36);
    Font smallFont = new Font("Arial", Font.PLAIN, 10);
    private int currentPlay = 0;
    private List<Integer> firstPlayers = new ArrayList<>();
    private int NUMBER_OF_PLAYS = 2;
    private List<List<List<String>>> playerAutoMovements = new ArrayList<>();
    private List<Integer> playerMovementIndexes = new ArrayList<>();
    private List<List<List<String>>> initialCardStrings = new ArrayList<>();
    private List<List<List<String>>> initialHeartStrings = new ArrayList<>();

    private List<String> cardListFromKey(Properties properties, String key) {
        String initialCardString = properties.getProperty(key);
        if (initialCardString != null && !initialCardString.isEmpty()) {
            return Arrays.asList(initialCardString.split(","));
        }
        return null;
    }

    /**
     * Initialise object
     */
    private void initWithProperties(Properties properties) {
        isAuto = Boolean.parseBoolean(properties.getProperty("isAuto"));

        // ---------------------------------------------------------------
        // Use PlayerFactory (Singleton) to create all Player instances.
        // The factory parses config strings like "human", "random",
        // "legal-oa,td,tm", "smart" and returns the correct subclass.
        // ---------------------------------------------------------------
        PlayerFactory.reset();  // Reset in case of multiple games (testing)
        PlayerFactory factory = PlayerFactory.init(random);
        players = factory.createAllPlayers(properties, nbPlayers);

        for (int i = 0; i < nbPlayers; i++) {
            playerMovementIndexes.add(0);
        }

        for (int i = 0; i < NUMBER_OF_PLAYS; i++) {
            String firstPlayerString = properties.getProperty("plays." + i + ".firstPlayer");
            if (firstPlayerString != null && !firstPlayerString.isEmpty()) {
                firstPlayers.add(Integer.parseInt(firstPlayerString));
            } else {
                firstPlayers.add(-1);
            }
        }

        for (int i = 0; i < NUMBER_OF_PLAYS; i++) {
            initialCardStrings.add(new ArrayList<>());
            playerAutoMovements.add(new ArrayList<>());
            initialHeartStrings.add(new ArrayList<>());
            for (int j = 0; j < nbPlayers; j++) {
                String initialCardKey = "plays." + i + ".players." + j + ".initialCards";
                List<String> initialStrings = cardListFromKey(properties, initialCardKey);
                initialCardStrings.get(i).add(new ArrayList<>());
                if (initialStrings != null) {
                    initialCardStrings.get(i).get(j).addAll(initialStrings);
                }

                String movementCardKey = "plays." + i + ".players." + j + ".cardsPlayed";
                List<String> movementStrings = cardListFromKey(properties, movementCardKey);
                playerAutoMovements.get(i).add(new ArrayList<>());
                if (movementStrings != null) {
                    playerAutoMovements.get(i).get(j).addAll(movementStrings);
                }

                String heartCardKey = "plays." + i + ".players." + j + ".initialHearts";
                List<String> heartStrings = cardListFromKey(properties, heartCardKey);
                initialHeartStrings.get(i).add(new ArrayList<>());
                if (heartStrings != null) {
                    initialHeartStrings.get(i).get(j).addAll(heartStrings);
                }
            }
        }
    }

    public GameOfThrones(Properties properties) {
        super(700, 700, 30);
        initWithProperties(properties);
        setSimulationPeriod(100);
        int version = 1;
        setTitle("Game of Thrones (V" + version + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");
        setStatusText("Initializing...");
        initScore();

        setupGame();
    }

    public String runApp() {
        while(currentPlay < nbPlays) {
            executeAPlay();
            updateScores();

        }

        String text;
        if (scores[0] > scores[1]) {
            text = "Players 0 and 2 won.";
        } else if (scores[0] == scores[1]) {
            text = "All players drew.";
        } else {
            text = "Players 1 and 3 won.";
        }
        System.out.println("Result: " + text);
        setStatusText(text);

        refresh();
        return logger.getAllLog();
    }

    // Hand sorting is now handled by PlayerFactory.dealCards()

    // Random card selection is now handled by PlayerFactory


    private Rank getRankFromString(String cardName) {
        String rankString = cardName.substring(0, cardName.length() - 1);
        Integer rankValue = Integer.parseInt(rankString);

        for (Rank rank : Rank.values()) {
            if (rank.getShortHandValue() == rankValue) {
                return rank;
            }
        }

        return Rank.ACE;
    }

    private Suit getSuitFromString(String cardName) {
        String suitString = cardName.substring(cardName.length() - 1, cardName.length());

        for (Suit suit : Suit.values()) {
            if (suit.getSuitShortHand().equals(suitString)) {
                return suit;
            }
        }
        return Suit.CLUBS;
    }

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

    // Card dealing is now handled by PlayerFactory.dealCards()
    // Card lookup utilities remain here for auto-mode play logic

    private void initScore() {
        for (int i = 0; i < nbPlayers; i++) {
            scores[i] = 0;
            String text = "P" + i + "-0";
            scoreActors[i] = new TextActor(text, Color.WHITE, bgColor, bigFont);
            addActor(scoreActors[i], scoreLocations[i]);
        }

        String text = "Attack: 0 - Defence: 0";
        for (int i = 0; i < pileTextActors.length; i++) {
            pileTextActors[i] = new TextActor(text, Color.WHITE, bgColor, smallFont);
            addActor(pileTextActors[i], pileStatusLocations[i]);
        }
    }

    private void updateScore(int player) {
        removeActor(scoreActors[player]);
        String text = "P" + player + "-" + scores[player];
        scoreActors[player] = new TextActor(text, Color.WHITE, bgColor, bigFont);
        addActor(scoreActors[player], scoreLocations[player]);
    }

    private void updateScores() {
        for (int i = 0; i < nbPlayers; i++) {
            updateScore(i);
        }
    }

    private Optional<Card> selected;
    private final int NON_SELECTION_VALUE = -1;
    private int selectedPileIndex = NON_SELECTION_VALUE;
    private final int UNDEFINED_INDEX = -1;
    public static final int ATTACK_RANK_INDEX = 0;
    public static final int DEFENCE_RANK_INDEX = 1;
    private void setupGame() {
        // Use PlayerFactory (Singleton) to create hands and deal cards
        PlayerFactory factory = PlayerFactory.getInstance();
        hands = factory.createHands(deck, nbPlayers);
        factory.dealCards(hands, deck, nbPlayers, isAuto,
                initialHeartStrings.get(currentPlay),
                initialCardStrings.get(currentPlay));

        // Log player cards after dealing
        for (int j = 0; j < nbPlayers; j++) {
            logger.logPlayerCards(hands[j], j);
        }

        // Assign dealt hands to Player objects so they can use them polymorphically
        for (int i = 0; i < nbPlayers; i++) {
            players.get(i).assignInitialHand(hands[i]);
        }

        for (int i = 0; i < nbPlayers; i++) {
            hands[i].sort(Hand.SortType.SUITPRIORITY, true);
            System.out.println("hands[" + i + "]: " + canonical(hands[i]));
        }

        for (final Hand currentHand : hands) {
            // Set up human player for interaction
            currentHand.addCardListener(new CardAdapter() {
                public void leftDoubleClicked(Card card) {
                    selected = Optional.of(card);
                    currentHand.setTouchEnabled(false);
                }
                public void rightClicked(Card card) {
                    selected = Optional.empty(); // Don't care which card we right-clicked for player to pass
                    currentHand.setTouchEnabled(false);
                }
            });
        }
        // graphics
        RowLayout[] layouts = new RowLayout[nbPlayers];
        for (int i = 0; i < nbPlayers; i++) {
            int handWidth = 400;
            layouts[i] = new RowLayout(handLocations[i], handWidth);
            layouts[i].setRotationAngle(90 * i);
            hands[i].setView(this, layouts[i]);
            hands[i].draw();
        }
        // End graphics
    }

    private void resetIndexes() {
        for (int i = 0; i < playerMovementIndexes.size(); i++) {
            playerMovementIndexes.set(i, 0);
        }
    }

    private void resetPile() {
        if (piles != null) {
            for (Hand pile : piles) {
                pile.removeAll(true);
            }
        }
        piles = new Hand[2];
        for (int i = 0; i < 2; i++) {
            piles[i] = new Hand(deck);
            int pileWidth = 40;
            piles[i].setView(this, new RowLayout(pileLocations[i], 8 * pileWidth));
            piles[i].draw();
            final Hand currentPile = piles[i];
            final int pileIndex = i;
            piles[i].addCardListener(new CardAdapter() {
                public void leftClicked(Card card) {
                    selectedPileIndex = pileIndex;
                    currentPile.setTouchEnabled(false);
                }
            });
        }

        updatePileRanks();
    }

    // pickACorrectSuit and selectRandomPile removed — replaced by
    // Player.selectCardToPlay() and Player.choosePileToPlayOn() polymorphic dispatch

    private void waitForCorrectSuit(int playerIndex, boolean isCharacter) {
        if (hands[playerIndex].isEmpty()) {
            selected = Optional.empty();
        } else {
            selected = null;
            hands[playerIndex].setTouchEnabled(true);
            do {
                if (selected == null) {
                    delay(100);
                    continue;
                }
                Suit suit = selected.isPresent() ? (Suit) selected.get().getSuit() : null;
                if (isCharacter && suit != null && suit.isCharacter() ||         // If we want character, can't pass and suit must be right
                        !isCharacter && (suit == null || !suit.isCharacter())) { // If we don't want character, can pass or suit must not be character
                    break;
                } else {
                    selected = null;
                    hands[playerIndex].setTouchEnabled(true);
                }
                delay(100);
            } while (true);
        }
    }

    private void waitForPileSelection() {
        selectedPileIndex = NON_SELECTION_VALUE;
        for (Hand pile : piles) {
            pile.setTouchEnabled(true);
        }
        while(selectedPileIndex == NON_SELECTION_VALUE) {
            delay(100);
        }
        for (Hand pile : piles) {
            pile.setTouchEnabled(false);
        }
    }

    private int[] calculatePileRanks(int pileIndex) {
        Hand currentPile = piles[pileIndex];
        int i = currentPile.isEmpty() ? 0 : ((Rank) currentPile.get(0).getRank()).getShortHandValue();
        return new int[] { i, i };
    }

    private void updatePileRankState(int pileIndex, int attackRank, int defenceRank) {
        TextActor currentPile = (TextActor) pileTextActors[pileIndex];
        removeActor(currentPile);
        String text = "Attack: " + attackRank + " - Defence: " + defenceRank;
        pileTextActors[pileIndex] = new TextActor(text, Color.WHITE, bgColor, smallFont);
        addActor(pileTextActors[pileIndex], pileStatusLocations[pileIndex]);
    }

    private void updatePileRanks() {
        for (int j = 0; j < piles.length; j++) {
            int[] ranks = calculatePileRanks(j);
            updatePileRankState(j, ranks[ATTACK_RANK_INDEX], ranks[DEFENCE_RANK_INDEX]);
        }
    }

    private int getPlayerIndex(int index) {
        return index % nbPlayers;
    }

    /**
     * Helper: checks if the player at playerIndex is a human.
     * Uses the Player objects created by PlayerFactory instead of the old PlayerType enum.
     */
    private boolean isHumanPlayer(int playerIndex) {
        return players.get(playerIndex) instanceof HumanPlayer;
    }

    private void playHeartForCharacters() {
        // 1: play the first 2 hearts
        nextStartingPlayer = -1;
        if (isAuto) {
            if (firstPlayers.size() >= currentPlay) {
                nextStartingPlayer = firstPlayers.get(currentPlay);
            }
        }

        if (nextStartingPlayer < 0) {
            nextStartingPlayer = getPlayerIndex(nextStartingPlayer);
            if (hands[nextStartingPlayer].getNumberOfCardsWithSuit(Suit.HEARTS) == 0)
                nextStartingPlayer = getPlayerIndex(nextStartingPlayer + 1);
        }

        assert hands[nextStartingPlayer].getNumberOfCardsWithSuit(Suit.HEARTS) != 0 : " Starting player has no hearts.";

        for (int i = 0; i < 2; i++) {
            int pileIndex = 0;
            selected = Optional.empty();

            int playerIndex = getPlayerIndex(nextStartingPlayer + i);
            setStatusText("Player " + playerIndex + " select a Heart card to play");
            if (isAuto) {
                if (playerAutoMovements.size() > currentPlay) {
                    List<List<String>>playersCards = playerAutoMovements.get(currentPlay);
                    if (playersCards.size() > playerIndex) {
                        List<String> movementStrings =  playersCards.get(playerIndex);
                        Hand currentHand = hands[playerIndex];
                        int playerMovementIndex = playerMovementIndexes.get(playerIndex);
                        if (movementStrings.size() > playerMovementIndex) {
                            String movementString = movementStrings.get(playerMovementIndex);
                            String[] components = movementString.split("-");
                            String cardString = components[0];
                            pileIndex = Integer.parseInt(components[1]);
                            selected = Optional.ofNullable(getCardFromList(currentHand.getCardList(), cardString));
                            playerMovementIndexes.set(playerIndex, playerMovementIndex + 1);
                        }
                    }
                }
            }

            if (selected.isEmpty()) {
                pileIndex = i % 2;
                // Polymorphic dispatch: each Player subclass implements its own card selection
                Player currentPlayer = players.get(playerIndex);
                if (isHumanPlayer(playerIndex)) {
                    waitForCorrectSuit(playerIndex, true);
                } else {
                    selected = currentPlayer.selectCardToPlay(null, true);
                }
            }

            assert selected.isPresent() : " Pass returned on selection of character.";
            selected.get().setVerso(false);
            selected.get().transfer(piles[pileIndex], true); // transfer to pile (includes graphic effect)
            logger.logPlayerMovement(nextStartingPlayer + i, selected.get(), pileIndex);
            updatePileRanks();
        }
    }

    private void playTurns() {
        // 2: play the remaining nbPlayers * nbRounds - 2
        int remainingTurns = nbPlayers * nbRounds - 2;
        int nextPlayer = (nextStartingPlayer + 2) % nbPlayers;

        while(remainingTurns > 0) {
            boolean hasSelectedCard = false;
            if (isAuto) {
                List<List<String>>playersCards = playerAutoMovements.get(currentPlay);
                if (playersCards.size() > nextPlayer) {
                    List<String> movementStrings =  playersCards.get(nextPlayer);
                    Hand currentHand = hands[nextPlayer];
                    int playerMovementIndex = playerMovementIndexes.get(nextPlayer);
                    if (movementStrings.size() > playerMovementIndex) {
                        String movementString = movementStrings.get(playerMovementIndex);
                        String[] components = movementString.split("-");
                        String cardString = components[0];
                        selectedPileIndex = Integer.parseInt(components[1]);
                        selected = Optional.ofNullable(getCardFromList(currentHand.getCardList(), cardString));
                        setStatusText("Selected: " + canonical(selected.get()) + ". Player" + nextPlayer +
                                " select a pile " + selectedPileIndex + " to play the card.");
                        playerMovementIndexes.set(nextPlayer, playerMovementIndex + 1);
                        hasSelectedCard = true;
                    }
                }
            }

            if (!hasSelectedCard || selected.isEmpty()) {
                nextPlayer = getPlayerIndex(nextPlayer);
                setStatusText("Player" + nextPlayer + " select a non-Heart card to play.");
                // Polymorphic dispatch: each Player subclass implements its own card selection
                Player currentPlayer = players.get(nextPlayer);
                if (isHumanPlayer(nextPlayer)) {
                    waitForCorrectSuit(nextPlayer, false);
                } else {
                    selected = currentPlayer.selectCardToPlay(null, false);
                }

                if (selected.isPresent()) {
                    setStatusText("Selected: " + canonical(selected.get()) + ". Player" + nextPlayer + " select a pile to play the card.");
                    if (isHumanPlayer(nextPlayer)) {
                        waitForPileSelection();
                    } else {
                        selectedPileIndex = currentPlayer.choosePileToPlayOn();
                    }
                } else {
                    System.out.println(". Player" + nextPlayer + "Pass.");
                    setStatusText("Pass.");
                }
            }

            if (selected != null && selected.isPresent()) {
                selected.get().setVerso(false);
                selected.get().transfer(piles[selectedPileIndex], true); // transfer to pile (includes graphic effect)
                updatePileRanks();
                logger.logPlayerMovement(nextPlayer, selected.get(), selectedPileIndex);
            }

            nextPlayer = (nextPlayer + 1) % nbPlayers;
            remainingTurns--;
        }
    }

    private void updateScoreForPlayers(int[] pileNorthRanks, int[] pileSouthRanks) {
        System.out.println("pile north: " + canonical(piles[Pile.NORTH.ordinal()]));
        System.out.println("pile north is " + "Attack: " + pileNorthRanks[ATTACK_RANK_INDEX] + " - Defence: " + pileNorthRanks[DEFENCE_RANK_INDEX]);
        System.out.println("pile south: " + canonical(piles[Pile.SOUTH.ordinal()]));
        System.out.println("pile south is " + "Attack: " + pileSouthRanks[ATTACK_RANK_INDEX] + " - Defence: " + pileSouthRanks[DEFENCE_RANK_INDEX]);
        Rank pileNorthCharacterRank = (Rank) piles[Pile.NORTH.ordinal()].getCardList().get(0).getRank();
        Rank pileSouthCharacterRank = (Rank) piles[Pile.SOUTH.ordinal()].getCardList().get(0).getRank();
        String character0Result;
        String character1Result;

        if (pileNorthRanks[ATTACK_RANK_INDEX] > pileSouthRanks[DEFENCE_RANK_INDEX]) {
            scores[getPlayerIndex(nextStartingPlayer)] += pileSouthCharacterRank.getScoreValue();
            scores[getPlayerIndex(nextStartingPlayer + 2)] += pileSouthCharacterRank.getScoreValue();
            character0Result = "Character 0 attack on character 1 succeeded.";
        } else {
            scores[getPlayerIndex(nextStartingPlayer + 1)] += pileSouthCharacterRank.getScoreValue();
            scores[getPlayerIndex(nextStartingPlayer + 3)] += pileSouthCharacterRank.getScoreValue();
            character0Result = "Character 0 attack on character 1 failed.";
        }

        if (pileSouthRanks[ATTACK_RANK_INDEX] > pileNorthRanks[DEFENCE_RANK_INDEX]) {
            scores[getPlayerIndex(nextStartingPlayer + 1)] += pileNorthCharacterRank.getScoreValue();
            scores[getPlayerIndex(nextStartingPlayer + 3)] += pileNorthCharacterRank.getScoreValue();
            character1Result = "Character 1 attack on character 0 succeeded.";
        } else {
            scores[getPlayerIndex(nextStartingPlayer)] += pileNorthCharacterRank.getScoreValue();
            scores[getPlayerIndex(nextStartingPlayer + 2)] += pileNorthCharacterRank.getScoreValue();
            character1Result = "Character 1 attack character 0 failed.";
        }
        updateScores();
        System.out.println(character0Result);
        System.out.println(character1Result);
        setStatusText(character0Result + " " + character1Result);
    }

    private void executeAPlay() {
        resetPile();
        resetIndexes();

        playHeartForCharacters();
        playTurns();

        // 3: calculate winning & update scores for players
        updatePileRanks();
        for (int i = 0; i < piles.length; i++) {
            logger.logPileCards(piles[i], Pile.values()[i]);
        }

        int[] pileNorthRanks = calculatePileRanks(Pile.NORTH.ordinal());
        int[] pileSouthRanks = calculatePileRanks(Pile.SOUTH.ordinal());
        updateScoreForPlayers(pileNorthRanks, pileSouthRanks);
        logger.logScores(pileNorthRanks, pileSouthRanks, scores);

        nextStartingPlayer += 1;
        currentPlay++;
        delay(watchingTime);
    }

    private int getWatchingTime() {
        if (isAuto) {
            return 100;
        } else {
            return 500;
        }
    }
}
