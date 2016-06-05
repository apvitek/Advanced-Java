package exception;

import adapter.BuildAuto;

public class FixFileName {
	private static String goodFileName = "car.txt";
	
	public FixFileName() {}

	protected void fix() {
		BuildAuto sharedBuilder = BuildAuto.getInstance();
		sharedBuilder.setSharedFileName(goodFileName);
	}
}
