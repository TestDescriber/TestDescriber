/**
 * NetUtil.java
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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class contains simple methods for returning network information.
 * 
 * @author Anthony W. Magee &#169 2005
 * @version 1.0, July 20, 2005
 */
public class NetUtil {

    public static String getLocalIP() {
        String localIP = "0.0.0.0";
        try {
            localIP = InetAddress.getLocalHost().getHostAddress();
        } catch(UnknownHostException uhe) {
            System.out.println("IP for the local host could not be found.");
            uhe.printStackTrace();
        }

        return localIP;
    }

    public static String getPublicIP() {
        String result = "0.0.0.0";
        BufferedReader in = null;
        String inputLine = "";
        Pattern p = Pattern.compile("\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b"); 
        // this matches ip addresses of the form ddd.ddd.ddd.ddd
        Matcher m = p.matcher("");

        try {
            URL ip = new URL("http://www.whatismyip.com/");
            in = new BufferedReader(new InputStreamReader(ip.openStream()));

            boolean found = false;
            while(!found) {
                inputLine = in.readLine();
                m = p.matcher(inputLine);
                if(m.find()) {
                    result = m.group();
                    found = true;
                }
            }

        } catch(IOException ioe) {
            System.out.println("The data cannot be accessed from the URL.");
            ioe.printStackTrace();
        } finally {
            try {
                in.close();
            } catch(IOException ioe2) {
            }// ignore this error
        }

        return result;
    }

    // common usage MacPing("192.168.10.10", 1, System.out)
    public static void MacPing(String ip, int packets, PrintStream stream) {
        if(System.getProperty("os.name").toLowerCase().startsWith("mac os x")) {
            if(GREPUtil.validIP(ip)) {
                Process p = null;
                BufferedReader in = null;
                try {
                    p = Runtime.getRuntime().exec(new String[] { "ping", "-c", Integer.toString(packets), ip });
                    in = new BufferedReader(new InputStreamReader(p.getInputStream()));

                    String lineIn = "";
                    while((lineIn = in.readLine()) != null)
                        stream.println(lineIn);
                } catch(IOException ioe) {
                    System.out.println("Exception while executing ping.");
                    ioe.printStackTrace();
                } finally {
                    try {
                        if(in != null)
                            in.close();
                    } catch(IOException ioe2) {
                    }//
                }
            } else
                throw new RuntimeException("IP address passed in is invalid.");
        } else
            throw new RuntimeException("MacPing will only work on an Apple computer running 10.0 or greater.");
    }
}