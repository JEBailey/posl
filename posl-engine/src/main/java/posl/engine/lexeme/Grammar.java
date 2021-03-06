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
	
	private static final Pattern pattern = Pattern.compile("[{}()\\[\\]]");
	
	private Matcher matcher;
	
	private CharSequence cachedSequence;
	
	@Override
	public int consume(List<Token> tokens, CharSequence ps, int offset) {
		if (ps != cachedSequence){
			cachedSequence = ps;
			matcher = pattern.matcher(ps);
		}
		int totalCaptured = 0;
		matcher.region(offset, ps.length());
		if (matcher.lookingAt()) {
			String s = matcher.group();
			tokens.add(new Inner(s, offset, matcher.end()));
			totalCaptured += s.length();
		}
		return totalCaptured;
	}

	private class Inner extends BasicToken {

		private char charValue;
		
		public Inner(String value, int start, int end) {
			this.charValue = value.charAt(0);
			this.value = value;
			this.startPos = start;
			this.endPos = end;
		}

		@Override
		public Collector consume(Collector collector,
				Stack<Collector> collectors, Stack<Character> charStack) {
			switch (charValue) {
			case '[':
				charStack.push(']');
				collectors.push(collector);
				collector = new Statement(startPos,collector.getLineNumber());
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



}
