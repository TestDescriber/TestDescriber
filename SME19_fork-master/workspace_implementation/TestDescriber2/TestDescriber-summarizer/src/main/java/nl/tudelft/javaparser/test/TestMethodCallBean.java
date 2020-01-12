package nl.tudelft.javaparser.test;

import static org.junit.Assert.*;
import nl.tudelft.javaparser.MethodCallBean;
import org.junit.Test;

public class TestMethodCallBean {

	@Test
	public void test0() {
		String statement = "Option option0 = temp.getOption();";
		MethodCallBean bean = new MethodCallBean(statement);
		assertEquals("getOption",bean.getName());
		assertEquals("[]",bean.getParameters().toString());
		assertEquals("[]",bean.getParameterValue().toString());
	}
	
	@Test
	public void test1() {
		String statement = "Option option0 = temp.addOption(value);";
		MethodCallBean bean = new MethodCallBean(statement);
		assertEquals("addOption",bean.getName());
		assertEquals("[NameExpr]",bean.getParameters().toString());
		assertEquals("[value]",bean.getParameterValue().toString());
	}
	
	@Test
	public void test2() {
		String statement = "temp.setType(\"\");";
		MethodCallBean bean = new MethodCallBean(statement);
		assertEquals("setType",bean.getName());
		assertEquals("[String]",bean.getParameters().toString());
		assertEquals("[\"\"]",bean.getParameterValue().toString());
	}
	
	@Test
	public void test3() {
		String statement = "temp.setType(\"\", 1);";
		MethodCallBean bean = new MethodCallBean(statement);
		assertEquals("setType",bean.getName());
		assertEquals("[String, int]",bean.getParameters().toString());
		assertEquals("[\"\", 1]",bean.getParameterValue().toString());
	}

}
