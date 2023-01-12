package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * This class represents a HTTP request.
 *
 * @author Alexander Lundqvist & Ramin Shojaei
 */
public class Request {
    private final String requestLine;
    private final String[] requestLineTokens;
    private final HashMap<String, String> headers;
    //private final String body;
    
    /**
     * Creates a new Request object with a input stream from the socket of 
     * a connected client.
     * @param incoming the input stream
     */
    public Request(BufferedReader incoming) {
        this.requestLine = parseRequestLine(incoming);
        this.requestLineTokens = this.requestLine.trim().split("\\s+");
        this.headers = parseHeaders(incoming);
        //this.body = parseBody(incoming);
    }
    
    /**
     * Reads the request line from the input stream.
     * @param incoming input stream
     * @return the request line
     */
    private String parseRequestLine(BufferedReader incoming) {
        String requestLine = new String();
        try {
            requestLine = incoming.readLine();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return requestLine;
    }
    
    /**
     * Converts the header lines to <key, value> pairs and puts them in a HashMap.
     * @param incoming the input stream
     * @return the HashMap
     */
    private HashMap<String, String> parseHeaders(BufferedReader incoming) {
        HashMap<String, String> headers = new HashMap<String, String>();
        String headerLine;
        try {
            while ((headerLine = incoming.readLine()) != null && !headerLine.isBlank()) {
                String[] values = headerLine.split(": ");
                headers.put(values[0], values[1]);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return headers;
    }
    
    // Not needed, we only use the URI
    private String parseBody(BufferedReader incoming) {
        String body = new String();
        try {
            while (incoming.ready()) {
                String line = incoming.readLine();
                if (line != null && !line.isBlank() && !line.isEmpty()) {
                    body += line;
                }
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return body;
    }
    
    /**
     * Gets the request cookie.
     * @return the cookie
     */
    public String getCookie() {
        String cookie = new String();
        if (this.headers.containsKey("Cookie") && this.headers.get("Cookie").startsWith("sessionID=")) {
            cookie = this.headers.get("Cookie").trim().split("=")[1];
        }
        return cookie;
    }
    
    /**
     * Gets the HTTP method.
     * @return the method
     */
    public String getMethod() {
        return this.requestLineTokens[0];
    }
    
    /**
     * Gets the requested resource (or user input in this case).
     * @return the URI in string format
     */
    public String getURI() {
        return this.requestLineTokens[1];
    }
    
    /**
     * This function analyses the request line to see if it is a valid HTTP
     * request and if the server can accept it.
     * requestLineTokens = {METHOD, URI, PROTOCOL}
     * @return the status code
     */
    public int validateRequestLine() {
        // Bad Request
        if (this.requestLineTokens.length != 3) {
           return 400;
        }        
        // HTTP Version Not Supported
        if (!this.requestLineTokens[2].equals("HTTP/1.1")) {
            return 505;
        }   
        // OK or Method Not Allowed
        if (this.requestLineTokens[1].startsWith("/") && !this.requestLineTokens[1].equals("/favicon.ico")) {
            if (this.requestLineTokens[0].equals("GET") || this.requestLineTokens[0].equals("POST")) {
                return 200;
            }
            else {
                return 405;
            }  
        }   
        // Not Found
        else {
            return 404;
        }    
    }
    
    /**
     * Prints out the whole request to the console.
     */
    public void print() {
        System.out.println();
        System.out.println(this.requestLine);
        for (String key : this.headers.keySet()) {
            System.out.println(key + ": " + this.headers.get(key));
        }
        //System.out.println(this.body);
        System.out.println();
    }
}
