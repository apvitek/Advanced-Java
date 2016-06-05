package driver;

import adapter.BuildAuto;
import enums.FileType;

public class AutomobileDriverProgram {
	public static void main(String[] args) {
		String modelName = "Ford Wagon ZTW";

		// Build auto from file
		BuildAuto builder = BuildAuto.getInstance();
		builder.buildAuto("carFromFile.Properties", FileType.PropertyList);
		builder.printAuto(modelName);
		System.out.println();
	}
}