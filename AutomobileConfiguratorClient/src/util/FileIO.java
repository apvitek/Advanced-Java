package util;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.*;

import enums.FileType;
import exception.AutoException;
import model.Automobile;

public class FileIO {
	// Instance Variables

	private String inputFileURL;
	private String outputFileName = "Exceptions.txt";
	private BufferedReader buff;
	private FileType inputFileType;

	// Constructors

	public FileIO(String fileURL, FileType type) throws AutoException {
		this.setFileURL(fileURL);
		this.inputFileType = type;
	}

	// Getters and Setters

	public FileIO() {
		inputFileURL = "";
	}

	public String getFileURL() {
		return inputFileURL;
	}

	public void setFileURL(String fileURL) throws AutoException {
		File f = new File(fileURL);

		if (!f.exists()) {
			throw new AutoException(1);
		}

		this.inputFileURL = fileURL;
	}

	public FileType getInputFileType() {
		return inputFileType;
	}

	public void setInputFileType(FileType inputFileType) {
		this.inputFileType = inputFileType;
	}

	// Instance Methods

	public void loadFromInputFile(Automobile carFromFile) throws IOException, AutoException {
		switch (inputFileType) {
		case TextFile:
			loadFromTextFile(carFromFile);
			break;
		case PropertyList:
			loadFromPropertyList(carFromFile);
			break;
		}
	}

	public void loadFromFile(Automobile carFromFile) {
		try (FileInputStream fis = new FileInputStream("automotive.dat");
				ObjectInputStream ois = new ObjectInputStream(fis);
				) {
			carFromFile = (Automobile) ois.readObject();

		} catch (IOException ioe) {
			System.out.println("Error reading file");
			ioe.printStackTrace();

		} catch (ClassNotFoundException cnfe) {
			System.out.println("Error loading from file.");
			cnfe.printStackTrace();
		}
	}

	public void saveToFile(Automobile car) {
		try (FileOutputStream fos = new FileOutputStream("automotive.dat");
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				) {
			oos.writeObject(car);

		} catch (IOException ioe) {
			System.out.println("Problem saving to file.");
			ioe.printStackTrace();
		}
	}

	public void logErrorsToTextFile(String exception) {
		try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outputFileName, true)))) {
			String timeStamp = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z").format(Calendar.getInstance().getTime());
			out.println(timeStamp + ": " + exception);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public HashMap<Integer, String> loadErrorCodesFromFile() {
		HashMap<Integer, String> errorsFromFile = new HashMap<>();

		try {
			FileReader file = new FileReader("AutoException.txt");
			buff = new BufferedReader(file);
			boolean eof = false;

			int currentCode = 0;

			while (!eof) {
				String line = buff.readLine();

				if (line == null) {
					eof = true;

				} else {
					// Line is read successfully
					Pattern p = Pattern.compile("[^ -]+");
					Matcher m = p.matcher(line);

					while (m.find()) {
						String currentLine = m.group();

						try {
							int errorCode = Integer.parseInt(currentLine);
							currentCode = errorCode;

						} catch (NumberFormatException e) {
							errorsFromFile.put(currentCode, currentLine);
						}
					}
				}
			}

		} catch (IOException e) {
			System.out.println("Error! " + e.toString());
		}

		return errorsFromFile;
	}

	private void loadFromTextFile(Automobile carFromFile) throws IOException, AutoException {
		try {
			FileReader file = new FileReader(inputFileURL);
			buff = new BufferedReader(file);
			boolean eof = false;

			boolean isCreatingOptionSet = false;
			int currentOptionSet = -1;
			String currentOptionName = "";

			while (!eof) {
				String line = buff.readLine();

				if (line == null) {
					eof = true;

				} else {
					// Line is read successfully
					Pattern p = Pattern.compile("[{} \\w-]+");
					Matcher m = p.matcher(line);

					while (m.find()) {
						String currentLine = m.group();

						if (carFromFile.getName() == "NOT SET") {	
							carFromFile.setName(currentLine);

						} else if (carFromFile.getBasePrice() == -1.0) {
							double priceFromFile;

							try {
								priceFromFile = Double.parseDouble(currentLine);
								carFromFile.setBasePrice(priceFromFile);

							} catch (NumberFormatException e) {
								throw new AutoException(2);
							}

						} else {
							if (currentLine == "NONE") break;
							if (carFromFile.getName().equalsIgnoreCase(currentLine)) break;

							switch (currentLine) {
							case "{":
								break;
							case "}":
								isCreatingOptionSet = false;
								break;
							default:
								if (!isCreatingOptionSet) {
									carFromFile.createSet(currentLine);
									currentOptionSet++;
									isCreatingOptionSet = true;

								} else {
									try {
										double price = Double.parseDouble(currentLine);
										carFromFile.updateOptionPriceInSet(currentOptionSet, currentOptionName, price);

									} catch (NumberFormatException e) {
										carFromFile.addOptionToSet(currentOptionSet, currentLine, 0);
										currentOptionName = currentLine;
									}
								}

							}
						}
					}
				}
			}

		} catch (IOException e) {
			System.out.println("Error! " + e.toString());

		} finally {
			buff.close();
		}
	}

	private void loadFromPropertyList(Automobile carFromFile) throws IOException, AutoException {
		Properties props = new Properties();
		FileInputStream in;

		try {
			in = new FileInputStream(inputFileURL);

			try {
				props.load(in);
				String model = props.getProperty("Model");

				if (!model.equals(null)) {
					if (carFromFile.getName() == "NOT SET") {
						carFromFile.setName(model);
					}

					double priceFromFile;

					if (carFromFile.getBasePrice() == -1.0) {
						try {
							priceFromFile = Double.parseDouble(props.getProperty("BasePrice"));
							carFromFile.setBasePrice(priceFromFile);

						} catch (NumberFormatException e) {
							throw new AutoException(2);
						}
					}
					
					Enumeration<?> e = props.keys();
					
					// Create all OptionSets first
					
					ArrayList<String> optionSetsNames = new ArrayList<>();
					
					while (e.hasMoreElements()) {
						String key = (String) e.nextElement();
						
						if (key.contains("OptionSetName")) {
							optionSetsNames.add(key);
						}
					}
					
					Collections.sort(optionSetsNames);
					
					for (String setName : optionSetsNames) {
						carFromFile.createSet(props.getProperty(setName));
					}

					// Reload properties enumeration and copy all Options
					
					e = props.keys();
					
					while (e.hasMoreElements()) {
						String key = (String) e.nextElement();
						String value = props.getProperty(key);
						
						if (key.contains("OptionName")) {
							String endingCode = key.substring(key.length() - 2, key.length());
							int optionSetIndex = Integer.parseInt(endingCode.substring(endingCode.length() - 2, endingCode.length() - 1));
							String keyToPrice = "OptionPrice" + endingCode;
							String priceFromProperties = props.getProperty(keyToPrice);
							carFromFile.addOptionToSet(optionSetIndex - 1, value, Double.parseDouble(priceFromProperties));
						}
					}
				}
			} catch (IOException e) {
				System.out.println("Error! " + e.toString());
			}

		} catch (FileNotFoundException e) {
			System.out.println("Error! " + e.toString());
		}
	}
}
