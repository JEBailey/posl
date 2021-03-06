package posl.engine.api;

import java.util.Iterator;
import java.util.List;

public interface Lexer extends Iterator<Token> {
	
	public abstract void tokenize(CharSequence data, List<Lexeme>lexemes);

}