package thrones.game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

import java.util.List;
import java.util.Optional;

public class Board implements PileInformation {

    private final Hand[] piles = new Hand[2];
    private final List<Player> players;
    private final int[] scores;

    // Optional: Pass an Observer here if you want the Board to notify GameOfThrones to redraw UI
    public Board(List<Player> players) {
        this.players = players;
        this.scores = new int[players.size()];
    }


    
    public void executeAPlay(int startingPlayerIndex) {
        // 1. Initial Heart Phase
        playHeartForCharacters(startingPlayerIndex);
        
        // 2. Main Turns Phase
        playTurns(startingPlayerIndex);
        
        // 3. Resolve Round and Update Scores
        updateScoreForPlayers();
    }
    
    private void playHeartForCharacters(int startingPlayerIndex) {
        // Example logic:
        // Player currentPlayer = players.get(index);
        // Optional<Card> card = currentPlayer.selectCardToPlay(this, true); // true = isCharacterRound
        // int pileIndex = currentPlayer.choosePileToPlayOn();
        // card.get().transfer(piles[pileIndex], true);
    }
    
    private void playTurns(int startingPlayerIndex) {
        // Example logic: Loop over players for the remaining turns
        // Optional<Card> card = currentPlayer.selectCardToPlay(this, false); // false = not character round
    }

    private void updateScoreForPlayers() {
        // Calculate the attack and defence from piles
        // Determine the winners for this play
        // Update this.scores 
        // Notify UI to update if necessary
    }

    // ==========================================
    // PileInformation (Facade/Read-Only) Methods
    // ==========================================

    @Override
    public int getPileAttack(int pileIndex) {
        // Implement logic to compute the attack value for the pile
        return CharacterBuilder.fromCards(getPileCards(pileIndex)).map(AffectedCharacter::getAttack).orElse(0);
    }

    @Override
    public int getPileDefence(int pileIndex) {
        // Implement logic to compute the defence value for the pile
        return CharacterBuilder.fromCards(getPileCards(pileIndex)).map(AffectedCharacter::getDefence).orElse(0);
    }

    @Override
    public Optional<Card> getLastPlayedCard(int pileIndex) {
        if (piles == null || pileIndex >= piles.length || piles[pileIndex].isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(piles[pileIndex].getLast());
    }

    @Override
    public List<Card> getPileCards(int pileIndex) {
        if (piles == null || pileIndex >= piles.length || piles[pileIndex].isEmpty()) {
            return List.of();
        }
        return piles[pileIndex].getCardList();
    }

    @Override
    public int getPlayerScore(int playerIndex) {
        if (playerIndex >= 0 && playerIndex < scores.length) {
            return scores[playerIndex];
        }
        return 0;
    }
}