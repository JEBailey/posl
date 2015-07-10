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
import posl.engine.core.Stream;
import posl.engine.type.ListCollector;
import posl.engine.type.MultiLineStatement;
import posl.engine.type.Statement;

/**
 * Represents the physical structure components of a basic Posl implementation.
 * The following represent built in types of structures
 * <p>
 * `{` and `}` surround a block of tokens which can be on multiple lines
 * `[` and `]` surround a single expression 
 * `(` and `)` represent a list of tokens
 * </p>
 * @author je bailey
 * 
 */
public class Grammar implements Lexeme {
	
	Pattern pattern = Pattern.compile("[{}()\\[\\]]");

	@Override
	public boolean consume(List<Token> tokens, Stream ps) {
		ps.setMark();
		boolean grammar = false;
		switch (ps.val()) {
		case '{':
		case '}':
		case '(':
		case ')':
		case '[':
		case ']':
			grammar = true;
		}
		if (grammar) {
			tokens.add(new Inner(ps.pop(), ps.getMark()));
		}
		return grammar;
	}

	private class Inner extends BasicToken {

		private char charValue;
		
		public Inner(String value, int start, int end) {
			this.value = value.charAt(0);
			this.startPos = start;
			this.endPos = end;
		}

		public Inner(char value, int i) {
			this.charValue = value;
			this.value = Character.toString(value);
			this.startPos = i;
			this.endPos = i + 1;
		}

		@Override
		public Collector consume(Collector collector,
				Stack<Collector> collectors, Stack<Character> charStack) {
			switch (charValue) {
			case '[':
				charStack.push(']');
				collectors.push(collector);
				collector = new Statement(startPos);
				break;
			case '(':
				charStack.push(')');
				collectors.push(collector);
				collector = new ListCollector();
				break;
			case '{':
				charStack.push('}');
				collectors.push(collector);
				collector = new MultiLineStatement();
				break;
			case ')':
				if (!charStack.empty() && charStack.pop() == charValue) {
					Collector temp = collector;
					collector = collectors.pop();
					collector.add(temp.get());
				} else {
					// throw new
					// PoslException(lineNumber,"could not match parenthesis");
				}
				break;
			case ']':
				if (!charStack.empty() && charStack.pop() == charValue) {
					Object temp = collector;
					collector = collectors.pop();
					collector.add(temp);
				} else {
					// throw new
					// PoslException(lineNumber,"could not match square bracket");
				}
				break;
			case '}':
				if (!charStack.empty() && charStack.pop() == charValue) {
					collector.isFinished();
					Object temp = collector;
					collector = collectors.pop();
					collector.add(temp);
				} else {
					// throw new
					// PoslException(lineNumber,"could not match brace");
				}
				break;
			}
			return collector;
		}

		@Override
		public void accept(TokenVisitor visitor) {
			visitor.visitGrammar(this);
		}

	}

	@Override
	public int consume(List<Token> tokens, CharSequence ps, int offset) {
		int totalCaptured = 0;
		Matcher matcher = pattern.matcher(ps);
		while (matcher.find(offset)) {
			String s = matcher.group();
			tokens.add(new Inner(s, offset + totalCaptured, matcher.end()));
			totalCaptured += s.length();
		}
		return totalCaptured;
	}

}