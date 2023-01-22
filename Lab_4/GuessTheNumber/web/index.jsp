<%-- 
    Document   : index
    Author     : Alexander Lundqvist & Ramin Shojaei

    This JSP document is the view component of the number guessing game. It relays
    the attempted guess to the HTTPHandler (Controller) and renders the web page
    according to the data stored in the HTTPSession.  
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Guess Game</title>
        <!-- Bootstrap latest compiled and minified core CSS -->
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@3.3.7/dist/css/bootstrap.min.css" 
        integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
        <!-- Optional theme -->
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@3.3.7/dist/css/bootstrap-theme.min.css" 
        integrity="sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp" crossorigin="anonymous">
    </head>
    <body>
        <h2>ID1212: Lab 4</h2>
        <h4>The Number Guess Game</h4>
        <div id="message">
            <%-- Conditional rendering based on session attributes --%>
            <%  
                if (request.getSession().getAttribute("GuessBean") == null) {
                    out.print("Welcome to the Number Guess Game. Guess a number between 1 and 100.");
                }
                else {
                    String answer = (String) request.getSession().getAttribute("Answer");
                    switch (answer) {
                        case "NO GUESS":
                            out.print("Welcome to the Number Guess Game. Guess a number between 1 and 100.");
                            break;
                        case "WRONG INPUT":
                            out.print("You have to guess on an integer between 1 and 100!");
                            break;
                        case "LOWER":
                            out.print("Nope, guess lower!");
                            break;
                        case "HIGHER":
                            out.print("Nope, guess higher!");
                            break;
                        case "CORRECT":
                            out.print("You made it!!!");
                            break;
                        default:
                            out.print("Something went wrong...");
                            break;
                    }
                    if (!answer.equals("CORRECT")) {
                        out.print("<br>You have made " + (int) request.getSession().getAttribute("AmountOfGuesses") + " guess(es).");
                    }
                }
            %>
        </div>
        <%-- submit calls the HTTPHandler.java method doGet() to handle the guess --%>
        <form id="form" action="HTTPHandler" method="GET">
            <input type="text" name="guess" placeholder="Enter a number..." required>
            <input type="submit" value="Submit">
        </form>
    </body>
</html>
