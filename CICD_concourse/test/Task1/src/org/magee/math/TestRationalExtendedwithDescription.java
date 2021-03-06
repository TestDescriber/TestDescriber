/*
 * This file was automatically generated by EvoSuite
 */
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

public class TestRationalExtendedwithDescription {

    @Test
    public void test0() throws Throwable {
        // To Complete : MethodDescriptor.variableDeclarationDescription(...)
        for (long i = 1; i < 5; i++) {
            Rational rational0 = new  Rational((-1 * i), (-1 * i));
            // The test case declares an object of the class "Rational"     whose value is equal to the absolute value of "rational0"
            Rational rational1 = rational0.abs();
            // Then, it tests:
            //1) whether the integer value of "rational1" is equal to -1;
            assertEquals(-1, rational1.intValue());
            //2) whether the denominator of rational1 is equal to (-1 * i);
            assertEquals((-1 * i), rational1.denominator);
        }
    }
}

