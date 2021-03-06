/*
 * JUnit test for the Triangle class
 * This class works inside Netbeans -- See JUnit documentation for more info
 * http://www.junit.org/
 */
package nl.tudelft.junitexample;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;

/**
 *
 * @author dfleck
 */
public class TriangleTest {
    // Create a new Triangle
    public TriangleTest() {
    }

    @Test
    public void testScalene() {
        System.out.println("testScalene");

        Triangle instance = new Triangle("30", "40", "50");
        String expResult = "Scalene";
        String result = instance.determineTriangleType();
        assertEquals(expResult, result);
    }

    @Test
    public void testEquilateral() {
        System.out.println("testEquilateral");
        Triangle instance = new Triangle("1", "1", "1");
        String expResult = "Equilateral";
        String result = instance.determineTriangleType();
        assertEquals(expResult, result);
    }

    @Test
    public void testIsoceles() {
        System.out.println("testIsoceles");
        Triangle instance = new Triangle("30", "40", "30");
        String expResult = "Isosceles";
        String result = instance.determineTriangleType();
        assertEquals(expResult, result);
    }

    @Test
    public void testGiantTriangle() {
        System.out.println("testGiantTriangle");
        Triangle instance = new Triangle("3000000", "4000000", "3000000");
        String expResult = "I feel your triangle is too big\n";
        String result = instance.determineTriangleType();
        assertEquals(expResult, result);
    }
}
