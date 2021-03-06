package posl.engine.lexeme;

import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import posl.engine.api.Collector;
import posl.engine.api.Lexeme;
import posl.engine.api.Token;
import posl.engine.api.TokenVisitor;
import posl.engine.core.BasicToken;
import posl.engine.type.Atom;

/**
 * 
 * 
 * 
 * @author jebailey
 *
 */
public class Special implements Lexeme {
	
	Pattern pattern = Pattern.compile("\\S+");
	
	@Override
	public int consume(List<Token> tokens, CharSequence ps, int offset) {
		int totalCaptured = 0;
		Matcher matcher = pattern.matcher(ps);
		matcher.region(offset, ps.length());
		if (matcher.lookingAt()) {
			String s = matcher.group();
			tokens.add(new Inner(s, offset, matcher.end()));
			totalCaptured = s.length();
		}
		return totalCaptured;
	}

	
	private class Inner extends BasicToken {
		
		public Inner(String value, int i, int end) {
			this.value = new Atom(value);
			this.startPos = i;
			this.endPos = i + value.length();
		}
		
		
		@Override
		public Collector consume(Collector statement, Stack<Collector> statements,
				Stack<Character> charStack) {
			statement.add(value);
			return statement;
		}
		
		@Override
		public void accept(TokenVisitor visitor) {
			visitor.visitIdentifier(this);
		}

	}




}
