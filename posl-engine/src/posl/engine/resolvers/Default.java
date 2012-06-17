package posl.engine.resolvers;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import posl.engine.annotation.Optional;
import posl.engine.api.AArgumentHandler;
import posl.engine.core.Scope;
import posl.engine.error.PoslException;
import posl.engine.type.Statement;

public class Default extends AArgumentHandler {

	public Object[] render(Scope scope, Statement tokens) throws PoslException {
		Object[] arguments = new Object[params.length];// subtract one to remove leading
		for (int i = 0; i < params.length; ++i){
			try {
				arguments[i] = scope.get(params[i],tokens.get(i+1));
			} catch (IndexOutOfBoundsException ioobe){
				loop : {
					for (Annotation annotation:annotations[i]){
						if (annotation instanceof Optional){
						    break loop;	
						}
					}
					throw new PoslException(tokens.getLineNumber(),"incorrect number of arguments");
				}
			}
		}
		System.out.println(Arrays.toString(arguments));
		return arguments;
	}

}
