package com.base.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Commonly used regular expression patterns.
 */
public class DMPatterns {

    /**
     * Good characters for Internationalized Resource Identifiers (IRI). This comprises most common used Unicode characters allowed in IRI as detailed in RFC 3987. Specifically, those two byte Unicode characters are not included.
     */
    public static final String GOOD_IRI_CHAR = "a-zA-Z0-9\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF";

    public static final Pattern IP_ADDRESS = Pattern
            .compile("((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
                    + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]"
                    + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}" + "|[1-9][0-9]|[0-9]))");

    /**
     * RFC 1035 Section 2.3.4 limits the labels to a maximum 63 octets.
     */
    private static final String IRI = "[" + GOOD_IRI_CHAR + "]([" + GOOD_IRI_CHAR + "\\-]{0,61}[" + GOOD_IRI_CHAR
            + "]){0,1}";

    private static final String GOOD_GTLD_CHAR = "a-zA-Z\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF";
    private static final String GTLD = "[" + GOOD_GTLD_CHAR + "]{2,63}";
    private static final String HOST_NAME = "(" + IRI + "\\.)+" + GTLD;

    public static final Pattern DOMAIN_NAME = Pattern.compile("(" + HOST_NAME + "|" + IP_ADDRESS + ")");

    /**
     * Regular expression pattern to match most part of RFC 3987 Internationalized URLs, aka IRIs. Commonly used Unicode characters are added.
     */
    public static final Pattern WEB_URL = Pattern
            .compile("((?:(http|https|Http|Https|rtsp|Rtsp):\\/\\/(?:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)"
                    + "\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,64}(?:\\:(?:[a-zA-Z0-9\\$\\-\\_"
                    + "\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,25})?\\@)?)?" + "(?:"
                    + DOMAIN_NAME + ")" + "(?:\\:\\d{1,5})?)" // plus option port number
                    + "(\\/(?:(?:[" + GOOD_IRI_CHAR + "\\;\\/\\?\\:\\@\\&\\=\\#\\~" // plus option query params
                    + "\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])|(?:\\%[a-fA-F0-9]{2}))*)?" + "(?:\\b|$)"); // and finally, a word boundary or end of
                                                                                                   // input. This is to stop foo.sure from
                                                                                                   // matching as foo.su

    public static final Pattern EMAIL_ADDRESS = Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
            + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");

    /**
     * This pattern is intended for searching for things that look like they might be phone numbers in arbitrary text, not for validating whether something is in fact a phone number. It will miss many things that are legitimate phone numbers.
     * <p>
     * The pattern matches the following:
     * <ul>
     * <li>Optionally, a + sign followed immediately by one or more digits. Spaces, dots, or dashes may follow.
     * <li>Optionally, sets of digits in parentheses, separated by spaces, dots, or dashes.
     * <li>A string starting and ending with a digit, containing digits, spaces, dots, and/or dashes.
     * </ul>
     */
    public static final Pattern PHONE = Pattern.compile( // sdd = space, dot, or dash
            "(\\+[0-9]+[\\- \\.]*)?" // +<digits><sdd>*
                    + "(\\([0-9]+\\)[\\- \\.]*)?" // (<digits>)<sdd>*
                    + "([0-9][0-9\\- \\.]+[0-9])"); // <digit><digit|sdd>+<digit>

    /**
     * Convenience method to take all of the non-null matching groups in a regex Matcher and return them as a concatenated string.
     * 
     * @param matcher
     *            The Matcher object from which grouped text will be extracted
     * @return A String comprising all of the non-null matched groups concatenated together
     */
    public static final String concatGroups(Matcher matcher) {
        StringBuilder b = new StringBuilder();
        final int numGroups = matcher.groupCount();

        for (int i = 1; i <= numGroups; i++) {
            String s = matcher.group(i);

            if (s != null) {
                b.append(s);
            }
        }

        return b.toString();
    }

    /**
     * Convenience method to return only the digits and plus signs in the matching string.
     * 
     * @param matcher
     *            The Matcher object from which digits and plus will be extracted
     * @return A String comprising all of the digits and plus in the match
     */
    public static final String digitsAndPlusOnly(Matcher matcher) {
        StringBuilder buffer = new StringBuilder();
        String matchingRegion = matcher.group();

        for (int i = 0, size = matchingRegion.length(); i < size; i++) {
            char character = matchingRegion.charAt(i);

            if (character == '+' || Character.isDigit(character)) {
                buffer.append(character);
            }
        }
        return buffer.toString();
    }

    /**
     * Do not create this static utility class.
     */
    private DMPatterns() {}
}
