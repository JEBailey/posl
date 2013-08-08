package posl.editorkit;import java.awt.event.ActionEvent;import java.awt.event.InputEvent;import java.awt.event.KeyEvent;import java.io.CharArrayReader;import java.util.ArrayList;import java.util.Iterator;import java.util.List;import java.util.logging.Level;import java.util.logging.Logger;import javax.swing.AbstractAction;import javax.swing.KeyStroke;import javax.swing.event.DocumentEvent;import javax.swing.event.UndoableEditEvent;import javax.swing.event.UndoableEditListener;import javax.swing.text.BadLocationException;import javax.swing.text.PlainDocument;import javax.swing.text.Segment;import javax.swing.undo.CannotRedoException;import javax.swing.undo.CannotUndoException;import javax.swing.undo.UndoManager;import posl.engine.api.Token;/** * A document to represent text in the form of the po scripting language */public class DocumentImpl extends PlainDocument {	/**	 * 	 */	private static final long serialVersionUID = 1L;	private DocumentLexer lexer;	public List<Token> tokens = new ArrayList<Token>();	private UndoManager undoManager = new UndoManager();	public UndoAction undoAction = new UndoAction();	public RedoAction redoAction = new RedoAction();	    private static final Logger log = Logger.getLogger(DocumentImpl.class.getName());	public DocumentImpl(String contentType) {		super();		lexer = new DocumentLexer(contentType);		putProperty(PlainDocument.tabSizeAttribute, 4);		addUndoableEditListener(new UndoableEditListener() {            @Override            public void undoableEditHappened(UndoableEditEvent evt) {                if (evt.getEdit().isSignificant()) {                    undoManager.addEdit(evt.getEdit());                    undoAction.updateUndoState();                    redoAction.updateRedoState();                }            }        });	}	    /**     * Parse the entire document and return list of tokens that do not already     * exist in the tokens list.  There may be overlaps, and replacements,      * which we will cleanup later.     * @return list of tokens that do not exist in the tokens field      */    private void parse() {        long ts = System.nanoTime();        int len = getLength();        try {            Segment seg = new Segment();            getText(0, getLength(), seg);            lexer.tokenize(seg.toString());            tokens = lexer.getTokens();        } catch (BadLocationException ex) {            log.log(Level.SEVERE, null, ex);        } finally {            if (log.isLoggable(Level.FINEST)) {                log.finest(String.format("Parsed %d in %d ms, giving %d tokens\n",                        len, (System.nanoTime() - ts) / 1000000, tokens.size()));            }        }    }        public List<Token> getTokensInRange(int start, int end){    	    	int startIndex = 0;    	int endIndex = 0;    	Iterator<Token> it = tokens.iterator();    	while (it.hasNext()){    		Token token = it.next();    		if (token.getStartOffset() >= start || token.getEndOffset() >= start){    			endIndex = startIndex = tokens.indexOf(token);    			break;     		}    	}    	while (it.hasNext()){    		Token token = it.next();    		if (token.getStartOffset() <= end){    			++endIndex;    		} else {    			if (token.getEndOffset() > end){    				++endIndex;    			}    			break;    		}    	}    	return tokens.subList(startIndex, endIndex);    }        public Token[] getTokenAt(int pos) throws Exception {    	Token[] reply = {};    	for (Token token:tokens){    		if (pos >= token.getStartOffset() && pos <= token.getEndOffset()){    			return new Token[]{token};    		}    	}    	return reply;    }	    @Override    protected void fireChangedUpdate(DocumentEvent e) {        super.fireChangedUpdate(e);    }    @Override    protected void fireInsertUpdate(DocumentEvent e) {        parse();        super.fireInsertUpdate(e);    }    @Override    protected void fireRemoveUpdate(DocumentEvent e) {        parse();        super.fireRemoveUpdate(e);    }        /**     * Perform an undo action, if possible     */    public void doUndo() {        if (undoManager.canUndo()) {            undoManager.undo();            parse();        }    }    /**     * Perform a redo action, if possible.     */    public void doRedo() {        if (undoManager.canRedo()) {            undoManager.redo();            parse();        }    }	/**	 * Updates any document structure as a result of text removal. This will	 * happen within a write lock. The superclass behavior of updating the line	 * map is executed followed by placing a lexical update command on the	 * analyzer queue.	 * 	 * @param chng	 *            the change event	 */	protected void removeUpdate(DefaultDocumentEvent chng) {		super.removeUpdate(chng);	}		public UndoManager getUndoManager() {		return undoManager;	}	    @SuppressWarnings("serial")	class UndoAction extends AbstractAction {        public UndoAction() {            super("Undo");            setEnabled(false);            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));        }        public void actionPerformed(ActionEvent e) {            try {                undoManager.undo();            } catch (CannotUndoException ex) {                System.out.println("Unable to undo: " + ex);                ex.printStackTrace();            }            updateUndoState();            redoAction.updateRedoState();        }        protected void updateUndoState() {        	setEnabled(undoManager.canUndo());        }    }    @SuppressWarnings("serial")	class RedoAction extends AbstractAction {        public RedoAction() {            super("Redo");            setEnabled(false);            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK));        }        public void actionPerformed(ActionEvent e) {            try {                undoManager.redo();            } catch (CannotRedoException ex) {                System.out.println("Unable to redo: " + ex);                ex.printStackTrace();            }            updateRedoState();            undoAction.updateUndoState();        }        protected void updateRedoState() {        	setEnabled(undoManager.canRedo());        }    }}