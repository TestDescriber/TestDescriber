/* ------------------------------
 * File: Main.java
 * Name: Dan Fleck
 * Assignment: N/A
 * Lab Section: All of them 
 * Creation Date: Nov 10, 2008
 * 
 * References:
 * Comments:
 * ------------------------------
 */

package nl.tudelft.junitexample;

/**
 *
 * @author dfleck
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Triangle t = new Triangle("12", "12", "12");
        System.out.println("t is:"+t.determineTriangleType());
    }

}
