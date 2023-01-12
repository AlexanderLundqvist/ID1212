package model;

import java.util.Random;

/**
 * This class represents an instance of the guessing game.
 * 
 * @author Alexander Lundqvist & Ramin Shojaei
 */
public class GameSession {
    private final String cookie;
    private Random random;
    private int amountOfGuesses;
    private int secretNumber;
    
    /**
     * Initializes the game session object.
     * @param cookie is the cookie
     */
    public GameSession(String cookie) {
        this.cookie = cookie;
        this.random = new Random();
        this.amountOfGuesses = 0;
        this.secretNumber = generateNumber();        
    }
    
    /**
     * Gets the current amount of attempted guesses for this session.
     * @return the amount
     */
    public int getAmountOfGuesses() {
        return this.amountOfGuesses;
    }
    
    /**
     * Gets the current secret number for this session.
     * @return the number
     */
    public int getSecretNumber() {
        return this.amountOfGuesses;
    }
    
    /**
     * Gets the cookie for this session.
     * @return the cookie
     */
    public String getCookie() {
        return this.cookie;
    }
    
    /**
     * Generates a random number between 1 and 100
     * @return the number
     */
    private int generateNumber() {
        return this.random.nextInt(100)+1; 
    }
    
    /**
     * Compared the clients guessed number against the actual secret number and
     * returns the corresponding flag.
     * @param guess the number the client guessed
     * @return the response flag
     */
    public String Guess(int guess) {
        this.amountOfGuesses++;
        if (guess == this.secretNumber) return "CORRECT";
        else if (guess < this.secretNumber) return "HIGHER";
        else return "LOWER";
    }
    
    /**
     * This function resets the game
     */
    public void restart() {
        this.amountOfGuesses = 0;
        this.secretNumber = generateNumber();
    }
    
    
}
