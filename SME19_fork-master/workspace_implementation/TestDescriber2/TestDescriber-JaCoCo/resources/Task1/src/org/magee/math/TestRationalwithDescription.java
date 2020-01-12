package org.magee.math;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.magee.math.Rational;

/** 
 * The main class under test is Rational.
 *  It describes a single rational and maintains information regarding: 
 *  - the numerator of the rational;
 *  - the denominator of the rational;
 * */

public class TestRationalwithDescription {

    @Test
    public void test0() throws Throwable {
        // The test case instantiates a "Rational" with numerator equal to -450L, and denominator equal to -450L.
        // The execution of this constructor implicitly covers the following 1 conditions:
        // - the condition  " denominator equals to 0L" is FALSE;
        Rational rational0 = new  Rational((-450L), (-450L));
        // The test case declares an object of the class "Rational"     whose value is equal to the absolute value of "rational0"
        Rational rational1 = rational0.abs();
        // Then, it tests:
        //1) whether the integer value of "rational1" is equal to -1;
        assertEquals(-1, rational1.intValue());
        //2) whether the denominator of rational1 is equal to (-450L);
        assertEquals((-450L), rational1.denominator);
    }

    @Test
    public void test1() throws Throwable {
        // The test case instantiates a "Rational" with numerator equal to -848L, and denominator equal to 2326L.
        // The execution of this constructor implicitly covers the following 1 conditions:
        // - the condition  " denominator equals to 0L" is FALSE;
        Rational rational0 = new  Rational((-848L), 2326L);
        // The test case declares an object of the class "Rational"     whose value is equal to the absolute value of "rational0"
        Rational rational1 = rational0.abs();
        // Then, it tests: //1) whether the double value of "rational1" is equal to 0.3645743766122098 with delta equal to 0.01D;
        assertEquals(0.3645743766122098, rational1.doubleValue(), 0.01D);
        //2) whether the numerator of rational1 is equal to 848L;
        assertEquals(848L, rational1.numerator);
    }

    @Test
    public void test2() throws Throwable {
        // The test case instantiates a "Rational" with numerator equal to 1L, and denominator equal to 2338L.
        // The execution of this constructor implicitly covers the following 1 conditions:
        // - the condition  " denominator equals to 0L" is FALSE;
        Rational rational0 = new  Rational(1L, 2338L);
        // The test case declares an object of the class "Rational"     whose value is equal to the absolute value of "rational0"
        Rational rational1 = rational0.abs();
        // Then, it tests:
        //1) whether the numerator of rational0 is equal to 1L;
        assertEquals(1L, rational0.numerator);
        //2) whether the float value of "rational1" is equal to 4.27716E-4F with delta equal to 0.01F;
        assertEquals(4.27716E-4F, rational1.floatValue(), 0.01F);
    }

    @Test
    public void test3() throws Throwable {
        // The test case instantiates a "Rational" with numerator equal to -1L, and denominator equal to 140L.
        // The execution of this constructor implicitly covers the following 1 conditions:
        // - the condition  " denominator equals to 0L" is FALSE;
        Rational rational0 = new  Rational((-1L), 140L);
        try {
            //  The next method call the add long integer -626L to rational0
            rational0.add((-626L));
            fail("Expecting exception: NumberFormatException");
        } catch (NumberFormatException e) {
        }
    }
}

