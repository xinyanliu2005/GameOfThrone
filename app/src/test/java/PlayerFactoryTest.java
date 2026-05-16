import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import thrones.game.*;

import java.util.List;
import java.util.Properties;
import java.util.Random;

public class PlayerFactoryTest {

    private PlayerFactory factory;

    @Before
    public void setUp() {
        PlayerFactory.reset();
        factory = PlayerFactory.init(new Random(1));
    }

    @Test
    public void createsHumanPlayer() {
        Player player = factory.createPlayer(0, "human");

        Assert.assertTrue(player instanceof HumanPlayer);
        Assert.assertEquals(0, player.getPlayerIdentifier());
    }

    @Test
    public void createsRandomBotPlayer() {
        Player player = factory.createPlayer(1, "random");

        Assert.assertTrue(player instanceof RandomBotPlayer);
        Assert.assertEquals(1, player.getPlayerIdentifier());
    }

    @Test
    public void createsSmartBotPlayer() {
        Player player = factory.createPlayer(2, "smart");

        Assert.assertTrue(player instanceof SmartBotPlayer);
        Assert.assertEquals(2, player.getPlayerIdentifier());
    }

    @Test
    public void createsLegalBotPlayerWithoutConsiderations() {
        Player player = factory.createPlayer(3, "legal");

        Assert.assertTrue(player instanceof LegalBotPlayer);
        Assert.assertEquals(3, player.getPlayerIdentifier());

        LegalBotPlayer legalBot = (LegalBotPlayer) player;
        Assert.assertTrue(legalBot.getConsiderationCodes().isEmpty());
    }

    @Test
    public void createsLegalBotPlayerWithConsiderations() {
        Player player = factory.createPlayer(1, "legal-oa,td,tm");

        Assert.assertTrue(player instanceof LegalBotPlayer);

        LegalBotPlayer legalBot = (LegalBotPlayer) player;

        Assert.assertEquals(3, legalBot.getConsiderationCodes().size());
        Assert.assertEquals("OA", legalBot.getConsiderationCodes().get(0));
        Assert.assertEquals("TD", legalBot.getConsiderationCodes().get(1));
        Assert.assertEquals("TM", legalBot.getConsiderationCodes().get(2));
    }

    @Test
    public void trimsSpacesAndIgnoresCase() {
        Player player = factory.createPlayer(2, "  LeGaL-oa, tm  ");

        Assert.assertTrue(player instanceof LegalBotPlayer);

        LegalBotPlayer legalBot = (LegalBotPlayer) player;

        Assert.assertEquals(2, legalBot.getConsiderationCodes().size());
        Assert.assertEquals("OA", legalBot.getConsiderationCodes().get(0));
        Assert.assertEquals("TM", legalBot.getConsiderationCodes().get(1));
    }

    @Test
    public void createsHumanPlayerWhenConfigMissing() {
        Player player = factory.createPlayer(0, null);

        Assert.assertTrue(player instanceof HumanPlayer);
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
        Assert.assertEquals("OA", legalBot.getConsiderationCodes().get(0));
        Assert.assertEquals("TD", legalBot.getConsiderationCodes().get(1));
    }
}