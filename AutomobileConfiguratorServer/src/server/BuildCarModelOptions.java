package server;

import java.io.ObjectOutputStream;
import java.util.ArrayList;

import model.Automobile;
import enums.FileType;
import adapter.BuildAuto;

public class BuildCarModelOptions implements AutoServer {
	private static BuildAuto builder = BuildAuto.getInstance();
	
	public void buildAutoFromFile(String fileName, FileType type) {
		builder.buildAuto(fileName, type);
	}

	public ArrayList<String> getAutoList() { 
		return builder.getAutoList();
	}
	
	// Interface Methods

	public void sendAutomobileToStream(ObjectOutputStream stream, String autoName) {
		builder.sendAutomobileToStream(stream, autoName);
	}

	public ArrayList<Automobile> getAllAutomobiles() {
		return builder.getAllAutomobiles();
	}
}
