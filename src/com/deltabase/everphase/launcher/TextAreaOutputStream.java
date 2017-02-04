package com.deltabase.everphase.launcher;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.io.IOException;
import java.io.OutputStream;

public class TextAreaOutputStream extends OutputStream {
    private JTextPane textControl;

    public TextAreaOutputStream(JTextPane control) {
        textControl = control;
    }

    public void write(int b) throws IOException {
        // append the data as characters to the JTextArea control
        append(String.valueOf((char) b));
    }

    public void append(String s) {
        try {
            Document doc = textControl.getDocument();
            doc.insertString(doc.getLength(), s, null);
        } catch (BadLocationException exc) {
            exc.printStackTrace();
        }
    }
}
