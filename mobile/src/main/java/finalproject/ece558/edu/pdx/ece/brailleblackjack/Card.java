package finalproject.ece558.edu.pdx.ece.brailleblackjack;

/**
 * A card in the deck
 */
public class Card {
    private String cardKey;
    private String cardDescription;
    private String cardDrawable;
    private int cardValue;

    /**
     * Fully Qualified Constructor
     */
    public Card(String cardKey, String cardDescription, String cardDrawable, int cardValue){
        this.cardKey = cardKey;
        this.cardDescription = cardDescription;
        this.cardDrawable = cardDrawable;
        this.cardValue = cardValue;
    }

    /**
     * Get the current card's key
     * @return String of card key
     */
    public String getCardKey() {
        return cardKey;
    }

    /**
     * Get the current card's value
     * @return Integer of card value
     */
    public int getCardValue() {
        return cardValue;
    }

    /**
     * Get the current card's drawable
     * @return String of card drawable
     */
    public String getCardDrawable() {
        return cardDrawable;
    }

    /**
     * Get the current card's description for Voice-Over
     * @return String of card description
     */
    public String getCardDescription() {
        return cardDescription;
    }

    /**
     * Set a card key value
     * @param cardKey
     */
    public void setCardKey(String cardKey) {
        this.cardKey = cardKey;
    }

    /**
     * Set a card description for Voice-Over
     * @param cardDescription
     */
    public void setCardDescription(String cardDescription) {
        this.cardDescription = cardDescription;
    }

    /**
     * Set a card drawable string
     * @param cardDrawable
     */
    public void setCardDrawable(String cardDrawable) {
        this.cardDrawable = cardDrawable;
    }

    /**
     * Set a card numerical value
     * @param cardValue
     */
    public void setCardValue(int cardValue) {
        this.cardValue = cardValue;
    }
}
