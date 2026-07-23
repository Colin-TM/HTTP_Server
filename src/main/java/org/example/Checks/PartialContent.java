package org.example.Checks;

import org.example.Request;
import org.example.RequestProcessor;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class PartialContent extends RequestProcessor {

    private boolean rangeAdjusted = false;
    public PartialContent() {}

    public String checkPartialContent(Request request, int contentLength, String lastModified) {

        if (!request.getHeader("Range:").isEmpty()) {
            int[] range = parseRange(request, contentLength);
            if (range[0] == -1) {
                return "500 Internal Server Error";
            }

            String status = checkInRange(range, contentLength);
            if (status.equals("416 Requested Range Not Satisfiable")) {
                return status;
            }

            status = checkIfRange(request.getHeader("If-Range:"), lastModified);
            if (status.equals("412 Precondition Failed")) {
                return status;
            }

            return "206 Partial Content";
        }

        return "200 OK";
    }

    private int[] parseRange(Request request, int contentLength) {
        String[] vals = request.getHeader("Range:").substring(6).split("-");
        int[] range = new int[2];

        try {
            if (vals[1].isEmpty() || Integer.parseInt(vals[1]) > contentLength) {
                range[1] = contentLength - 1;
                setRangeAdjusted(true);
            } else {
                range[1] = Integer.parseInt(vals[1]);
            }

            if (vals[0].isEmpty()) {
                range[0] = contentLength - Integer.parseInt(vals[1]);
                setRangeAdjusted(true);
            } else {
                range[0] = Integer.parseInt(vals[0]);
            }
        } catch (NumberFormatException e) {
            return new int[]{-1, -1};
        }

        return range;
    }

    private String checkInRange(int[] range, int contentLength) {
        if (range[0] > contentLength || range[1] > contentLength) {
            return "416 Requested Range Not Satisfiable";
        }

        return "206 Partial Content";
    }

    private String checkIfRange(String ifRange, String lastModified) {
        Instant headerDate = ZonedDateTime.parse(ifRange, DateTimeFormatter.RFC_1123_DATE_TIME).toInstant();
        Instant fileDate = ZonedDateTime.parse(lastModified, DateTimeFormatter.RFC_1123_DATE_TIME).toInstant();
        if (!fileDate.isAfter(headerDate)) {
            return "206 Partial Content";
        }

        return "412 Precondition Failed";
    }

    public void setRangeAdjusted(boolean to) {
        this.rangeAdjusted = to;
    }

    public boolean getRangeAdjusted() {
        return this.rangeAdjusted;
    }

}
