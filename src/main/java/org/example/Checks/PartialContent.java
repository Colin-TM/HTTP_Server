package org.example.Checks;

import org.example.Request;
import org.example.RequestProcessor;

public class PartialContent extends RequestProcessor {

    public PartialContent() {}

    public String checkPartialContent(Request request) {
        String range = request.getHeader("Range:");
        
        return "200 OK";
    }

    private boolean checkInRange() {

        return true;
    }

    private boolean checkIsInt() {

        return true;
    }

}
