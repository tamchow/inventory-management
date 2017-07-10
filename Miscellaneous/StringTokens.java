package Miscellaneous;

/**
 * utility class which aids in splitting a string into its component tokensas per the given delimiter(s).
 */
public class StringTokens {
    private String argument = null;//string to process
    private char[] delimiters = null;//list of supplied delimiter characters
    private String[] split = null;//list of processed tokens
    private int returnedCount;//counter for tokens returned to caller

    /**
     * constructor which accepts string to process and given delimiter set.
     */
    public StringTokens(String s, String delimiter) {
        argument = s + delimiter.charAt(0);
        delimiters = new char[delimiter.length()];
        returnedCount = 0;
        for (int i = 0; i < delimiter.length(); i++)
            delimiters[i] = delimiter.charAt(i);
        split = new String[splitCounter()];
        checkSplits();
    }

    /**
     * internal method for checking if a character is a delimiter or not
     */
    private boolean isSplitter(char c) {
        for (char delimiter : delimiters) {
            if (c == delimiter)
                return true;
        }
        return false;
    }

    /**
     * internal method for counting the number of tokens
     */
    public int splitCounter() {
        int count = 0;
        for (int i = 0; i < argument.length(); i++) {
            if (isSplitter(argument.charAt(i)))
                count++;
        }
        return count;
    }

    /**
     * internal method for processing argument string into tokens and populating the 'split' array
     */
    private void checkSplits() {
        int j = 0, k = 0;
        for (int i = 0; i < argument.length(); i++) {
            char c = argument.charAt(i);
            if (isSplitter(c) && k < split.length) {
                split[k] = argument.substring(j, i);
                j = i + 1;
                k++;
            }
        }
    }

    /**
     * method by which the caller can know if there are more tokens to be accessed or not
     */
    public boolean hasMoreTokens() {
        try {
            return returnedCount != splitCounter();
        } catch (NullPointerException e) {
            return false;
        }
    }

    /**
     * returns trimmed versions of the tokens one at a time in sequential order to the caller
     */
    public String nextToken() {
        String tmp = split[returnedCount];
        returnedCount++;
        return tmp.trim();
    }
}