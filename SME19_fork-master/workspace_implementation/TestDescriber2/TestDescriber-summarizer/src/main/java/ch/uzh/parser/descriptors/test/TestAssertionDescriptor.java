package ch.uzh.parser.descriptors.test;

import static org.junit.Assert.*;

import org.junit.Test;

import ch.uzh.parser.bean.MethodBean;
import ch.uzh.parser.descriptors.AssertionDescriptor;

public class TestAssertionDescriptor {

	@Test
	public void test0() {
		String text = "assertEquals(1.0, double0);";
		MethodBean testCase = new MethodBean();
		testCase.setCounterAssertions(1);
		String description = AssertionDescriptor.generateAssertionsComments(text,null, testCase, null);
		assertEquals(" //2) whether \"double0\" is equal to 1.0;\n", description);
	}
	
	@Test
	public void test1() {
		String text = "assertNotEquals(double1, double0);";
		MethodBean testCase = new MethodBean();
		testCase.setCounterAssertions(1);
		String description = AssertionDescriptor.generateAssertionsComments(text,null, testCase, null);
		assertEquals(" //2) whether \"double0\" is not equal to double1;\n", description);
	}
	
	@Test
	public void test2() {
		String text = "assertNotNull(double0);";
		MethodBean testCase = new MethodBean();
		testCase.setCounterAssertions(1);
		String description = AssertionDescriptor.generateAssertionsComments(text,null, testCase, null);
		assertEquals(" //2) whether \"double0\" is not null;\n", description);
	}

	@Test
	public void test3() {
		String text = "assertEquals(-291L,rational1.denominator);";
		MethodBean testCase = new MethodBean();
		testCase.setCounterAssertions(1);
		String description = AssertionDescriptor.generateAssertionsComments(text, null, testCase, null);
		assertEquals(" //2) whether the denominator of rational1 is equal to -291L; \n", description);
	}

}
