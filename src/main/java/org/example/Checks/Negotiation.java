package org.example.Checks;

import org.example.Request;
import org.example.RequestProcessor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

public class Negotiation extends RequestProcessor {

    public Negotiation() {}

    public String checkNegotiation(Request request, Properties props) {



        return "200 OK";
    }

    /**
     * Parses acceptType for file-types and q-values.
     * @param acceptType - "Accept:", "Accept-Language:", "Accept-Encoding:", "Accept-Charset:"
     * @return - a map of the file-types and respective q-values
     */
    protected HashMap<String, Double> getPreferences(String acceptType) {
        HashMap<String, Double> preferences = new HashMap<>();
        String[] splitLine = acceptType.split(", ");
        try {
            for (String s : splitLine) {
                String[] toPreferences = s.split("; q=");
                preferences.put(toPreferences[0], Double.parseDouble(toPreferences[1]));
            }
        } catch (IndexOutOfBoundsException e) {
            return preferences;
        }

        return preferences;
    }

    /**
     * Using the "Accept:" header's preference map, the method will return the highest rated matched file.
     * @return - matched file
     */
    public String findPreferredFile(Request request, String fileName, String acceptType, Properties props) {
        // extension given and file exists
        //if (getFilePath().toFile().exists()) {
        //    return fileName;
        //}

        // determine preference type
        HashMap<String, Double> preferences = new HashMap<>();
        if (acceptType.equals("Accept")) {
            preferences = associateStar(getPreferences(request.getHeader("Accept:")), props);
        } else if (acceptType.equals("Encoding")) {
            preferences = getPreferences(request.getHeader("Accept-Encoding:"));
        } else if (acceptType.equals("Language")) {
            preferences = getPreferences(request.getHeader("Accept-Language:"));
        } else if (acceptType.equals("Charset")) {
            preferences = getPreferences(request.getHeader("Accept-Charset:"));
        } else {
            return "500 Internal Server Error";
        }

        // in-case of negotiate being filled and accept being empty
        if (preferences.isEmpty()) {
            preferences.put("Negotiate", 1.0);
            preferences = associateStar(preferences, props);
        }

        // check for valid MIME-types
        Set<String> keys = preferences.keySet();
        keys.removeIf(key -> !props.containsValue(key));

        // check existence when filename is appended, save valid names
        

        return "";
    }

    /**
     * Headers involving stars will be run through this method to determine which file extensions should be included
     * in matchmaking and what values they should be given.
     * @param preferences - map of existing extensions and q-values
     * @param props - needed to parse through allowed file-types
     * @return - the map of extensions and q-values
     */
    protected HashMap<String, Double> associateStar(HashMap<String, Double> preferences, Properties props) {
        if (preferences.containsKey("application/*")) {

            preferences.put(props.getProperty("octet-stream"), preferences.get("application/*"));
            preferences.put(props.getProperty("word"), preferences.get("application/*"));
            preferences.put(props.getProperty("ppt"), preferences.get("application/*"));
            preferences.remove("application/*");

        }

        if (preferences.containsKey("image/*")) {

            preferences.put(props.getProperty("gif"), preferences.get("image/*"));
            preferences.put(props.getProperty("jpeg"), preferences.get("image/*"));
            preferences.put(props.getProperty("png"), preferences.get("image/*"));
            preferences.remove("image/*");

        }

        if (preferences.containsKey("text/*")) {

            preferences.put(props.getProperty("xml"), preferences.get("text/*"));
            preferences.put(props.getProperty("html"), preferences.get("text/*"));
            preferences.put(props.getProperty("txt"), preferences.get("text/*"));
            preferences.remove("text/*");

        }

        if (preferences.containsKey("*/*")) {

            preferences.put(props.getProperty("octet-stream"), preferences.get("application/*"));
            preferences.put(props.getProperty("word"), preferences.get("application/*"));
            preferences.put(props.getProperty("ppt"), preferences.get("application/*"));
            preferences.put(props.getProperty("gif"), preferences.get("image/*"));
            preferences.put(props.getProperty("jpeg"), preferences.get("image/*"));
            preferences.put(props.getProperty("png"), preferences.get("image/*"));
            preferences.put(props.getProperty("xml"), preferences.get("text/*"));
            preferences.put(props.getProperty("html"), preferences.get("text/*"));
            preferences.put(props.getProperty("txt"), preferences.get("text/*"));
            preferences.remove("*/*");

        }

        if (preferences.containsKey("Negotiate")) {

            preferences.put(props.getProperty("octet-stream"), 1.0);
            preferences.put(props.getProperty("word"), 1.0);
            preferences.put(props.getProperty("ppt"), 1.0);
            preferences.put(props.getProperty("gif"), 1.0);
            preferences.put(props.getProperty("jpeg"), 1.0);
            preferences.put(props.getProperty("png"), 1.0);
            preferences.put(props.getProperty("xml"), 1.0);
            preferences.put(props.getProperty("html"), 1.0);
            preferences.put(props.getProperty("txt"), 1.0);
            preferences.remove("Negotiate");

        }

        if (preferences.containsKey("Negotiate-Language")) {

            preferences.put(props.getProperty("en"), 1.0);
            preferences.put(props.getProperty("es"), 1.0);
            preferences.put(props.getProperty("de"), 1.0);
            preferences.put(props.getProperty("ja"), 1.0);
            preferences.put(props.getProperty("ko"), 1.0);
            preferences.put(props.getProperty("ru"), 1.0);
            preferences.remove("Negotiate-Language");

        }

        return preferences;
    }
}
