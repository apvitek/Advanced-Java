package adapter;

import java.io.IOException;
import java.util.ArrayList;

import enums.FileType;
import model.Automobile;
import exception.AutoException;
import server.AutoServer;
import util.FileIO;

public class BuildAuto extends ProxyAutomobile implements CreateAuto, UpdateAuto, FixAuto, AutoServer {
	private static String sharedFileName;
	private double defaultAutoPrice = 0.0;
	private static BuildAuto builder = new BuildAuto();
	FileIO fileIO = new FileIO();

	// BuildAuto will be a singleton object
	protected BuildAuto() {}

	public static BuildAuto getInstance() {
		return builder;
	}

	public void updateOptionSetName(String modelName, String optionSetName, String newName) {
		try {
			autos.getAutomobile(modelName).updateOptionSetName(optionSetName, newName);

		} catch (AutoException e) {
			e.printIssue();
			logExceptionOnFile(e);
		}
	}

	public void updateOptionPrice(String modelName, String optionSetName, String optionName, double newPrice) {
		int index = -1;

		try {
			index = autos.getAutomobile(modelName).getIndexOfOptionSetNamed(optionSetName);
		} catch (AutoException e) {
			e.printIssue();
		}

		if (index != -1) {
			try {
				autos.getAutomobile(modelName).updateOptionPriceInSet(index, optionName, newPrice);
			} catch (AutoException e) {
				e.printIssue();
				logExceptionOnFile(e);
			}
		}
	}

	public void buildAuto(String fileName, FileType type) {
		Automobile auto = null;
		
		fileIO.setInputFileType(type);

		try {
			auto = new Automobile("NOT SET", -1);

		} catch (AutoException e2) {
			e2.printIssue();
			logExceptionOnFile(e2);
		}

		try {
			fileIO.setFileURL(fileName);

		} catch (AutoException e) {
			e.printIssue();
			logExceptionOnFile(e);
			e.fixIssue();

			try {
				fileIO.setFileURL(sharedFileName);

			} catch (AutoException e1) {
				e1.printIssue();
			}

		}

		try {
			fileIO.loadFromInputFile(auto);

		} catch (IOException e) {
			e.printStackTrace();

		} catch (AutoException e) {
			e.printIssue();
			logExceptionOnFile(e);
			e.fixIssue();
			auto.setBasePrice(defaultAutoPrice);

			try {
				fileIO.loadFromInputFile(auto);

			} catch (IOException e1) {
				e1.printStackTrace();

			} catch (AutoException e1) {
				e1.printIssue();
			}
		}

		addAutomobile(auto);
	}
	
	public ArrayList<String> getAutoList() {
		ArrayList<Automobile> autos = getAllAutomobiles();
		ArrayList<String> models = new ArrayList<>();
		
		for (Automobile auto: autos) {
			models.add(auto.getName());
		}
		
		return models;
	}
	
	public ArrayList<Automobile> getAllAutomobiles() {
		return super.getAllAutomobiles();
	}

	public void fixAuto() {
		// UNIMPLEMENTED
	}

	// Utility Methods
	public void setSharedFileName(String newFileName) {
		sharedFileName = newFileName;
	}

	public void setDefaultAutoPrice(double newPrice) {
		defaultAutoPrice = newPrice;
	}

	public void printAuto(String modelName) {
		System.out.println(autos.getAutomobile(modelName));
	}

	private void logExceptionOnFile(AutoException e) {
		fileIO.logErrorsToTextFile(e.getMessage());
	}
}