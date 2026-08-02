package org.example.Checks;

import org.example.Request;
import org.example.RequestProcessor;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Negotiation extends RequestProcessor {

    public Negotiation() {}

    public String checkNegotiation(Request request, Properties props) {
        // run through checks similarly to ResponseHandler.java using findPreferredFile()...
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
     * Using the headers pertaining to "Accept" and "Negotiation", will return a filename with the highest match or
     * a status code for errors.
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
        Iterator<String> iter = keys.iterator();
        checkFileExists(props, fileName, iter);

        // account for language
        if (preferences.isEmpty()) {
            preferences.put("Negotiate-Language", 1.0);
            preferences = associateStar(preferences, props);
            iter = keys.iterator();
            checkFileExists(props, fileName, iter);
        }

        // remove from preferences during loop of finding greatest q-value(s)
        int counter = 0;
        while (counter <= preferences.size()) {

            counter++;
            LinkedList<Double> qVals = new LinkedList<>(preferences.values());

            // Find the maximum value, get its index and remove, then read to first index
            double maxVal = Collections.max(qVals);
            int indexOfMax = qVals.indexOf(maxVal);
            qVals.remove(indexOfMax);
            qVals.addFirst(maxVal);

            if (Double.compare(maxVal, 0.0) == 0) { // if all vals are 0, there is no point in continuing
                return "406";
            }

            for (int i = 0; i < qVals.size(); i++) {

                if (qVals.size() <= (i + 1)) { // check limit
                    break;
                }

                double next = qVals.get(i + 1);
                if (Double.compare(maxVal, next) > 0 || Double.compare(next, 0.0) == 0) { // next is less than maxVal or 0
                    iter = keys.iterator();
                    while (iter.hasNext()) {
                        String key = iter.next();
                        if (Double.compare(preferences.get(key), next) == 0) {
                            iter.remove();
                            break;
                        }
                    }
                    qVals.remove(next);
                } else if (Double.compare(maxVal, next) == 0) { // equal to max value
                    break;
                }
            }
        }

        iter = keys.iterator();
        if (preferences.isEmpty()) {
            return "406";
        } else if (preferences.size() > 1) {

            List<String> lastMimes = keys.stream().toList();
            List<String> foundCurrently = new LinkedList<>();
            StringBuilder allFound = new StringBuilder();

            for (String propKey : props.stringPropertyNames()) {
                for (String lastMime : lastMimes) {
                    if (props.getProperty(propKey).equals(lastMime)) {
                        String ext = "." + propKey;
                        String foundThis = fileName + ext;

                        if (!(foundCurrently.contains(foundThis))) {
                            foundCurrently.add(foundThis);
                        }
                    }
                }
            }

            for (String s : foundCurrently) {
                allFound.append(s).append(" ");
            }
            return allFound.toString();
        } else {

            String lastMime = keys.stream().toList().getFirst();
            for (String propKey : props.stringPropertyNames()) {
                if (props.getProperty(propKey).equals(lastMime)) {
                    String ext = "." + propKey;
                    fileName = (fileName + ext);

                    try {
                        URI forPath = new URI(fileName);
                        Path testThisPath = Paths.get(
                                System.getProperty("user.dir"),
                                props.getProperty("DOCS_ROOT"),
                                forPath.toString());
                        if (!(testThisPath.toFile().exists())) {
                            return "406";
                        }
                    } catch (URISyntaxException e) {
                        System.err.println("Conversion to URI failed.");
                        return "406";
                    }
                }
            }
        }

        return fileName;
    }

    private void checkFileExists(Properties props, String fileName, Iterator<String> iter) {
        while (iter.hasNext()) {
            String key = iter.next();
            String extension;
            for (String propKey : props.stringPropertyNames()) {
                if (props.getProperty(propKey).equals(key)) {
                    extension = "." + propKey;
                    Path locateThis = Paths.get(
                            System.getProperty("user.dir"),
                            props.getProperty("DOCS_ROOT"),
                            fileName + extension);
                    if (!locateThis.toFile().exists()) {
                        iter.remove();
                    }
                }
            }
        }
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
