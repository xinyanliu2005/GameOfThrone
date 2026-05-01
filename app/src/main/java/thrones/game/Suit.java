package thrones.game;

enum GoTSuit { CHARACTER, DEFENCE, ATTACK, MAGIC }
public enum Suit {
    SPADES(GoTSuit.DEFENCE, "S"),
    HEARTS(GoTSuit.CHARACTER, "H"),
    DIAMONDS(GoTSuit.MAGIC, "D"),
    CLUBS(GoTSuit.ATTACK, "C");
    private String suitShortHand = "";

    Suit(GoTSuit gotsuit, String shortHand) {
        this.gotsuit = gotsuit;
        this.suitShortHand = shortHand;
    }
    private final GoTSuit gotsuit;

    public String getSuitShortHand() {
        return suitShortHand;
    }

    public boolean isDefence(){ return gotsuit == GoTSuit.DEFENCE; }

    public boolean isAttack(){ return gotsuit == GoTSuit.ATTACK; }

    public boolean isCharacter(){ return gotsuit == GoTSuit.CHARACTER; }

    public boolean isMagic(){ return gotsuit == GoTSuit.MAGIC; }
}
