package thrones.game;

import java.util.*;

/**
 * Singleton Factory for creating Player instances based on configuration strings
 * from the properties file.
 *
 * Design patterns used:
 * - Singleton: ensures a single factory instance manages player creation consistently
 * - Factory Method: encapsulates the instantiation logic for different Player subtypes,
 *   so GameOfThrones doesn't need to know concrete player classes or parsing details
 *
 * GRASP principles:
 * - Creator: PlayerFactory has the knowledge needed to create players (it parses config strings)
 * - Low Coupling: GameOfThrones depends only on PlayerFactory and abstract Player, not on
 *   HumanPlayer, RandomBotPlayer, LegalBotPlayer, SmartBotPlayer directly
 * - High Cohesion: all player-creation logic is in one place
 */
public class PlayerFactory {

    private static PlayerFactory instance;
    private Random random;

    private PlayerFactory(Random random) {
        this.random = random;
    }

    /**
     * Initialise the singleton with a shared Random instance.
     * Must be called once before getInstance().
     */
    public static synchronized PlayerFactory init(Random random) {
        if (instance == null) {
            instance = new PlayerFactory(random);
        }
        return instance;
    }

    /**
     * Get the singleton instance. Throws if init() hasn't been called.
     */
    public static synchronized PlayerFactory getInstance() {
        if (instance == null) {
            throw new IllegalStateException("PlayerFactory has not been initialised. Call init(Random) first.");
        }
        return instance;
    }

    /**
     * Reset the singleton (useful for testing where multiple games are created).
     */
    public static synchronized void reset() {
        instance = null;
    }

    /**
     * Creates a Player from the properties configuration string.
     *
     * Expected formats:
     *   "human"                -> HumanPlayer
     *   "random"               -> RandomBotPlayer
     *   "legal-oa,td,tm"       -> LegalBotPlayer with OA, TD, TM considerations
     *   "legal"                -> LegalBotPlayer with no considerations (passes by default)
     *   "smart"                -> SmartBotPlayer
     *
     * @param playerIndex  the player's index (0-3)
     * @param configString the raw string from properties, e.g. "legal-oa,td,tm"
     * @return a fully configured Player instance
     */
    public Player createPlayer(int playerIndex, String configString) {
        if (configString == null || configString.trim().isEmpty()) {
            // Default to human if not specified
            return new HumanPlayer(playerIndex);
        }

        String trimmed = configString.trim().toLowerCase();

        // Split on the first '-' to separate player type from optional config
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
                System.err.println("Unknown player type: " + playerTypeStr + ". Defaulting to human.");
                return new HumanPlayer(playerIndex);
        }
    }

    /**
     * Creates all four players from a Properties object.
     *
     * @param properties the game properties
     * @param nbPlayers  number of players (typically 4)
     * @return list of Player instances in order
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
     * Creates a LegalBotPlayer with the specified considerations parsed from the config string.
     * e.g. "oa,td,tm" -> list of consideration codes [OA, TD, TM]
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
        return new LegalBotPlayer(playerIndex, random, considerationCodes);
    }
}
