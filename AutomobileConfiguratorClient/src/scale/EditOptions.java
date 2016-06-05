package scale;

import java.util.Random;

import model.Automobile;
import exception.AutoException;
import adapter.ProxyAutomobile;

public class EditOptions extends ProxyAutomobile implements Runnable, ScaleThread {
	// Instance Variables
	
	private int ops;
	private String modelName;
	private String optionSetName;
	private String optionName;
	private String newName;
	private double newPrice;

	// Constructors
	
	public EditOptions(String modelName, String optionSetName, String newName) {
		this.ops = 0;
		this.modelName = modelName;
		this.optionSetName = optionSetName;
		this.newName = newName;
	}
	
	public EditOptions(String modelName, String optionSetName, String optionName, double newPrice) {
		this.ops = 1;
		this.modelName = modelName;
		this.optionSetName = optionSetName;
		this.optionName = optionName;
		this.newPrice = newPrice;
	}

	// Wrapper synchronized methods

	public synchronized void updateOptionSetName(String modelName, String optionSetName, String newName) {
		try {
			Automobile auto = getAutomobile(modelName);
			sleepForRandomTime();
			auto.updateOptionSetName(optionSetName, newName);
		} catch (AutoException e) {
			e.printStackTrace();
		}
	}

	public synchronized void updateOptionPrice(String modelName, String optionSetName, String optionName, double newPrice) {
		try {
			Automobile auto = getAutomobile(modelName);
			int setIndex = auto.indexOfOptionSet(optionSetName);
			sleepForRandomTime();
			auto.updateOptionPriceInSet(setIndex, optionName, newPrice);
		} catch (AutoException e) {
			e.printStackTrace();
		}
	}
	
	public void updateOptionName(String modelName, String optionSetName, String optionName, String newName) {
		try {
			Automobile auto = getAutomobile(modelName);
			sleepForRandomTime();
			auto.updateOptionNameInSet(optionSetName, optionName, newName);
		} catch (AutoException e) {
			e.printStackTrace();
		}
	}

	private int generateRandomSleepTime(int min, int max) {
		Random number = new Random();
		int randomInt = number.nextInt((max - min) + 1) + min;
		return randomInt;
	}
	
	private void sleepForRandomTime() {
		try {
			Thread.sleep(generateRandomSleepTime(0, 1000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// Overridden Methods

	@Override
	public void run() {
		switch(ops) {
		case 0:
			updateOptionSetName(modelName, optionSetName, newName);
			break;
		case 1:
			updateOptionPrice(modelName, optionSetName, optionName, newPrice);
			break;
		}
	}
}