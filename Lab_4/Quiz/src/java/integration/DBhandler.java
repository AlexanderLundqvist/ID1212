package integration;

import java.sql.*;
import java.util.*;
import model.QuestionBean;
import model.QuizBean;
import model.UserBean;

// 'jdbc:derby:C:\Users\Alexander\Software\DerbyDB\quizDB;'

/**
 * This class handles transactions aimed at an Apache Derby database that 
 * follows the schema described in the assignment. The DB mainly contains the 
 * quizzes and registered users.
 * 
 * Tables in Derby DB:
 *      users       (id, username, password)
 *      questions   (id, text, options, answer)
 *      quizzes     (id, subject)
 *      selector    (quiz_id, question_id)
 *      results     (id, user_id, quiz_id, score)
 * 
 *
 * @author Alexander Lundqvist & Ramin Shojaei
 */
public class DBhandler {
    private final String dbURL = "jdbc:derby:C:\\Users\\Alexander\\Software\\DerbyDB\\quizDB";
    private final String dbURLWeb = "jdbc:derby://localhost:1527/quizDB";
    private final String dbDriver = "org.apache.derby.jdbc.EmbeddedDriver";
    private final String dbUser = "nbuser";
    private final String dbPassword = "nbuser";
    private Connection connection;
    private PreparedStatement getUser;
    private PreparedStatement getQuizzes;   
    private PreparedStatement getQuestions;
    private PreparedStatement updateResult;
    private PreparedStatement getResults; 
    
    /**
     * Constructor for the handler.
     */
    public DBhandler() {          
        try{
            Class.forName(this.dbDriver);
            Properties properties = new Properties();
            properties.setProperty("user", this.dbUser);
            properties.setProperty("password",this.dbPassword);
            this.connection = DriverManager.getConnection(this.dbURL, properties);
            this.connection.setAutoCommit(false);
            prepareStatements();
        }
        catch(Exception exception){
            System.err.println("Could not connect to database: " + exception.getMessage());
            exception.printStackTrace();
        }
    }
    
    /**
     * This method uses several SQL statements to create a UserBean object from
     * the data present in the database.
     * @param username the username
     * @param password the password
     * @return a UserBean
     */
    public UserBean getUser(String username, String password) {
        UserBean user = null;
        try {
            getUser.setString(1, username);
            getUser.setString(2, password);
            ResultSet result = getUser.executeQuery();
            if (result.next()) {
                user = new UserBean();
                Integer id = result.getInt("id");
                String usernameDB = result.getString("username");
                user.setId(id);
                user.setUsername(usernameDB);
                user.setPassword(password);
                user.setResults(getResults(id));
            }
            result.close();
        } catch (SQLException exception) {
            System.err.println("Could not fetch user: " + exception.getMessage());
            exception.printStackTrace();
        }
        return user;
    } 
    
    /**
     * Gets all the available quizzes from the database.
     * @return all quizzes
     */
    public ArrayList<QuizBean> getQuizzes() {
        ArrayList<QuizBean> quizzes = new ArrayList<>();
        try {
            ResultSet result = getQuizzes.executeQuery();
            while (result.next()) {
                // Get the row cells
                Integer id = result.getInt("id");
                String subject = result.getString("subject");
                
                // Create the quiz 
                QuizBean quiz = new QuizBean();
                quiz.setId(id);
                quiz.setSubject(subject);
                quiz.setQuestions(getQuestions(id));
            }
            result.close();
        } catch (SQLException exception) {
            System.err.println("Could not fetch quiz: " + exception.getMessage());
            exception.printStackTrace();
        }
        return quizzes;
    }
    
    
    /**
     * Creates a question for a specific quiz.
     * @param quizId is the ID of the quiz which identifies the category.
     * @return the QuestionBean
     */
    public ArrayList<QuestionBean> getQuestions(Integer quizId) {
        ArrayList<QuestionBean> requestedQuestions = new ArrayList<>();
        try {
            ResultSet result = getQuestions.executeQuery();
            while (result.next()) {
                // Get the row cells
                Integer id = result.getInt("id");
                String text = result.getString("text");
                String options = result.getString("options");
                String answer = result.getString("answer");
                
                // Parse the questions
                ArrayList<String> optionsList = new ArrayList<>();
                String[] optionsParsed = options.split("/");
                for (int i = 0; i < optionsParsed.length; i++) {
                    optionsList.add(optionsParsed[i]);
                }
                
                // Parse the answer
                ArrayList<String> answerList = new ArrayList<>();
                String[] answerParsed = answer.split("/");
                for (int j = 0; j < answerParsed.length; j++) {
                    answerList.add(answerParsed[j]);
                }
                
                // Create the question
                QuestionBean question = new QuestionBean();
                question.setId(id);
                question.setQuestion(text);
                question.setOptions(optionsList);
                question.setAnswer(answerList);
                
            }
            result.close();
        } catch (SQLException exception) {
            System.err.println("Could not fetch questions: " + exception.getMessage());
            exception.printStackTrace();
        } 
        return requestedQuestions;
    }
    
    
    
    /**
     * Retrieves the stored quiz results from the database for a specific user.
     * @param id the user ID
     * @return the results for that user
     */
    public ArrayList<Integer> getResults(Integer id) {
        ArrayList<Integer> requestedResults = new ArrayList<>();
        try {
            getResults.setInt(1, id);
            ResultSet result = getResults.executeQuery();
            while (result.next()) {
                // Get the row cells
                Integer score = result.getInt("score");
                requestedResults.add(score);
            }
            result.close();
        } catch (SQLException exception) {
            System.err.println("Could not fetch results: " + exception.getMessage());
            exception.printStackTrace();
        }
        return requestedResults;
    }
    
    /**
     * Updates the results table for a specific user with the new results. 
     * @param userId the user ID
     * @param quizId the quiz ID
     * @param score the score
     */
    public void updateResult(Integer userId, Integer quizId, Integer score) {
        try {
            updateResult.setInt(1, userId);
            updateResult.setInt(2, quizId);
            updateResult.setInt(3, score);
            updateResult.executeQuery();
        } catch (SQLException exception) {
            System.err.println("Could not update results: " + exception.getMessage());
            exception.printStackTrace();
        }
    }
    
    /**
     * This method prepares the SQL statements that are used by the other methods.
     * @throws SQLException 
     */
    private void prepareStatements() throws SQLException {
        getUser = this.connection.prepareStatement(
                "SELECT id, username, password FROM users WHERE username=? AND password=?"
        );    
        
        getQuizzes = this.connection.prepareStatement(
                "SELECT id, subject FROM quizzes"
        );

        getQuestions = this.connection.prepareStatement(
                "SELECT id, text, options, answer FROM questions WHERE id=(" + 
                        "SELECT question_id FROM selector WHERE quiz_id=?)"
        );

        getResults = this.connection.prepareStatement(
              "SELECT user_id, quiz_id, score FROM results WHERE user_id=?"
        );

        updateResult = this.connection.prepareStatement(
                "INSERT INTO results (user_id, quiz_id, score) VALUES (?, ?, ?);"
        );
    }
}
