package posl.engine.lexeme;

import java.util.List;
import java.util.Stack;

import posl.engine.api.Collector;
import posl.engine.api.Lexeme;
import posl.engine.api.BasicToken;
import posl.engine.api.Token;
import posl.engine.api.TokenVisitor;
import posl.engine.core.Stream;
import posl.engine.type.ListContainer;
import posl.engine.type.MultiLineStatement;
import posl.engine.type.SingleStatement;

public class Grammar implements Lexeme {

	@Override
	public boolean consume(List<Token> tokens, Stream ps) {
		ps.setMark();
		boolean grammar = false;
		switch (ps.val()){
		case '{':
		case '}':
		case '(':
		case ')':
		case '[':
		case ']':
			grammar = true;
		}
		if (grammar){
			tokens.add(new Inner((char)ps.pop(), ps.getMark()));
		}
		return grammar;
	}
	
	
	private class Inner extends BasicToken {
		
		private char charValue;
		
		public Inner(char value, int i) {
			this.charValue = value;
			this.value = Character.toString(value);
			this.startPos = i;
			this.endPos = i + 1;
		}
		
		@Override
		public Collector consume(Collector statement, Stack<Collector> statements,
				Stack<Character> charStack) {
			switch (charValue) {
			case '[':
				charStack.push(']');
				statements.push(statement);
				statement = new SingleStatement(startPos);
				break;
			case '(':
				charStack.push(')');
				statements.push(statement);
				statement = new ListContainer();
				break;
			case '{':
				charStack.push('}');
				statements.push(statement);
				statement = new MultiLineStatement();
				break;
			case ')':
				if (!charStack.empty() && charStack.pop() == charValue){
					Collector temp = statement;
					statement = statements.pop();
					statement.add((List<?>)temp.get());
				} else {
					//throw new PoslException(lineNumber,"could not match parenthesis");
				}
				break;
			case ']':
				if (!charStack.empty() && charStack.pop() == charValue){
					Object temp = statement;
					statement = statements.pop();
					statement.add(temp);
				} else {
					//throw new PoslException(lineNumber,"could not match square bracket");
				}
				break;
			case '}':
				if (!charStack.empty() && charStack.pop() == charValue){
					statement.finish();
					Object temp = statement;
					statement = statements.pop();
					statement.add(temp);
				} else {
					//throw new PoslException(lineNumber,"could not match brace");
				}
				break;
			}
			return statement;
		}
		
		@Override
		public void accept(TokenVisitor visitor) {
			visitor.visitGrammar(this);
		}
		
	}

}
