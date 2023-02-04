package socketMain;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.BoundedRangeModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;

import guiUtilities.JTextPaneChat;

public class ClientGUI extends JFrame {
	private static final long serialVersionUID = 1L;
	private JTextPaneChat chat;
	private JPanel chatPanel;
	private JButton connectButton;
	private JButton clearButton;
	private JPanel buttonPanel;
	private JPanel fullPanel;
	private JLabel inputLabel;
	private JTextField input;
	private JPanel inputPanel;
	private Color color  = new Color(85,25,225);
	private Font font = new Font("Serif", Font.ITALIC, 18);
	private Client client;
	
	public ClientGUI() {
		super("Client Interface");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		chat = new JTextPaneChat(500, 300);
		chat.setContentType("text/plain");
		 JScrollPane  scrollChat = new JScrollPane(chat,
				 JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		 scrollChat.getVerticalScrollBar().addAdjustmentListener( new AdjustmentListener() {
			 BoundedRangeModel brm = scrollChat.getVerticalScrollBar().getModel();
			 boolean wasAtBottom = true;

		     public void adjustmentValueChanged(AdjustmentEvent e) {
		     if (!brm.getValueIsAdjusting()) {
		        	if (wasAtBottom)
		        	   brm.setValue(brm.getMaximum());
		        }
		        else
		        	wasAtBottom = ((brm.getValue() + brm.getExtent()) == brm.getMaximum());
		     }
		});
		 
		chat.setEditable(false);
		chatPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		chatPanel.add(scrollChat);
		chat.setFont(font);
		chat.setForeground(Color.BLUE);
		
		connectButton = new JButton("Connect");
		clearButton = new JButton("Clear");
		clearButton.setEnabled(false);
		
		connectButton.setFont(font);
		clearButton.setFont(font);
		
		buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
		buttonPanel.add(connectButton);
		buttonPanel.add(clearButton);
		buttonPanel.setBackground(color);
		
		fullPanel = new JPanel(new FlowLayout());
		fullPanel.add(chatPanel);
		fullPanel.add(buttonPanel);
		fullPanel.setBackground(color);
		
		add(fullPanel, BorderLayout.CENTER);
		
		inputLabel = new JLabel("Input: ");
		inputLabel.setFont(font);
		input = new JTextField();
		input.setFont(font);
		input.setEditable(false);
		input.setEnabled(false);
		
		inputPanel = new JPanel(new GridLayout(1, 1, -500, 0));
		inputPanel.add(inputLabel);
		inputPanel.add(input);
		
		
		add(inputPanel, BorderLayout.PAGE_END);
		
		ActionListenerHandler handler = new ActionListenerHandler();
		connectButton.addActionListener(handler);
		clearButton.addActionListener(handler);
		input.addActionListener(handler);
	}
	
	private class ActionListenerHandler extends Object implements ActionListener {
		private String name;
		@Override
		public void actionPerformed(ActionEvent event) {
			if(event.getSource() == connectButton) {
				name = JOptionPane.showInputDialog(ClientGUI.this, "Input name");
				String hostname = JOptionPane.showInputDialog(ClientGUI.this, "Input Hostname");
				int port = Integer.parseInt(JOptionPane.showInputDialog(ClientGUI.this, "Input port number"));
				
				client = new Client(name, hostname, port, ClientGUI.this);
				//we probably need to add thread
				client.connect();
				connectButton.setEnabled(false);
				clearButton.setEnabled(true);
				
				input.setEditable(true);
				input.setEnabled(true);
				
				chat.println(name + " has succesfully connected to server");
				ClientGUI.this.setTitle("Client Interface - " + name);
			}
			else if (event.getSource() == clearButton) {
				chat.setText("");
			}
			else if(event.getSource() == input) {
				client.getOutputStream().println(name + ": " + input.getText());
				input.setText("");
			}
		}
	}
	
	public JTextPaneChat getConsoleChat() {
		return chat;
	}
	
	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		ClientGUI frame = new ClientGUI();
		frame.setBounds(400, 200, 640, 390);
		frame.setResizable(false);
		frame.setVisible(true);
	}
}