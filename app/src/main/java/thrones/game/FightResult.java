package thrones.game;

// Utility class for displaying fight results
public class FightResult {
    private final boolean northAttackSucceeded;
    private final boolean southAttackSucceeded;

    public FightResult (boolean northAttackSucceeded, boolean southAttackSucceeded) {
        this.northAttackSucceeded = northAttackSucceeded;
        this.southAttackSucceeded = southAttackSucceeded;
    }

    public String northResultMessage() {
       return northAttackSucceeded ? "Character 0 attack on character 1 succeeded." : "Character 0 attack on character 1 failed.";
    }

    public String southResultMessage() {
       return southAttackSucceeded ? "Character 1 attack on character 0 succeeded." : "Character 1 attack on character 0 failed.";
    }
}
