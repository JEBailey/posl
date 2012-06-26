package posl.lang;

import posl.engine.annotation.Command;
import posl.engine.core.Scope;
import posl.engine.type.MultiLineStatement;
import posl.engine.type.PList;
import posl.engine.type.Reference;
import posl.lang.executable.Function;

public class CoreFunction {
	

	// executable creation commands
	@Command("function")
	public static Object run(Scope scope, Reference functionName, PList argList,
			MultiLineStatement functionBody) {
		//Function func = new Function(argList, functionBody, scope);
		functionName.put(new Function(argList, functionBody, scope));
		return functionName.getValue();
	}


}
