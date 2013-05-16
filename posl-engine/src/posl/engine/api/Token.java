package posl.engine.api;

import java.util.Stack;

public abstract class Token {
	
	protected int startPos;
	protected int endPos;
	
	protected String value;
	protected Object meta;
	
	/**
	 * represents an encapsulation of functionality,
	 * 
	 * @param statement current Aggregator which is consuming tokens
	 * @param statements stack of aggregators
	 * @param charStack used for to look for current bounding representation
	 * @return
	 */
	public abstract Aggregator consume(Aggregator statement, Stack<Aggregator> statements ,Stack<Character> charStack);
	
	public abstract void accept(TokenVisitor visitor);
	
	public int getStartOffset(){
		return startPos;
	}

	public int getEndOffset(){
		return endPos;
	}
	
	public void setMeta(Object meta){
		this.meta = meta;
	}
    
	public Object getMeta(){
		return meta;
	}
	
	public String getString(){
		return value;
	}
}
