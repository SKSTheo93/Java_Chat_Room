package guiUtilities;

import java.awt.Dimension;

import javax.swing.JTextPane;

public class JTextPaneChat extends JTextPane {
	private static final long serialVersionUID = 1L;
	private StringBuilder sb = null;
	
	public JTextPaneChat(int width, int height) {
		setPreferredSize(new Dimension(width, height));
		sb = new StringBuilder();
	}
	
	public void println(String message) {
		sb.append(getText() + message + "\n");
		setText(sb.toString());
		sb.setLength(0);
	}
}