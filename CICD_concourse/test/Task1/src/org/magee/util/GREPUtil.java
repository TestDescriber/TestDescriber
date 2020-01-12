/**
 * GREPUtil.java
 */

/* This file is part of Math4J.
 * Math4J is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Math4J is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Math4J; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 * 
 * Copyright 2005 Anthony Magee
 */

package org.magee.util;

// import other packages
import java.util.regex.Pattern;

/**
 * This class contains some commonly used regular expression tests.
 * 
 * @author Anthony W. Magee
 * @version 1.0, July 28, 2005
 */
public class GREPUtil {

    private static final String IP_RE = "\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b";

    private static final String EMAIL_RE = "\\b[a-zA-Z0-9._%-]+@[a-zA-Z0-9._%-]+\\.[a-zA-Z]{2,4}\\b";

    private static final String FLOAT_RE = "[-+]?([0-9]*\\.)?[0-9]+([eE][-+]?[0-9]+)?";

    /**
     * Determines if the search string contains a valid IP address
     * 
     * @param canidate The string to search.
     * @return true if, and only if, the entire region sequence matches this
     *         matcher's IP pattern
     */
    public static boolean validIP(String canidate) {
        return Pattern.matches(IP_RE, canidate);
    }

    /**
     * Determines if the search string contains a valid Email address
     * 
     * @param canidate The string to search.
     * @return true if, and only if, the entire region sequence matches this
     *         matcher's Email pattern
     */
    public static boolean validEmail(String canidate) {
        return Pattern.matches(EMAIL_RE, canidate);
    }

    /**
     * Determines if the search string contains a valid floating point number,
     * including scientific notation
     * 
     * @param canidate The string to search.
     * @return true if, and only if, the entire region sequence matches this
     *         matcher's float pattern
     */
    public static boolean validFloat(String canidate) {
        return Pattern.matches(FLOAT_RE, canidate);
    }

    static final long serialVersionUID = 1L;
}
