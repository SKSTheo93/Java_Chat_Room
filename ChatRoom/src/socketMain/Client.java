package socketMain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

public class Client extends Object {
	private String name;
	private String hostname;
	private int port;
	private Socket clientSocket;
	private PrintWriter out;
	private BufferedReader in;
	private BufferedReader stdIn;
	private Thread receive;
	private Thread send;
	private ClientGUI gui;
	
	private void displayMessageThroughGUI(String message) {
		JOptionPane.showMessageDialog(gui,
				message,
				"Information",
				JOptionPane.INFORMATION_MESSAGE);
	}
	
	public Client(String name, String hostname, int port, ClientGUI gui) {
		this.name = name;
		this.hostname = hostname;
		this.port = port;
		this.gui = gui;
		out = null;
		in = null;
		stdIn = null;
		receive = null;
		send = null;
	}
	
	public synchronized void connect() {
		try {
			clientSocket = new Socket(hostname, port);
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			stdIn = new BufferedReader(new InputStreamReader(System.in));
			
			receive = new Thread() {
				@Override
				public void run() {
					String message = null;
					try {
						while((message = in.readLine()) != null) {
							if(gui == null)
								System.out.println(message);
							else
								gui.getConsoleChat().println(message);
						}
					}
					catch(IOException e) {
						if(gui == null) {
							System.err.println("Error while receiveing data for client " + name);
							System.out.println(e.getMessage());
						}
						else {
							displayMessageThroughGUI("Error while receiveing data for client " + name + "\n"
									+ e.getMessage());
						}
						System.exit(1);
					}
				}
			};
			
			send = new Thread() {
				@Override
				public void run() {
					String input = null;
					try {
						out.println(name);
						while((input = stdIn.readLine()) != null)
							out.println(name + ": " + input);
					}
					catch(IOException e) {
						if(gui == null) {
							System.err.println("Error while sending data");
							System.err.println(e.getMessage());
						}
						else {
							displayMessageThroughGUI("Error while sending data\n" + e.getMessage() );
						}
						System.exit(1);
					}
				}
			};
			
			receive.start();
			if(gui == null)
				send.start();
			else
				out.println(name);
		}
		catch(UnknownHostException e) {
			if(gui == null) {
				System.err.println("Unknown host " + hostname);
				System.err.println(e.getMessage());
			}
			else {
				displayMessageThroughGUI("Unknown host " + hostname + "\n" + e.getMessage());
			}
			System.exit(1);
		}
		catch(IOException e) {
			if(gui == null) {
				System.err.println("Error Creating I/O Streams for client " + name);
				System.err.println(e.getMessage());
			}
			else {
				displayMessageThroughGUI("Error Creating I/O Streams for client " + name + "\n" 
						+ e.getMessage());
			}
			System.exit(1);
		}
	}
	
	public PrintWriter getOutputStream() {
		return out;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("---Socket Information Start---\n");
		sb.append("Name: " + name + "\n");
		sb.append("Hostname: " + hostname + "\n");
		sb.append("Port: " + port + "\n");
		sb.append("---Socket Information End---\n");
		return sb.toString();
	}
	
	public static void main(String[] args) {
		if(args.length != 3) {
			System.err.println("Usage: java socketMain.Client <Name> <Hostname> <Port number>");
			System.exit(1);
		}
		
		String name = args[0];
		String hostname = args[1];
		int port = Integer.parseInt(args[2]);
		
		Client client = new Client(name, hostname, port, null);
		client.connect();
		System.out.println(client);
	}
}