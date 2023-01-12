package model;

import java.io.Serializable;
import java.util.Random;

/**
 * This is a JavaBean that represents the model for the number guessing game.
 * It contains the game logic and state.
 * 
 * @author Alexander Lundqvist & Ramin Shojaei
 */
public class GuessBean implements Serializable {
    private int amountOfGuesses;
    private int secretNumber;    
    
    /**
     * No-argument constructor for the Bean.
     */
    public GuessBean() {
        this.amountOfGuesses = 0;
        this.secretNumber = generateNumber();  
    }
    
    /**
     * Getter for the amountOfGuesses field.
     * @return the current amount of guesses
     */
    public int getAmountOfGuesses() {
        return this.amountOfGuesses;
    }
    
    /**
     * Setter for the amountOfGuesses field.
     * @param value the value to be set
     */
    public void setAmountOfQuesses(int value) {
        this.amountOfGuesses = value;
    }
    
    /**
     * Getter for the amountOfGuesses field.
     * @return the secret number
     */
    public int getSecretNumber() {
        return this.secretNumber;
    }
    
    /**
     * Setter for the secretNumber field.
     * @param value the value to be set
     */
    public void setSecretNumber(int value) {
        this.secretNumber = value;
    }
    
    /**
     * Generates a random number between 1 and 100
     * @return the number
     */
    private int generateNumber() {
        Random random = new Random();
        return random.nextInt(100)+1; 
    }
    
    /**
     * Compared the clients guessed number against the actual secret number and
     * returns the corresponding flag.
     * @param input the number the client guessed
     * @return the response flag
     */
    public String handleGuess(String input) {
        if (input == null || input.isEmpty()) {
            return "NO GUESS";
        }
        try {
            int guess = Integer.parseInt(input);
            if (guess < 1 || guess > 100) {
                return "WRONG INPUT";
            }
            this.amountOfGuesses++;
            if (guess == this.secretNumber) return "CORRECT";
            else if (guess < this.secretNumber) return "HIGHER";
            else return "LOWER";
        } catch (NumberFormatException exception) {
            return "WRONG INPUT";
        }
    }   
}
