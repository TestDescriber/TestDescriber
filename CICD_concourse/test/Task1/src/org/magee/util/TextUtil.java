/**
 * TextUtil.java A class that provides miscellaneous text altering capabilities.
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
import java.util.StringTokenizer;

/**
 * The <code>TextUtil</code> class is for manipulating strings. It cannot be
 * instantiated.
 * <p>
 * 
 * It provides a few static methods for capitalizing the first letter of every
 * <code>word</code> of a user provided string. The option of converting all
 * other letters of the string to lowercase is also available. A
 * <code>word</code> is a string of text delimited by a white-space or a user
 * defined delimiter.
 * 
 * @author Anthony W. Magee
 * @version 1.0, July 18, 2001
 */

/*
 * version changes
 */
public class TextUtil {

    /**
     * Enum defining valid values for case alteration
     */
    public static enum LetterCase {
        IGNORE, LOWERCASE, UPPERCASE
    }

    /**
     * The default implementation of <code>titleCase</code>. The first letter
     * of every word will be capitalized; the remaining letters will be
     * unchanged. A <code>word</code> is delimited by a white-space. This
     * method replaces all white-spaces with a space.
     * 
     * @param str The <code>String</code> to be manipulated
     */
    public static String titleCase(String str) {
        return titleCase(str, LetterCase.IGNORE, " ");
    }

    /**
     * This overload will do the same as
     * <code>titleCase(java.lang.String)</code>, but gives the option of
     * changing the letters that are not the first in a word to lowercase.
     * <P>
     * <code>changeOption</code><BR>
     * IGNORE<BR>
     * LOWERCASE<BR>
     * UPPERCASE
     * 
     * @param str The <code>String</code> to be manipulated
     * @param changeOption The option to modify secondary letters
     */
    public static String titleCase(String str, LetterCase changeOption) {
        return titleCase(str, changeOption, " ");
    }

    /**
     * This overload will do the same as <code>titleCase(String, int)</code>,
     * but allows the user to supply a text delimiter. The delimiter is not
     * changed.
     * 
     * @param str The <code>String</code> to be manipulated
     * @param changeOption The option to modify secondary letters
     * @param delimit The delimiter to be used
     */
    public static String titleCase(String str, LetterCase changeOption, String delimit) {
        StringBuffer reply = new StringBuffer();
        StringTokenizer st = new StringTokenizer(str, delimit);
        int tokenCount = 1;
        int totalTokens = st.countTokens();
        while(st.hasMoreTokens()) {
            char[] c = st.nextToken().toCharArray();
            c[0] = Character.toUpperCase(c[0]);

            switch(changeOption) {
                case IGNORE: // option to ignore secondary letters
                    reply.append(new String(c));
                    break;
                case LOWERCASE: // option to lowercase secondary letters
                    for(int i = 1; i < c.length; i++) {
                        c[i] = Character.toLowerCase(c[i]);
                    }
                    reply.append(new String(c));
                    break;
                case UPPERCASE: // option to uppercase secondary letters
                    for(int i = 1; i < c.length; i++) {
                        c[i] = Character.toUpperCase(c[i]);
                    }
                    reply.append(new String(c));
                    break;
                default: // throw exception if user provived irregular value
                    throw new RuntimeException("changeOption must be one of the following: TextUtil.IGNORE, TextUtil.LOWERCASE, TextUtil.UPPERCASE");
            }

            if(tokenCount < totalTokens)
                reply.append(delimit);
            tokenCount++;
        }

        return reply.toString();
    }
}