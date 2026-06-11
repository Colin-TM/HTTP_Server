package org.example;

/**
 * The StatusCodes class will act as a quick reference to every code that will be anticipated for this project. If there
 * is a custom status code that is not anticipated, it will be input manually since it will be the result of a CGI.
 */
public class StatusCodes {
    public String get200() { return "200 OK"; }
    public String get201() { return "201 Created"; }
    public String get206() { return "206 Partial Content"; }
    public String get300() { return "300 Multiple Choice"; }
    public String get301() { return "301 Moved Permanently"; }
    public String get302() { return "302 Found"; }
    public String get304() { return "304 Not Modified"; }
    public String get400() { return "400 Bad Request"; }
    public String get401() { return "401 Unauthorized"; }
    public String get403() { return "403 Forbidden"; }
    public String get404() { return "404 Not Found"; }
    public String get405() { return "405 Method Not Allowed"; }
    public String get406() { return "406 Not Acceptable"; }
    public String get408() { return "408 Request Timeout"; }
    public String get411() { return "411 Length Required"; }
    public String get412() { return "412 Precondition Failed"; }
    public String get413() { return "413 Request Entity Too Large"; }
    public String get414() { return "414 Request-URI Too Long"; }
    public String get416() { return "416 Requested Range Not Satisfiable"; }
    public String get500() { return "500 Internal Server Error"; }
    public String get501() { return "501 Not Implemented"; }
    public String get505() { return "505 HTTP Version Not Supported"; }
}