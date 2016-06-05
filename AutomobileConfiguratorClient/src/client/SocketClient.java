package client;

import java.net.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.io.*;

import exception.AutoException;
import model.Automobile;

public class SocketClient extends Thread implements SocketClientInterface {
	// Instance Variables

	private Socket socket;
	private String hostName;
	private int incomingPort;
	private ObjectInputStream objectInputStream;
	private ObjectOutputStream objectOutputStream;
	private BufferedOutputStream bufferedOutputStream;
	private FileInputStream inputStream;
	private Scanner scanner;
	private static int BUFFER_SIZE = 1024;
	private static int SERVER_PORT = 17000;

	// Constructors

	public SocketClient(String hostName, int incomingPort) {
		this.hostName = hostName;
		this.incomingPort = incomingPort;
	}

	public SocketClient(Socket socket) {
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

	// Instance Methods

	public void run() {
		while(true) {
			if (openConnection()) {
				handleSession();
				closeSession();
			}
		}
	}

	// Interface Methods

	public boolean openConnection() {
		try {
			socket = new Socket("localhost", SERVER_PORT);
			objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

		} catch (IOException socketError) {
			socketError.printStackTrace();
			return false;
		}
		return true;
	}

	public void handleSession() {
		try {
			System.out.println("*** Welcome to Automobile Configurator ***");
			System.out.println("Please enter \"1\" for uploading a new car or \"2\" for configuring an existing car: ");

			scanner = new Scanner(System.in);
			int userChoice = 0;
			boolean validInput = false;
			
			while (!validInput) {
				userChoice = scanner.nextInt();
				
				switch(userChoice) {
					case 1:
						validInput = true;
						break;
					case 2:
						validInput = true;
						break;
					default:
						System.out.println("Invalid input! Please enter \"1\" or \"2\".");
						break;
				}
			}
			
			objectOutputStream.flush();

			if (userChoice == 1) {
				scanner = new Scanner(System.in);

				objectOutputStream.writeObject("upload");

				System.out.println("Please enter the input file name:");
				String inputFilePath = scanner.nextLine();

				try {
					File inputFile = new File(inputFilePath);

					OutputStream socketOutputStream = socket.getOutputStream();

					bufferedOutputStream = new BufferedOutputStream(socketOutputStream, BUFFER_SIZE);
					inputStream = new FileInputStream(inputFile);

					int bytesReceived = 0;
					int bufferSize = 1024;
					byte[] buffer = new byte[1024];

					while ((bytesReceived = inputStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
						bufferSize += BUFFER_SIZE;
						bufferedOutputStream.write(buffer, 0, bytesReceived);
						bufferedOutputStream.flush();
					}

					socket.shutdownOutput();

					objectInputStream = new ObjectInputStream(socket.getInputStream());
					objectInputStream.skip(Long.MAX_VALUE);

				} catch (NullPointerException npe) {
					System.out.println("ERROR! Input file not found.");
				}

			} else if (userChoice == 2) {
				objectOutputStream.writeObject("configure");
				objectOutputStream.flush();

				objectInputStream = new ObjectInputStream(socket.getInputStream());
				objectInputStream.skip(Long.MAX_VALUE);	

				@SuppressWarnings("unchecked")
				ArrayList<String> automobilesFromServer = (ArrayList<String>) objectInputStream.readObject();

				if (automobilesFromServer.isEmpty()) {
					System.out.println("No cars on server at this time.");
					objectOutputStream.flush();
					
				} else {
					String selectedCar = selectionHelper(automobilesFromServer);

					if (selectedCar != "") {
						objectOutputStream.writeObject(selectedCar);
						objectOutputStream.flush();

						Automobile carFromServer = (Automobile) objectInputStream.readObject();

						if (carFromServer != null) {
							selectOptionOnCar(carFromServer);

						} else {
							System.out.println("The server returned NULL for the selected car.");
						}
					}
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void closeSession() {
		try {
			bufferedOutputStream.close();
			inputStream.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String arg[]) {
		String localHostName;
		SocketClient client;

		try {
			localHostName = InetAddress.getLocalHost().getHostName();

		} catch (UnknownHostException e) {
			System.out.println("ERROR! Local host not found.");
		}

		try {
			client = new SocketClient(new Socket("localhost", SERVER_PORT));
			client.start();

		} catch (UnknownHostException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Utility Methods

	private synchronized void selectOptionOnCar(Automobile selectedCar) {
		String setSelection = "";
		String optionSelection = "";

		while (setSelection != "QUIT" || optionSelection != "QUIT") {
			System.out.println("Enter \"QUIT\" to exit");

			setSelection = selectionHelper(selectedCar.getOptionSetsNames());

			if (setSelection == "QUIT") {
				break;
			}

			String[] optionsInSelectedSet = selectedCar.getOptionsInSet(setSelection);

			optionSelection = selectionHelper(optionsInSelectedSet);

			try {
				selectedCar.selectOption(setSelection, optionSelection.split(",")[0]);

			} catch (AutoException e) {
				e.printIssue();
			}

			System.out.println("\nHere is the updated Automobile:\n" + selectedCar);
			System.out.println();
		}
	}

	private String selectionHelper(ArrayList<String> automobiles) {
		scanner = new Scanner(System.in);
		String selectedModel = "";
		int selection = -1;
		int index = 0;

		while (selectedModel == "") {
			if (automobiles.isEmpty()) {
				System.out.println("No automobiles available on server.");
				break;

			} else {
				System.out.println("Available cars:");

				for (String optionName: automobiles) {
					System.out.println("[" + (index + 1) + "] - " + optionName);
					index++;
				}

				System.out.println("Make a selection by entering the corresponding number.");

				try {
					selection = scanner.nextInt();
				
				} catch (InputMismatchException ime) {
					return "QUIT";
				}

				if (selection < 0 || selection > automobiles.size()) {
					System.out.println("Invalid selection! Please try again.");
					index = 0;

				} else {
					selectedModel = automobiles.get(selection - 1);
				}
			}
		}

		return selectedModel;
	}

	private String selectionHelper(String[] optionsNames) {
		scanner = new Scanner(System.in);
		int selection = -1;
		int index = 0;

		while (selection < 0 || selection > optionsNames.length) {
			System.out.println("Options Available:");

			for (String optionName: optionsNames) {
				System.out.println("[" + (index + 1) + "] - " + optionName);
				index++;
			}

			System.out.println("Make a selection by entering the corresponding number.");

			try {
				selection = scanner.nextInt();
			
			} catch (InputMismatchException ime) {
				return "QUIT";
			}

			if (selection < 0 || selection > optionsNames.length) {
				System.out.println("Invalid selection! Please try again.");
				index = 0;
			}
		}

		return optionsNames[selection - 1];
	}
}
