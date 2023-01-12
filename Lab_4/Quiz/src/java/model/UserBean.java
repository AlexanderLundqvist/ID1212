package model;

import java.io.Serializable;
import java.util.HashMap;

/**
 * This JavaBean represents a user.
 * 
 * @author Alexander Lundqvist & Ramin Shojaei
 */
public class UserBean implements Serializable {
    private Integer id;
    private String username;
    private String password;
    private HashMap<String, Integer> statistics;
    
    /**
     * No-argument constructor for the Bean.
     */
    public UserBean() {
    }
    
    /**
     * Getter for the id field.
     * @return the id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Setter for the id field.
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }
    
    /**
     * Getter for the username field.
     * @return the username
     */
    public String getUsername(){
        return this.username;
    }
    
    /**
     * Setter for the username field.
     * @param username the username to set
     */
    public void setUsername(String username){
        this.username = username;
    }
    
    /**
     * Getter for the password field.
     * @return the password
     */
    public String getPassword(){
        return this.password;
    }
    
    /**
     * Setter for the password field.
     * @param password the password to set
     */
    public void setPassword(String password){
        this.password = password;
    }
    
    /**
     * Getter for the statistics field.
     * @return the statistics
     */
    public HashMap<String, Integer> getStatistics(){
        return this.statistics;
    }
    
    /**
     * Setter for the statistics field.
     * @param results the statistics to set
     */
    public void setStatistics(HashMap<String, Integer> statistics){
        this.statistics = statistics;
    }
    
}
