package integration;

import java.sql.*;
import java.util.*;
import model.QuestionBean;
import model.QuizBean;
import model.UserBean;


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
        try {
            connection = createConnection();
            prepareStatements();
        } catch (Exception exception){
            System.err.println("Could not create database handler: " + exception.getMessage());
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
            this.getUser.setString(1, username);
            this.getUser.setString(2, password);
            ResultSet result = this.getUser.executeQuery();
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
            ResultSet result = this.getQuizzes.executeQuery();
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
            this.getQuestions.setInt(1, quizId);
            ResultSet result = this.getQuestions.executeQuery();
            while (result.next()) {
                // Get the row cells
                Integer id = result.getInt("id");
                String text = result.getString("text");
                String options = result.getString("options");
                String answer = result.getString("answer");
                
                // Parse the questions
                ArrayList<String> optionsList = new ArrayList<>();
                String[] optionsParsed = options.split("/");
                optionsList.addAll(Arrays.asList(optionsParsed));
                
                // Parse the answer
                ArrayList<String> answerList = new ArrayList<>();
                String[] answerParsed = answer.split("/");
                answerList.addAll(Arrays.asList(answerParsed));
                
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
            this.getResults.setInt(1, id);
            ResultSet result = this.getResults.executeQuery();
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
            this.updateResult.setInt(1, userId);
            this.updateResult.setInt(2, quizId);
            this.updateResult.setInt(3, score);
            this.updateResult.executeQuery();
        } catch (SQLException exception) {
            System.err.println("Could not update results: " + exception.getMessage());
            exception.printStackTrace();
        }
    }
    
    /**
     * Creates the database connection.
     * @return the connection
     */
    private Connection createConnection() {
        Connection dbConnection = null;
        try {
            Class.forName(this.dbDriver);
            Properties properties = new Properties();
            properties.setProperty("user", this.dbUser);
            properties.setProperty("password",this.dbPassword);
            dbConnection = DriverManager.getConnection(this.dbURL, properties);
            dbConnection.setAutoCommit(false);
            System.out.println("Successful connection to database!");
        } catch (ClassNotFoundException exception){
            System.err.println("Failed to load database drivers: " + exception.getMessage());
            exception.printStackTrace();
        } catch (SQLException exception){
            System.err.println("Could not connect to database: " + exception.getMessage());
            exception.printStackTrace();
        }
        return dbConnection;
    }
    
    
    /**
     * This method prepares the SQL statements that are used by the other methods.
     */
    private void prepareStatements() throws SQLException {
        this.getUser = this.connection.prepareStatement(
            "SELECT id, username, password FROM users WHERE username=? AND password=?"
        );    

        this.getQuizzes = this.connection.prepareStatement(
                "SELECT id, subject FROM quizzes"
        );

        this.getQuestions = this.connection.prepareStatement(
                "SELECT id, text, options, answer FROM selector INNER JOIN questions ON id=question_id WHERE quiz_id=?"
        );

        this.getResults = this.connection.prepareStatement(
              "SELECT quiz_id, score FROM results WHERE user_id=?"
        );

        this.updateResult = this.connection.prepareStatement(
                "INSERT INTO results (user_id, quiz_id, score) VALUES (?, ?, ?)"
        );
    }
}
