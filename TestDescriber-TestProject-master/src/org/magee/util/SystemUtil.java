/**
 * SystemUtil.java
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * 
 * @author Anthony Magee
 */
public class SystemUtil {

    public static String exec(String command) throws IOException {
        String ls = System.getProperty("line.separator");
        StringBuffer result = new StringBuffer();
        ArrayList<String> argsArray = new ArrayList<String>();
        StreamTokenizer st = new StreamTokenizer(new StringReader(command));
        st.resetSyntax();
        st.wordChars('a', 'z');
        st.wordChars('A', 'Z');
        st.wordChars('0', '9');
        st.wordChars('-', '-');
        st.wordChars('.', '.');
        st.wordChars('~', '~');
        st.wordChars(':', ':');
        st.wordChars('/', '/');
        st.wordChars('\\', '\\'); // the \ character must be escaped
        st.quoteChar('"');
        st.quoteChar('\'');
        st.whitespaceChars(' ', ' ');

        while(st.nextToken() != StreamTokenizer.TT_EOF) {
            argsArray.add(st.sval);
        }

        String[] args = new String[1];
        // System.out.println(argsArray.toString()); //check resulting array
        // here for validity
        Process p = Runtime.getRuntime().exec(argsArray.toArray(args));

        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

        String lineIn = "";
        while((lineIn = br.readLine()) != null) {
            result.append(lineIn);
            result.append(ls);
        }

        return result.toString();
    }

}
