package posl.engine.lexeme;

import java.util.List;

import posl.engine.api.ALexeme;
import posl.engine.core.PoslStream;
import posl.engine.token.Token;

public class MultiLineComments extends ALexeme {

	@Override
	public boolean consume(List<Token> tokens, PoslStream ps) {
		if (ps.val() == '/' && ps.LA(1)== '*') {
			ps.mark();
			while (ps.hasMore()) {
				if (ps.val() == '*' && ps.LA(1)== '/') {
					ps.pop();
					ps.pop();
					break;
				}
				ps.pop();
			}

			tokens.add(Token.COMMENT(ps.getSubString(), ps.getMark()));
			return true;
		} 
		return false;
	}

}
