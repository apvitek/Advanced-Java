package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	// Instance Variables
	
	private ServerSocket serverSocket;
	private static int SERVER_PORT = 17000;
	
	// Constructors
	
	public Server() {
		try {
			serverSocket = new ServerSocket(SERVER_PORT);
			System.out.println("Server started on port " + SERVER_PORT + ".");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Instance Methods
	
	public void runServer() {
		DefaultSocketServer defaultClientSocket = null;
		
		try {
			while(true) {
				Socket socket = serverSocket.accept();
				defaultClientSocket = new DefaultSocketServer(serverSocket, socket);
	            defaultClientSocket.start();
			}
        } catch (IOException e) {
        	e.printStackTrace();
        }
	}

	public static void main(String[] args) {
		Server server = new Server();
		server.runServer();
	}
}
