package thrones.game;

public class HeartCharacter implements AffectedCharacter {
    private final Rank rank;
    public HeartCharacter(Rank rank) {
        this.rank = rank;
    }

    public Rank getRank() {
        return this.rank;
    }

    public int getAttack() {
        return this.rank.getScoreValue();
    }

    public int getDefence() {
        return this.rank.getScoreValue();
    }

    public Suit getLastSuit() {
        return Suit.HEARTS;
    }
}
