package org.magee.math;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.magee.math.Rational;



public class TestRationalw {

	@Test
	public void test0() throws Throwable {
		Rational rational0 = new Rational((-450L), (-450L));
		Rational rational1 = rational0.abs();
		assertEquals(-1, rational1.intValue());
		assertEquals((-450L), rational1.denominator);
	}

	@Test
	public void test1() throws Throwable {
		Rational rational0 = new Rational((-848L), 2326L);
		Rational rational1 = rational0.abs();
		assertEquals(0.3645743766122098, rational1.doubleValue(), 0.01D);
		assertEquals(848L, rational1.numerator);
	}

	@Test
	public void test2() throws Throwable {
		Rational rational0 = new Rational(1L, 2338L);
		Rational rational1 = rational0.abs();
		assertEquals(1L, rational0.numerator);
		assertEquals(4.27716E-4F, rational1.floatValue(), 0.01F);
	}

	@Test
	public void test3() throws Throwable {
		Rational rational0 = new Rational((-1L), 140L);
		try {
			rational0.add((-626L));
			fail("Expecting exception: NumberFormatException");
		} catch (NumberFormatException e) {
	    }
	}

	

}
