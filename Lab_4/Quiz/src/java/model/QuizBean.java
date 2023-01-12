package model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This JavaBean represents a user.
 * 
 * @author Alexander Lundqvist & Ramin Shojaei
 */
public class QuizBean implements Serializable {
    private Integer id;
    private String subject;
    private ArrayList<QuestionBean> questions;
    
    /**
     * No-argument constructor for the Bean.
     */
    public QuizBean() {
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
     * Getter for the subject field.
     * @return the subject
     */
    public String getSubject() {
        return this.subject;
    }

    /**
     * Setter for the subject field.
     * @param subject the subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    /**
     * Getter for the questions field.
     * @return the questions
     */
    public ArrayList<QuestionBean> getQuestions() {
        return this.questions;
    }

    /**
     * Setter for the questions field.
     * @param questions the questions to set
     */
    public void setQuestions(ArrayList<QuestionBean> questions) {
        this.questions = questions;
    }
}
