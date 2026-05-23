package thrones.game;

/**
 * Immutable value object that represents the outcome of a single fight
 * @param northAttackSucceeded true if the North's attack value was larger the South's defence
 * @param southAttackSucceeded true if the South's attack value was larger the North's defence
 */
public record FightResult(boolean northAttackSucceeded, boolean southAttackSucceeded){

    /**
     * @return a message representing the result of North's attack
     */
    public String northResultMessage() {
       return northAttackSucceeded ? "Character 0 attack on character 1 succeeded." : "Character 0 attack on character 1 failed.";
    }

    /**
     * @return a message representing the result of South's attack
     */
    public String southResultMessage() {
       return southAttackSucceeded ? "Character 1 attack on character 0 succeeded." : "Character 1 attack on character 0 failed.";
    }
}
