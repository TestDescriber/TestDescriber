package ch.uzh.parser.descriptors.test;

import static org.junit.Assert.*;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import ch.uzh.parser.descriptors.Descriptor;

public class TestDescriptor {

	@Test
	public void test0() {
		String text = "hasValue";
		String[] words = Descriptor.splitAndExpandIdentifier(text);
		assertEquals(2, words.length);
	}
	
	@Test
	public void test1() {
		String text = "getID";
		String[] words = Descriptor.splitAndExpandIdentifier(text);
		assertEquals(2, words.length);
	}
	
	@Test
	public void test2() {
		String text = "getID";
		String[] words = Descriptor.splitAndExpandIdentifier(text);
		assertEquals(2, words.length);
	}
}
