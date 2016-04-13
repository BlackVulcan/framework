package util;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * The Class JTextFieldLimit.
 */
public class JTextFieldLimit extends PlainDocument {
    private int limit;

    /**
     * Instantiates a new j text field limit.
     *
     * @param limit the limit
     */
    public JTextFieldLimit(int limit) {
        super();
        this.limit = limit;
    }

    /* (non-Javadoc)
     * @see javax.swing.text.PlainDocument#insertString(int, java.lang.String, javax.swing.text.AttributeSet)
     */
    @Override
    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
        if (str == null || !str.matches("\\d+"))
            return;

        if ((getLength() + str.length()) <= limit) {
            super.insertString(offset, str, attr);
        }
    }
}
