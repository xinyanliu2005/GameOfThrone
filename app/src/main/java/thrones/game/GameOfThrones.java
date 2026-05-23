package thrones.game;

// Oh_Heaven.java

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;
import thrones.game.utility.Logger;

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
    private final int nbPlayers = 4;
    // Dealing constants moved to PlayerFactory
    private final int nbPlays = 2;
    private final int nbRounds = 3;
    private Deck deck = new Deck(Suit.values(), Rank.values(), "cover");
    private final Location[] handLocations = {
            new Location(350, 625),
            new Location(75, 350),
            new Location(350, 75),
            new Location(625, 350)
    };

    private final Location[] pileLocations = {
            new Location(350, 280),
            new Location(350, 430)
    };

    // ====================================================================
    // Players created via PlayerFactory (Singleton + Factory Method pattern)
    // Replaces the old PlayerType enum and manual if/else instantiation.
    // ====================================================================
    private List<Player> players;

    private final int watchingTime = 5000;

    private int nextStartingPlayer = random.nextInt(nbPlayers);

    private Board board = new Board(nbPlayers, deck);
    private Logger logger = new Logger();
    private BoardRendererFacade boardRenderer;

    private int currentPlay = 0;
    private List<Integer> firstPlayers = new ArrayList<>();
    private int NUMBER_OF_PLAYS = 2;
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

        // Load auto-play movement data into each Player's MoveData
        if (isAuto) {
            factory.loadAutoPlayMovements(players, properties, nbPlays);
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
            initialHeartStrings.add(new ArrayList<>());
            for (int j = 0; j < nbPlayers; j++) {
                String initialCardKey = "plays." + i + ".players." + j + ".initialCards";
                List<String> initialStrings = cardListFromKey(properties, initialCardKey);
                initialCardStrings.get(i).add(new ArrayList<>());
                if (initialStrings != null) {
                    initialCardStrings.get(i).get(j).addAll(initialStrings);
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

        boardRenderer = new BoardRendererFacade(
                this,
                nbPlayers,
                bgColor
        );
        boardRenderer.initScore();

        setupGame();
    }

    public String runApp() {
        while(currentPlay < nbPlays) {
            executeAPlay();

            boardRenderer.updateScores(board.getScores());
        }

        String text;
        if (board.getScore(0) > board.getScore(1)) {
            text = "Players 0 and 2 won.";
        } else if (board.getScore(0) == board.getScore(1)) {
            text = "All players drew.";
        } else {
            text = "Players 1 and 3 won.";
        }
        System.out.println("Result: " + text);
        setStatusText(text);

        refresh();
        return logger.getAllLog();
    }

    // Card dealing and card lookup are now handled by PlayerFactory and Player.MoveData

    private Optional<Card> selected;
    private final int NON_SELECTION_VALUE = -1;
    private int selectedPileIndex = NON_SELECTION_VALUE;
    private final int UNDEFINED_INDEX = -1;
    public static final int ATTACK_RANK_INDEX = 0;
    public static final int DEFENCE_RANK_INDEX = 1;
    private void setupGame() {
        // Use PlayerFactory (Singleton) to create hands and deal cards
        PlayerFactory factory = PlayerFactory.getInstance();
        Hand[] initialHands = factory.createHands(deck, nbPlayers);
        factory.dealCards(initialHands, deck, nbPlayers, isAuto,
                initialHeartStrings.get(currentPlay),
                initialCardStrings.get(currentPlay));

        // Assign dealt hands to Player
        for (int i = 0; i < nbPlayers; i++) {
            players.get(i).assignInitialHand(initialHands[i]);
            logger.logPlayerCards(initialHands[i], i);
        }

        for (int i = 0; i < nbPlayers; i++) {
            Player player = players.get(i);
            player.getPlayerHand().sort(Hand.SortType.SUITPRIORITY, true);
            System.out.println("hands[" + i + "]: " + canonical(player.getPlayerHand()));
        }

        for (int i = 0; i < nbPlayers; i++) {
            final Hand currentHand = players.get(i).getPlayerHand();
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
            players.get(i).getPlayerHand().setView(this, layouts[i]);
            players.get(i).getPlayerHand().draw();
        }
        // End graphics
    }

    private void resetIndexes() {
        for (Player player : players) {
            player.resetMovementIndex();
        }
    }

    private void resetPile() {
        board.resetPiles();
        Hand[] boardPiles = board.getPiles();
        for (int i = 0; i < boardPiles.length; i++) {
            int pileWidth = 40;
            boardPiles[i].setView(this, new RowLayout(pileLocations[i], 8 * pileWidth));
            boardPiles[i].draw();
            final Hand currentPile = boardPiles[i];
            final int pileIndex = i;
            boardPiles[i].addCardListener(new CardAdapter() {
                public void leftClicked(Card card) {
                    selectedPileIndex = pileIndex;
                    currentPile.setTouchEnabled(false);
                }
            });
        }

        boardRenderer.updatePileRanks(board);
    }

    // pickACorrectSuit and selectRandomPile removed — replaced by
    // Player.selectCardToPlay() and Player.choosePileToPlayOn() polymorphic dispatch

    private void waitForCorrectSuit(int playerIndex, boolean isCharacter) {
        Player currentPlayer = players.get(playerIndex);
        if (currentPlayer.getPlayerHand().isEmpty()) {
            selected = Optional.empty();
        } else {
            selected = null;
            currentPlayer.getPlayerHand().setTouchEnabled(true);
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
                    currentPlayer.getPlayerHand().setTouchEnabled(true);
                }
                delay(100);
            } while (true);
        }
    }

    private void waitForPileSelection() {
        selectedPileIndex = NON_SELECTION_VALUE;
        for (Hand pile : board.getPiles()) {
            pile.setTouchEnabled(true);
        }
        while(selectedPileIndex == NON_SELECTION_VALUE) {
            delay(100);
        }
        for (Hand pile : board.getPiles()) {
            pile.setTouchEnabled(false);
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
            if (players.get(nextStartingPlayer).getPlayerHand().getNumberOfCardsWithSuit(Suit.HEARTS) == 0)
                nextStartingPlayer = getPlayerIndex(nextStartingPlayer + 1);
        }

        assert players.get(nextStartingPlayer).getPlayerHand().getNumberOfCardsWithSuit(Suit.HEARTS) != 0 : " Starting player has no hearts.";

        for (int i = 0; i < 2; i++) {
            int pileIndex = 0;
            selected = Optional.empty();

            int playerIndex = getPlayerIndex(nextStartingPlayer + i);
            setStatusText("Player " + playerIndex + " select a Heart card to play");
            if (isAuto) {
                Player currentPlayer = players.get(playerIndex);
                selected = currentPlayer.playAutoCard(currentPlay);
                if (selected.isPresent()) {
                    pileIndex = currentPlayer.getAutoPileIndex();
                }
            }

            if (selected.isEmpty()) {
                Player currentPlayer = players.get(playerIndex);
                // Polymorphic dispatch: each Player subclass implements its own card selection
                if (isHumanPlayer(playerIndex)) {
                    pileIndex = (nextStartingPlayer + i) % 2;
                    waitForCorrectSuit(playerIndex, true);
                } else {
                    selected = currentPlayer.selectCardToPlay(board, true);
                    if (selected.isPresent()) {
                        pileIndex = currentPlayer.choosePileToPlayOn();
                    }
                }
            }

            assert selected.isPresent() : " Pass returned on selection of character.";
            selected.get().setVerso(false);
            selected.get().transfer(board.getPiles()[pileIndex], true); // transfer to pile (includes graphic effect)
            logger.logPlayerMovement(nextStartingPlayer + i, selected.get(), pileIndex);
            boardRenderer.updatePileRanks(board);

        }
    }

    private void playTurns() {
        // 2: play the remaining nbPlayers * nbRounds - 2
        int remainingTurns = nbPlayers * nbRounds - 2;
        int nextPlayer = (nextStartingPlayer + 2) % nbPlayers;

        while(remainingTurns > 0) {
            boolean hasSelectedCard = false;
            if (isAuto) {
                Player currentPlayer = players.get(nextPlayer);
                selected = currentPlayer.playAutoCard(currentPlay);
                if (selected.isPresent()) {
                    selectedPileIndex = currentPlayer.getAutoPileIndex();
                    setStatusText("Selected: " + canonical(selected.get()) + ". Player" + nextPlayer +
                            " select a pile " + selectedPileIndex + " to play the card.");
                    hasSelectedCard = true;
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
                    selected = currentPlayer.selectCardToPlay(board, false);
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
                selected.get().transfer(board.getPiles()[selectedPileIndex], true); // transfer to pile (includes graphic effect)
                boardRenderer.updatePileRanks(board);
                logger.logPlayerMovement(nextPlayer, selected.get(), selectedPileIndex);
            }

            nextPlayer = (nextPlayer + 1) % nbPlayers;
            remainingTurns--;
        }
    }

    private void displayFightResult(int[] pileNorthRanks, int[] pileSouthRanks) {
        System.out.println("pile north: " + canonical(board.getPiles()[Pile.NORTH.ordinal()]));
        System.out.println("pile north is " + "Attack: " + pileNorthRanks[ATTACK_RANK_INDEX] + " - Defence: " + pileNorthRanks[DEFENCE_RANK_INDEX]);
        System.out.println("pile south: " + canonical(board.getPiles()[Pile.SOUTH.ordinal()]));
        System.out.println("pile south is " + "Attack: " + pileSouthRanks[ATTACK_RANK_INDEX] + " - Defence: " + pileSouthRanks[DEFENCE_RANK_INDEX]);

        FightResult fightResult = board.executeFight();
        boardRenderer.updateScores(board.getScores());
        System.out.println(fightResult.northResultMessage());
        System.out.println(fightResult.southResultMessage());
        setStatusText(fightResult.northResultMessage() + " " + fightResult.southResultMessage());
    }

    private void executeAPlay() {
        resetPile();
        resetIndexes();

        playHeartForCharacters();
        playTurns();

        // 3: calculate winning & update scores for players
        boardRenderer.updatePileRanks(board);
        for (int i = 0; i < board.getPiles().length; i++) {
            logger.logPileCards(board.getPiles()[i], Pile.values()[i]);
        }

        int[] pileNorthRanks =  { board.getPileAttack(Pile.NORTH.ordinal()), board.getPileDefence(Pile.NORTH.ordinal())};
        int[] pileSouthRanks =  { board.getPileAttack(Pile.SOUTH.ordinal()), board.getPileDefence(Pile.SOUTH.ordinal())};
        displayFightResult(pileNorthRanks, pileSouthRanks);
        logger.logScores(pileNorthRanks, pileSouthRanks, board.getScores());

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