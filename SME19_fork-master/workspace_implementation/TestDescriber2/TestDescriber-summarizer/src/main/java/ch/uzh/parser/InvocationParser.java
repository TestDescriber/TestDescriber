package ch.uzh.parser;

import ch.uzh.parser.bean.MethodBean;

public class InvocationParser {
	
	public static MethodBean parse(String pInvocationName) {
		MethodBean methodBean = new MethodBean();
		methodBean.setName(pInvocationName);
		return methodBean;
	}

}
