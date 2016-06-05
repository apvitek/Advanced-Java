package exception;

import adapter.BuildAuto;

public class FixMissingPrice {
	public FixMissingPrice() {}

	protected void fix() {
		BuildAuto sharedBuilder = BuildAuto.getInstance();
		sharedBuilder.setDefaultAutoPrice(10000);
	}
}