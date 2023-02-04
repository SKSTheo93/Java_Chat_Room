package socketMain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JOptionPane;

import dataStructures.AVLTree;
import guiUtilities.JTextPaneChat;

public class Server extends Object {
	
	private class ClientData extends Object implements Comparable<ClientData> {
		private String name;
		private String hostname;
		private int port;
		private Socket clientSocket;
		private PrintWriter out;
		private BufferedReader in;
		private Thread thread;
		
		public ClientData(Socket socket) {
			try {
				clientSocket = socket;
				out = new PrintWriter(clientSocket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				name = consumeEnter(in.readLine());
				hostname = clientSocket.getLocalAddress().getHostName();
				port = clientSocket.getLocalPort(); // cause this is an accepted socket, not created from zero
				
				thread = new Thread() {
					@Override
					public void run() {
						String message = null;
						try {
							while((message = in.readLine()) != null)
								sendMessage(message);
						}
						catch(IOException e) {
							if(gui == null) {
								System.err.println("Error receiving data from client " + name);
								System.err.println(e.getMessage());
							}
							else {
								displayMessageThroughGUI("Error receiving data from client " + name + "\n" 
										+ e.getMessage());
							}
							
							for(ClientData cd : clients) {
								if(cd.clientSocket == clientSocket) {
									clients.remove(cd);
									break;
								}
							}
						}
					}
				};
				
				thread.start();
			}
			catch(IOException e) {
				if(gui == null) {
					System.err.println("Error getting I/O Streams");
					System.err.println(e.getMessage());
				}
				else {
					displayMessageThroughGUI("Error getting I/O Streams\n" + e.getMessage());
				}
					
			}
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
		
		@Override
		public int compareTo(ClientData cd) {
			return this.name.compareTo(cd.name);
		}
	}
	
	private int port;
	private ServerSocket serverSocket;
	private AVLTree<ClientData> clients;
	private BufferedReader stdIn;
	private Thread accept;
	private Thread menu;
	private ServerGUI gui;
	
	private void displayMessageThroughGUI(String message) {
		JOptionPane.showMessageDialog(gui,
				message,
				"Information",
				JOptionPane.INFORMATION_MESSAGE);
	}
	
	private String consumeEnter(String name) {
		String clearName = name.substring(0, name.length());
		return clearName;
	}
	
	private synchronized void sendMessage(String message) {
		if(gui == null) {
			for(ClientData client : clients) {
				if(client.thread != Thread.currentThread())
					client.out.println(message);
				}
		}
		else {
			for(ClientData client : clients)
					client.out.println(message);
		}
	}
	
	public Server(int port, ServerGUI gui) {
		this.port = port;
		serverSocket = null;
		clients = null;
		stdIn = null; 
		accept = null;
		menu = null;
		this.gui = gui;
	}
	
	public void displayClients(boolean fullDisplay) {
		if(fullDisplay) {
			if(gui == null) {
				if(clients.size() == 0)
					System.out.println("Noone is connected");
				else {
					for(ClientData client : clients)
						System.out.println(client);
				}
			}
			else {
				JTextPaneChat console = gui.getChatConsole();
				if(clients.size() == 0)
					console.println("Noone is connected");
				else {
					for(ClientData client : clients)
						console.println(client.toString());
				}
			}
		}
		else {
			if(gui == null) {
				if(clients.size() == 0)
					System.out.println("Noone is connected");
				else {
					for(ClientData client : clients)
						System.out.println(client.name);
				}
			}
			else {
				JTextPaneChat console = gui.getChatConsole();
				if(clients.size() == 0)
					console.println("Noone is connected");
				else {
					for(ClientData client : clients)
						console.println(client.name);
				}
			}
		}
	}
	
	public void create() {
		try {
			serverSocket = new ServerSocket(port);
			clients = new AVLTree<>();
			stdIn = new BufferedReader(new InputStreamReader(System.in));
			
			accept = new Thread() {
				@Override
				public void run() {
					Socket clientSocket = null;
					try {
						ClientData clientData = null;
						while(true) {
							clientSocket = serverSocket.accept();
							clientData = new ClientData(clientSocket);
							clients.add(clientData);
							if(gui == null) {
								System.out.println("\n-------------" + clientData.name + " has been Connected------------");
								System.out.println("1. Show Clients");
								System.out.println("2. Show Clients With Details");
								System.out.println("3. Exit");
								System.out.print("Enter option: ");
							}
							else
								gui.getChatConsole().println("-------------" + clientData.name + " has been Connected------------");
						}
					}
					catch(IOException e) {
						if(gui == null) {
							System.err.println("Error while accepting clients");
							System.err.println(e.getMessage());
						}
						else {
							displayMessageThroughGUI("Error while accepting clients\n" + e.getMessage());
						}
						System.exit(1);
					}
				}
			};
			
			menu = new Thread() {
				@Override
				public void run() {
					boolean serverIsRunning = true;
					int option = 0;
					while(serverIsRunning) {
						try {
							System.out.println("1. Show Clients");
							System.out.println("2. Show Clients With Details");
							System.out.println("3. Exit");
							System.out.print("Enter option: ");
							option = Integer.parseInt(consumeEnter(stdIn.readLine()));
							
							switch(option) {
								case 1:
									displayClients(false);
									break;
								case 2:
									displayClients(true);
									break;
								case 3:
									serverIsRunning = false;
									break;
								default:
									System.out.println("Option " + option + " does not exist");
							}
						}
						catch(IOException e) {
							System.err.println("Error during input from server");
							System.err.println(e.getMessage());
						}
					}					
					System.exit(1);
				}
			};
			
			accept.start();
			if(gui == null)
				menu.start();
			
		}
		catch(IOException e) {
			if(gui == null) {
				System.err.println("Error creating server using port " + port);
				System.err.println(e.getMessage());
			}
			else {
				displayMessageThroughGUI("Error creating server using port " + port + "\n"
						+ e.getMessage());
			}
			System.exit(1);
		}
	}
	
	public static void main(String[] args) {
		if(args.length != 1) {
			System.err.println("Usage: java socketMain.Server <Port number>");
			System.exit(1);
		}
		
		int port = Integer.parseInt(args[0]);
		Server server = new Server(port, null);
		server.create();
		System.out.println("Server has been created and awating for connections...");
	}
}