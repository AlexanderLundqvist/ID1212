package integration;

import java.sql.*;
import java.util.*;
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
    
    /* Development values */
    private HashMap<String, String> users = new HashMap<>();
    private HashMap<Integer, String[]> questions = new HashMap<>();
    private HashMap<Integer, String> quizzes = new HashMap<>();
    private HashMap<Integer, Integer> selector = new HashMap<>();
    private HashMap<Integer, Integer[]> results = new HashMap<>();
    
    /**
     * Constructor for the handler.
     */
    public DBhandler() {
        users.put("Alexlu@kth.se", "Alex");
        users.put("Rshojaei@kth.se", "Ramin");
        
        String[] question1 = {"Which planets are larger than earth?", "Mercury/Mars/Saturn", "0/0/1"};
        String[] question2 = {"Which planet is nearest to the Sun?", "Mercury/Mars/Saturn", "1/0/0"};
        String[] question3 = {"Which planets have rings?", "Mercury/Mars/Saturn", "0/0/1"};
        String[] question4 = {"A caracal is what type of animal?", "Bird/Fish/Cat", "0/0/1"};
        String[] question5 = {"What is the collective name for a group of crows?", "Flight/Murder/Shoal", "0/1/0"};
        String[] question6 = {"Canis is the latin word for which animal?", "Rabbit/Dog/Snake", "0/1/0"};
        String[] question7 = {"Which of these cities was NOT founded by the Romans?", "Alexandria/Cologne/London", "1/0/0"};
        String[] question8 = {"What was the capital of the Byzantine Empire?", "Jerusalem/Constantinople/Alexandria", "0/1/0"};
        String[] question9 = {"Which era came first?", "Paleolithic/Neolithic/Chalcolithic", "1/0/0"};
        questions.put(1, question1);
        questions.put(2, question2);
        questions.put(3, question3);
        questions.put(4, question4);
        questions.put(5, question5);
        questions.put(6, question6);
        questions.put(7, question7);
        questions.put(8, question8);
        questions.put(9, question9);
        
        String subject1 = "Astronomy";
        String subject2 = "Nature";
        String subject3 = "History";
        quizzes.put(1,subject1);
        quizzes.put(2,subject2);
        quizzes.put(3,subject3);
        
        selector.put(1, 1);
        selector.put(1, 2);
        selector.put(1, 3);
        selector.put(2, 4);
        selector.put(2, 5);
        selector.put(2, 6);
        selector.put(3, 7);
        selector.put(3, 8);
        selector.put(3, 9);
        
        Integer[] result1 = {1,1,0};
        Integer[] result2 = {1,2,0};
        Integer[] result3 = {1,3,0};
        Integer[] result4 = {2,1,0};
        Integer[] result5 = {2,2,0};
        Integer[] result6 = {2,3,0};
        results.put(1,result1);
        results.put(2,result2);
        results.put(3,result3);
        results.put(4,result4);
        results.put(5,result5);
        results.put(6,result6);
        
        try{
            Class.forName(this.dbDriver);
            Properties properties = new Properties();
            properties.setProperty(this.dbUser, this.dbPassword);
            this.connection = DriverManager.getConnection(this.dbURL, properties);
            connection.setAutoCommit(false);
            prepareStatements();
        }
        catch(Exception exception){
            System.err.println("Could not connect to database: " + exception.getMessage());
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
                Integer id = result.getInt("id");
                String usernameDB = result.getString("username");
                user.setId(id);
                user.setUsername(usernameDB);
                user.setPassword(password);
            }
        } catch (SQLException exception) {
            System.err.println("Could not fetch user: " + exception.getMessage());
        }
        return user;
    } 
    
    /**
     * 
     * @param subject
     * @return 
     */
    public ArrayList<String> getQuestions(String subject) {
        ArrayList<String> requestedQuestions = new ArrayList<>();
        
        /*-------- Replace with SQL -------------*/
        
        if (subject.equals("Astronomy")) {
            requestedQuestions.add(questions.get(1));
            requestedQuestions.add(questions.get(2));
            requestedQuestions.add(questions.get(3));
        } 
        else if (subject.equals("Nature")) {
            requestedQuestions.add(questions.get(4));
            requestedQuestions.add(questions.get(5));
            requestedQuestions.add(questions.get(6));
        }
        else if (subject.equals("History")) {
            requestedQuestions.add(questions.get(7));
            requestedQuestions.add(questions.get(8));
            requestedQuestions.add(questions.get(9));
        }
        
        /*---------- Replace with SQL -----------*/
        
        
        return requestedQuestions;
    }
    
    /**
     * 
     * @return 
     */
    public String[] getSubjects() {
        String[] requestedSubjects = {"Astronomy", "Nature", "History"};
        // Select all subjects from quizzes table
        return requestedSubjects;
    }
    
    /**
     * 
     * @param user
     * @param subject
     * @return 
     */
    public Integer[] getResults(UserBean user, String subject) {
        Integer[] requestedResults = null;
        // get id from users table, then id from quizzes
        String username = user.getUsername();

        if (username.equals("Alexlu@kth.se")) {
            if (subject.equals("Astronomy")) {
                requestedResults = results.get(1);
            } 
            else if (subject.equals("Nature")) {
                requestedResults = results.get(2);
            }
            else if (subject.equals("History")) {
                requestedResults = results.get(3);
            }
        }
        else if (username.equals("Rshojaei@kth.se")) {
            if (subject.equals("Astronomy")) {
                requestedResults = results.get(4);
            } 
            else if (subject.equals("Nature")) {
                requestedResults = results.get(5);
            }
            else if (subject.equals("History")) {
                requestedResults = results.get(6);
            }
        }
        return requestedResults;
    }
    
    /**
     * This method prepares the SQL statements that are used by the other methods.
     * @throws SQLException 
     */
    private void prepareStatements() throws SQLException {
        getUser = connection.prepareStatement(
                "SELECT id, username, password from users WHERE username=? AND password=?"
        );    
        
        getQuizzes = connection.prepareStatement(
                "SELECT id, subject FROM quizzes"
        );

        getQuestions = connection.prepareStatement(
                "SELECT text, options, answer FROM selector INNER JOIN questions ON id=question_id " + 
                "WHERE quiz_id=(SELECT id FROM quizzes WHERE subject=?)"
        );

        updateResult = connection.prepareStatement(
                "INSERT INTO results (user_id, quiz_id, score) VALUES (?, ?, ?);"
        );

        getResults = connection.prepareStatement(
              "SELECT quiz_id, score FROM results WHERE user_id=?"
        );
    }
}
