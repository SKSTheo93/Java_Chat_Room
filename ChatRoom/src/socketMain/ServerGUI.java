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

public class ServerGUI extends JFrame {
	private static final long serialVersionUID  = 1L;
	
	private JTextPaneChat chat;
	private JScrollPane scrollChat;
	private JPanel chatPanel;
	private JButton startServerButton;
	private JButton displayClientsButton;
	private JButton displayClientsWithDetailsButton;
	private JPanel buttonPanel;
	private JPanel fullPanel;
	private JLabel inputLabel;
	private JTextField input;
	private JPanel inputPanel;
	private Server server;
	private Color color = new Color(61,127,23);
	
	public ServerGUI() {
		super("Server Interface");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		Font font = new Font("Serif", Font.ITALIC, 20);
		
		chat = new JTextPaneChat(500, 300);
		chat.setContentType("text/plain");
		chat.setFont(new Font("Serif", Font.ITALIC, 18));
		chat.setForeground(Color.BLUE);
		chat.setEditable(false);
		
		scrollChat = new JScrollPane(chat,
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
		
		chatPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		chatPanel.add(scrollChat);
		
		startServerButton = new JButton("Start Server");
		displayClientsButton = new JButton("Display Clients");
		displayClientsButton.setEnabled(false);
		displayClientsWithDetailsButton = new JButton("Display Clients With Details");
		displayClientsWithDetailsButton.setEnabled(false);
		
		startServerButton.setFont(font);
		displayClientsButton.setFont(font);
		displayClientsWithDetailsButton.setFont(font);
		
		buttonPanel = new JPanel(new GridLayout(3, 1, 10 , 10));
		buttonPanel.add(startServerButton);
		buttonPanel.add(displayClientsButton);
		buttonPanel.add(displayClientsWithDetailsButton);
		buttonPanel.setBackground(color);
		
		fullPanel = new JPanel(new FlowLayout());
		fullPanel.add(chatPanel);
		fullPanel.add(buttonPanel);
		fullPanel.setBackground(color);
		
		add(fullPanel, BorderLayout.CENTER);
		
		inputLabel = new JLabel("Input: ");
		inputLabel.setFont(font);
		input = new JTextField();
		input.setEnabled(false);
		input.setEditable(false);
		input.setFont(font);
		
		inputPanel = new JPanel(new GridLayout(1, 2, -680 ,0));
		inputPanel.add(inputLabel);
		inputPanel.add(input);
		
		add(inputPanel, BorderLayout.PAGE_END);
		
		ActionListenerHandler handler = new ActionListenerHandler();
		startServerButton.addActionListener(handler);
		displayClientsButton.addActionListener(handler);
		displayClientsWithDetailsButton.addActionListener(handler);
		input.addActionListener(handler);
	}
	
	private class ActionListenerHandler extends Object implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			if(event.getSource() == startServerButton) {
				int port = Integer.parseInt(JOptionPane.showInputDialog(ServerGUI.this, "Input Port Number"));
				server = new Server(port, ServerGUI.this);
			
				// we probably need to add thread
				
				server.create();
				startServerButton.setEnabled(false);
				displayClientsButton.setEnabled(true);
				displayClientsWithDetailsButton.setEnabled(true);
				input.setEditable(true);
				input.setEnabled(true);
				
				chat.println("Server has been create and awaiting for connection...");
				
				ServerGUI.this.setTitle("Server Interface - Port " + port);
			}
			else if(event.getSource() == displayClientsButton) {
				chat.setText("");
				chat.println("Currently Connected:");
				server.displayClients(false);
			}
			else if(event.getSource() == displayClientsWithDetailsButton) {
				chat.setText("");
				chat.println("Currently Connected:");
				server.displayClients(true);
			}
			else if(event.getSource() == input) {
				if(input.getText().toUpperCase().equals("SHOW_CLIENTS")) {
					chat.setText("");
					chat.println("Currently Connected:");
					server.displayClients(true);
				}
				input.setText("");
			}
		}
	}
	
	public JTextPaneChat getChatConsole() {
		return chat;
	}
	
	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		ServerGUI frame = new ServerGUI();
		frame.setBounds(400, 200, 816, 390);
		frame.setResizable(false);
		frame.setVisible(true);
	}
}