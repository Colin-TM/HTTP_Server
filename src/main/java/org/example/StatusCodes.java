package org.example;

public class StatusCodes {

    private String currentStatus;
    public StatusCodes () { this.currentStatus = ""; };

    // Get different status codes
    public String getCurrentStatus() { return this.currentStatus; }
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

    // Set current status
    public void setCustom(String differentCode) { this.currentStatus = differentCode; }
    public void setTo200() { this.currentStatus=get200(); }
    public void setTo201() { this.currentStatus=get201(); }
    public void setTo206() { this.currentStatus=get206(); }
    public void setTo300() { this.currentStatus=get300(); }
    public void setTo301() { this.currentStatus=get301(); }
    public void setTo302() { this.currentStatus=get302(); }
    public void setTo304() { this.currentStatus=get304(); }
    public void setTo400() { this.currentStatus=get400(); }
    public void setTo401() { this.currentStatus=get401(); }
    public void setTo403() { this.currentStatus=get403(); }
    public void setTo404() { this.currentStatus=get404(); }
    public void setTo405() { this.currentStatus=get405(); }
    public void setTo406() { this.currentStatus=get406(); }
    public void setTo408() { this.currentStatus=get408(); }
    public void setTo411() { this.currentStatus=get411(); }
    public void setTo412() { this.currentStatus=get412(); }
    public void setTo413() { this.currentStatus=get413(); }
    public void setTo414() { this.currentStatus=get414(); }
    public void setTo416() { this.currentStatus=get416(); }
    public void setTo500() { this.currentStatus=get500(); }
    public void setTo501() { this.currentStatus=get501(); }
    public void setTo505() { this.currentStatus=get505(); }
}