package posl.engine.lexeme;

import java.util.List;
import java.util.Stack;

import posl.engine.api.ALexeme;
import posl.engine.api.IStatement;
import posl.engine.api.IToken;
import posl.engine.api.TokenVisitor;
import posl.engine.core.PoslStream;
import posl.engine.type.Statement;

public class Eol extends ALexeme {

	@Override
	public boolean consume(List<IToken> tokens, PoslStream ps) {
		ps.mark();
		if (ps.val() == -1){
			tokens.add(new Inner(ps));
			return false;
		}
		while (ps.val() == '\n'){
			ps.pop();
			return tokens.add(new Inner(ps));
		}
		return false;
	}
	
	private class Inner extends IToken {
		
		
		
		public Inner(PoslStream ps){
			this.value = "\n";
			this.startPos = ps.getMark();
			this.endPos = ps.getMark();
		}
		
		public IStatement consume(IStatement statement, Stack<IStatement> statements,
				Stack<Character> charStack) {
			if (!statement.isMultiLine()){
				if (statement.notEmpty()){
					statements.add((Statement)statement);
				}
				return new Statement(statement.endLineNumber()+1);
			} else {
				statement.addEol();
				return statement;
			}
		}
		
		@Override
		public void accept(TokenVisitor visitor) {
			visitor.visitEol(this);
		}

	}

}
