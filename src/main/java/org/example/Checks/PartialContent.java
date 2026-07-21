package org.example.Checks;

import org.example.Request;
import org.example.RequestProcessor;

public class PartialContent extends RequestProcessor {

    private boolean rangeAdjusted = false;
    public PartialContent() {}

    public String checkPartialContent(Request request, int contentLength) {
        int[] range = parseRange(request, contentLength);

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
            return new int[]{0, 0};
        }

        return range;
    }

    private boolean checkIsInt(String[] range, int contentLength) {
        // may not need?
        return true;
    }

    private boolean checkInRange() {
        // may not need?
        return true;
    }

    public void setRangeAdjusted(boolean to) {
        this.rangeAdjusted = to;
    }

    public boolean getRangeAdjusted() {
        return this.rangeAdjusted;
    }

}
