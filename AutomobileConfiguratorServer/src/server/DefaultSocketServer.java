package server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import enums.FileType;

public class DefaultSocketServer extends Thread implements SocketClientInterface {
	private String hostName;
	private int incomingPort;
	private Socket socket;
	private ServerSocket serverSocket;
	private String fileName = "car.Properties";
	private ObjectInputStream objectInputStream;
	private ObjectOutputStream objectOutputStream;
	private BufferedInputStream bufferedInputStream;
	private FileOutputStream fileOutputStream;
	private static int BUFFER_SIZE = 1024;
	private BuildCarModelOptions serverBuilder = new BuildCarModelOptions();

	// Constructors

	public DefaultSocketServer(String stringHost, int incomingPort) {
		this.setHostName(stringHost);
		this.setIncomingPort(incomingPort);
	}

	public DefaultSocketServer(ServerSocket serverSocket, Socket socket) {
		this.setServerSocket(serverSocket);
		this.socket = socket;
	}

	// Getters and Setters

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public int getIncomingPort() {
		return incomingPort;
	}

	public void setIncomingPort(int incomingPort) {
		this.incomingPort = incomingPort;
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public void setServerSocket(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	// Instance Methods

	public void run() {
		if (openConnection()) {
			handleSession();

		} else {
			System.out.println("ERROR! Connection could not be established.");
		}
	}

	// Interface Methods

	public boolean openConnection() {
		try {
			objectInputStream = new ObjectInputStream(socket.getInputStream());

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public void handleSession() {
		try {
			objectInputStream.skip(Long.MAX_VALUE);
			String clientOption = (String) objectInputStream.readObject();

			if (clientOption.equals("upload")) {
				int bytesReceived = 0;
				int bufferSize = 1024;
				byte[] buffer = new byte[1024];

				File outputFile = new File(fileName);
				fileOutputStream = new FileOutputStream(outputFile);
				bufferedInputStream = new BufferedInputStream(socket.getInputStream(), BUFFER_SIZE);

				while ((bytesReceived = bufferedInputStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
					bufferSize += BUFFER_SIZE;
					fileOutputStream.write(buffer, 0, bytesReceived);
				}

				objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
				objectOutputStream.flush();
				objectOutputStream.writeObject("completed");

				serverBuilder.buildAutoFromFile(fileName, FileType.PropertyList);

			} else if (clientOption.equals("configure")) {
				objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
				objectOutputStream.flush();

				ArrayList<String> availableModels = serverBuilder.getAutoList();

				objectOutputStream.writeObject(availableModels);
				objectOutputStream.flush();

				if (!availableModels.isEmpty()) {
					String currentModel = (String) objectInputStream.readObject();
					serverBuilder.sendAutomobileToStream(objectOutputStream, currentModel);
				}

			} else {
				System.out.println("ERROR! Invalid input.");
			}

		} catch (EOFException eof) {
			eof.printStackTrace();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void closeSession() {
		try {
			bufferedInputStream.close();
			fileOutputStream.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
