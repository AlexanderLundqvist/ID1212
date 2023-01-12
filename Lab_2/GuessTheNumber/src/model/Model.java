package model;

import java.util.HashMap;

/**
 * This is the static model for the application that manages and holds all 
 * game session objects. 
 *
 * @author Alexander Lundqvist & Ramin Shojaei
 */
public class Model {
    public HashMap<String, GameSession> sessions;
    
    /**
     * Initializes the model. Only done once.
     */
    public Model() {
        this.sessions = new HashMap<String, GameSession>();
    }
    
    /**
     * Get function to retrieve the GameSession tied to the specified cookie.
     * @param cookie is the identifier for the client.
     * @return the GameSession object. 
     */
    public GameSession getSession(String cookie) {
        return sessions.get(cookie);
    }
    
    /**
     * Set function that adds a new <Cookie, GameSession> pair to the model.
     * @param cookie is the identifier for the client. 
     */
    public void addSession(String cookie) {
        sessions.put(cookie, new GameSession(cookie));
    }
    
    /**
     * Delete function that removes a GameSession from the model.
     * @param cookie is the identifier for the client.
     */
    public void removeSession(String cookie) {
        sessions.remove(cookie);
    }

}
