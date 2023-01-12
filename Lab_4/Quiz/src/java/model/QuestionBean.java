package model;

import java.io.Serializable;

/**
 * This JavaBean represents a question.
 * 
 * @author Alexander Lundqvist & Ramin Shojaei
 */
public class QuestionBean implements Serializable {
    private Integer id;
    private String question;
    private String[] options;
    private String[] solution;
    
    /**
     * No-argument constructor for the Bean.
     */
    public QuestionBean() {
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
     * Getter for the question field.
     * @return the question
     */
    public String getQuestion() {
        return question;
    }

    /**
     * Setter for the question field.
     * @param question the question to set
     */
    public void setQuestion(String question) {
        this.question = question;
    }

    /**
     * Getter for the options field.
     * @return the options
     */
    public String[] getOptions() {
        return this.options;
    }

    /**
     * Setter for the options field.
     * @param options the options to set
     */
    public void setOptions(String[] options) {
        this.options = options;
    }

    /**
     * Getter for the solution field.
     * @return the solution
     */
    public String[] getSolution() {
        return this.solution;
    }

    /**
     * Setter for the solution field.
     * @param solution the solution to set
     */
    public void setSolution(String[] solution) {
        this.solution = solution;
    }
    
    
    
}
