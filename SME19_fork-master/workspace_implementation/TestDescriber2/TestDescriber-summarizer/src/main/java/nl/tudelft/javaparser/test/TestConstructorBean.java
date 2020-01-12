package nl.tudelft.javaparser.test;

import static org.junit.Assert.*;
import nl.tudelft.javaparser.ConstructorBean;
import nl.tudelft.javaparser.ExpressionUtils;

import org.junit.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;

public class TestConstructorBean {

	@Test
	public void test0() {
		String statement = "Option option0 = new  Option(\"\",\"\");";
			ConstructorBean bean = new ConstructorBean(statement);
			assertEquals("[String, String]",bean.getParameters().toString());
			assertEquals("[\"\", \"\"]",bean.getParameterValue().toString());

	}
	
	@Test
	public void test1() {
		String statement = "Option option0 = new  Option();";
			ConstructorBean bean = new ConstructorBean(statement);
			assertEquals("[]",bean.getParameters().toString());
			assertEquals("[]",bean.getParameterValue().toString());

		
	}
	
	@Test
	public void test2() {
		String statement = "arrayIntList0 = new ArrayIntList();";
		ConstructorBean bean = new ConstructorBean(statement);
		assertEquals("[]",bean.getParameters().toString());
		assertEquals("[]",bean.getParameterValue().toString());
	}

}
