package thrones.game;

//Record for displaying fight results

public record FightResult(boolean northAttackSucceeded, boolean southAttackSucceeded){

    public String northResultMessage() {
       return northAttackSucceeded ? "Character 0 attack on character 1 succeeded." : "Character 0 attack on character 1 failed.";
    }

    public String southResultMessage() {
       return southAttackSucceeded ? "Character 1 attack on character 0 succeeded." : "Character 1 attack on character 0 failed.";
    }
}
