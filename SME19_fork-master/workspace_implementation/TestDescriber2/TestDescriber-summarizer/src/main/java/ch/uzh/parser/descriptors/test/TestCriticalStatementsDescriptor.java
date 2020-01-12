package ch.uzh.parser.descriptors.test;

import static org.junit.Assert.*;

import org.junit.Test;

import ch.uzh.parser.descriptors.CriticalStatementsDescriptor;

public class TestCriticalStatementsDescriptor {

	@Test
	public void test0() {
		String text = "if (longOpt != null){}";
		String comment =CriticalStatementsDescriptor.parseIfStatement(text, false); 
		assertEquals("the long option is equal to null", comment);
	}
	
	@Test
	public void test1() {
		String text = "if (longOpt != null){}";
		String comment =CriticalStatementsDescriptor.parseIfStatement(text, true); 
		assertEquals("the long option is not equal to null", comment);
	}
	
	@Test
	public void test2() {
		String text = "if (hasArgs()){}";
		String comment =CriticalStatementsDescriptor.parseIfStatement(text, true); 
		assertEquals("the current object has arguments", comment);
	}
	
	@Test
	public void test3() {
		String text = "if (hasArgs()){}";
		String comment =CriticalStatementsDescriptor.parseIfStatement(text, false); 
		assertEquals("the current object has not arguments", comment);
	}

}
