package posl.engine.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import posl.engine.error.PoslException;
import posl.engine.type.SingleStatement;

public class Resolver  {
	
protected Type[] params;
	
	protected Annotation[][] annotations;
	
	protected ParameterInfo[] info;
	
	
	public void populate(Method method){
		this.params = method.getGenericParameterTypes();
		this.annotations = method.getParameterAnnotations();
		info = new ParameterInfo[params.length];
		for (int i = 0;i < params.length ; i++){
			info[i] = new ParameterInfo(params[i], annotations[i]);
		}
	}
	

	/**
	 * 
	 * 
	 * 
	 * @param scope provides the available set of objects to work with
	 * @param statement is the executable statement
	 * @return
	 * @throws PoslException
	 */
	public Object[] render(Scope scope, SingleStatement statement) throws PoslException {
		// This is the argument array that will be passed in the method call
		Object[] arguments = new Object[info.length];
		// we're going to loop through the parameter information
		// i is the index for the parameter information
		// t is the index for the passed in tokens.
		int tokenIndex = 1;
		int numberOfTokens = statement.size() - 1; // length of tokens passed in
		for (int paramIndex = 0; paramIndex < info.length; ++paramIndex) {
			ParameterInfo param = info[paramIndex];
			// first check to see if we've ran out of arguments
			if (tokenIndex > numberOfTokens) {
				if (!param.isOptional()) {
					throw new PoslException(statement.startPos(),
							"incorrect number of arguments");
				}
			} else {
				// we have enough arguments. do we need them?
				arguments[paramIndex] = param.render(scope, statement, tokenIndex);
			}
			// do we increment?
			tokenIndex += param.incr();
		}
		return arguments;
	}

}
