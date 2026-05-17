package thrones.game;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.CardAdapter;
import ch.aplu.jcardgame.Hand;

import java.util.Optional;

public class HumanPlayer extends Player {

    private Optional<Card> pendingSelectedCard;
    private boolean isCardSelected;

    private int pendingSelectedPileIndex;
    private final int NO_PILE_SELECTED = -1;

    public HumanPlayer(int playerIdentifier) {
        super(playerIdentifier);
        this.pendingSelectedPileIndex = NO_PILE_SELECTED;
        this.isCardSelected = false;
    }

    @Override
    public void assignInitialHand(Hand initialHand) {
        super.assignInitialHand(initialHand);

        getPlayerHand().addCardListener(new CardAdapter() {
            @Override
            public void leftDoubleClicked(Card card) {
                pendingSelectedCard = Optional.of(card);
                isCardSelected = true;
                getPlayerHand().setTouchEnabled(false);
            }

            @Override
            public void rightClicked(Card card) {
                pendingSelectedCard = Optional.empty(); // Represents Passing current round
                isCardSelected = true;
                getPlayerHand().setTouchEnabled(false);
            }
        });
    }

    @Override
    public Optional<Card> selectCardToPlay(PileInformation currentBoard, boolean isCharacterRound) {
        if (getPlayerHand().isEmpty()) {
            return Optional.empty();
        }

        isCardSelected = false;
        pendingSelectedCard = Optional.empty();
        getPlayerHand().setTouchEnabled(true);


        while (true) {
            if (!isCardSelected) {
                pauseThreadExecution(100);
                continue;
            }

            Suit cardSuit = pendingSelectedCard.isPresent() ? (Suit) pendingSelectedCard.get().getSuit() : null;

            boolean isValidCharacterPlay = isCharacterRound && cardSuit != null && cardSuit.isCharacter();
            boolean isValidNonCharacterPlay = !isCharacterRound && (cardSuit == null || !cardSuit.isCharacter());

            if (isValidCharacterPlay || isValidNonCharacterPlay) {
                break;
            } else {
                // The human clicked an invalid card. Reset and wait again.
                isCardSelected = false;
                pendingSelectedCard = Optional.empty();
                getPlayerHand().setTouchEnabled(true);
            }
            pauseThreadExecution(100);
        }

        return pendingSelectedCard;
    }

    public void setTargetPileIndex(int pileIndex) {
        this.pendingSelectedPileIndex = pileIndex;
    }

    @Override
    /** It doesn't actually select a pile. The Board Class needs to call the setTargetPileIndex to update the Pile using
     * pile's cardListener */
    public int choosePileToPlayOn() {
        this.pendingSelectedPileIndex = NO_PILE_SELECTED;

        while (this.pendingSelectedPileIndex == NO_PILE_SELECTED) {
            pauseThreadExecution(100);
        }

        return pendingSelectedPileIndex;
    }

    /* Avoid having the Grid framework in player class */
    private void pauseThreadExecution(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
