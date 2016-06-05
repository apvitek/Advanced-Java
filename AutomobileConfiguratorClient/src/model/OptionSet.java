package model;

import java.io.Serializable;
import java.util.ArrayList;

import exception.AutoException;

public class OptionSet implements Serializable {
	// Instance Variables

	private ArrayList<Option> options;
	private String name;
	private Option selectedOption;

	// Static Variables

	private static final long serialVersionUID = 8051431971025665281L;

	// Constructors

	protected OptionSet() {
		this.name = "";
		options = new ArrayList<>();
	}
	
	protected OptionSet(String name) {
		this();
		this.name = name;
	}

	// Getters and Setters

	protected String getName() {
		return name;
	}

	protected void setName(String name) {
		this.name = name;
	}

	protected ArrayList<Option> getOptions() {
		return options;
	}

	protected void setOptions(ArrayList<Option> options) {
		this.options = options;
	}
	
	// May return null if no selected option
	protected Option getSelectedOption() {
		return selectedOption;
	}
	
	// May return null if no selected option
	protected String getSelectedOptionName() {
		if (selectedOption != null) {
			return selectedOption.getName();
		}
		
		return null;
	}
	
	// May return 0 if no selected option
	protected double getSelectedOptionPrice() {
		if (selectedOption != null) {
			return selectedOption.getPrice();
		}
		
		return 0;
	}

	protected boolean setSelectedOption(Option option) {
		for (Option existingOption : options) {
			if (existingOption.getName().equalsIgnoreCase(option.getName())) {
				selectedOption = existingOption;
				return true;
			}
		}
		
		return false;
	}

	protected boolean setSelectedOption(String optionName) throws AutoException {
		if (containsOption(optionName)) {
			selectedOption = options.get(indexOfOption(optionName));
			return true;
		}
		
		return false;
	}
	
	// Instance Methods

	// *** CREATE ***

	// Returns true if successful, false if no space in array
	private boolean createOption(Option option) {
		return options.add(option);
	}

	protected boolean createOption(String name) {
		return createOption(new Option(name));
	}

	protected boolean createOption(String name, double price) {
		return createOption(new Option(name, price));
	}

	// *** RETRIEVE ***

	protected double getOptionPrice(String optionName) {
		Option result = getOption(optionName);

		if (result == null) {
			return 0;
		}

		return result.getPrice();
	}

	protected double getOptionPrice(int position) {
		Option result = getOption(position);

		if (result == null) {
			return 0;
		}

		return result.getPrice();
	}

	protected int getNumberOfOptions() {
		return options.size();
	}

	// Returns the option, or null if not found
	private Option getOption(String name) {
		int position = findOption(name);

		if (position == -1) {
			return null;
		}

		return options.get(position);
	}

	// May return null if option does not exist at the specified index
	private Option getOption(int position) {
		if (position > options.size() - 1 || position < 0) {
			return null;
		}

		return options.get(position);
	}

	// *** UPDATE ***
	protected boolean updateOption(String name, double newPrice) {
		int position = findOption(new Option(name));

		if (position == -1) {
			return false;
		}

		options.get(position).setPrice(newPrice);

		return true;
	}

	protected boolean updateOption(String name, String newName) {
		int position = findOption(new Option(name));

		if (position == -1) {
			return false;
		}

		options.get(position).setName(newName);

		return true;
	}

	protected boolean updateOption(String name, String newName, double newPrice) {
		boolean successful = updateOption(name, newName);

		if (!successful) {
			return false;
		}

		return updateOption(newName, newPrice);
	}

	protected boolean updateOption(int index, String newName, double newPrice) {
		if (updateOption(index, newName)) {
			return updateOption(index, newPrice);
		}

		return false;
	}

	protected boolean updateOption(int index, String newName) {
		if (index >= 0 && index < options.size() && options.get(index) != null) {
			options.get(index).setName(newName);
			return true;
		}

		return false;
	}

	protected boolean updateOption(int index, double newPrice) {
		if (index >= 0 && index < options.size() && options.get(index) != null) {
			options.get(index).setPrice(newPrice);
			return true;
		}

		return false;
	}

	//*** DELETE ***
	private boolean deleteOption(Option option) {
		int position = findOption(option);

		if (position == -1) {
			return false;
		}

		options.remove(position);

		return true;
	}

	protected boolean deleteOption(String name) {
		return deleteOption(new Option(name));
	}

	protected boolean deleteOption(int index) {
		if (index >= 0 && index < options.size() && options.get(index) != null) {
			options.remove(index);
			return true;
		}

		return false;
	}

	protected void deleteAllOptions() {
		options.clear();
	}

	// Helper Methods

	// Returns index of Option if found, -1 if not found
	private int findOption(Option option) {
		for (int i = 0; i < options.size(); ++i) {
			if (options.get(i).getName().equalsIgnoreCase(option.getName())) {
				return i;
			}
		}

		return -1;
	}

	private int findOption(String name) {
		return findOption(new Option(name, 0));
	}

	protected boolean isEmpty() {
		return options.isEmpty();
	}
	
	protected boolean containsOption(String optionName) {
		for (Option option : options) {
			if (option.getName().toLowerCase().equals(optionName.toLowerCase())) {
				return true;
			}
		}
		
		return false;
	}
	
	protected int indexOfOption(String optionName) throws AutoException {
		if (optionName == null || optionName == "") {
			throw new AutoException(3);
		}
		
		for (int i = 0; i < options.size(); ++i) {
			if (options.get(i).getName().equalsIgnoreCase(optionName)) {
				return i;
			}
		}

		return -1;
	}
	
	protected void deselectOption() {
		selectedOption = null;
	}

	// Overridden Methods

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		String NEW_LINE = System.getProperty("line.separator");

		result.append(name).append(":").append(NEW_LINE);

		if (!isEmpty()) {
			for (Option existingOption : options) {
					if (selectedOption != null && existingOption.getName().equalsIgnoreCase(selectedOption.getName())) {
						result.append("\t--> [").append(existingOption).append("]").append(NEW_LINE);
					} else {
						result.append("\t    [").append(existingOption).append("]").append(NEW_LINE);
					}
				}
			}
		
		return result.toString();
	}
}