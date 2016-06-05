package model;

import java.io.Serializable;
import java.util.ArrayList;

import exception.AutoException;

public class Automobile implements Serializable {
	// Instance Variables

	private String name;
	private double basePrice;
	private ArrayList<OptionSet> optionSets;

	// Static Variables

	private static final long serialVersionUID = 9220006156919897946L;

	// Constructors

	public Automobile() throws AutoException {
		this("NONE", 0);
	}

	public Automobile(String name, double basePrice) throws AutoException {
		if (name == null || name == "") {
			throw new AutoException(5);
		}

		this.name = name;
		this.basePrice = basePrice;
		optionSets = new ArrayList<>();
	}

	public Automobile(String name, double basePrice, String... optionSetsNames) throws AutoException {
		this(name, basePrice);

		for (int i = 0; i < optionSetsNames.length; ++i) {
			optionSets.set(i, new OptionSet(optionSetsNames[i]));
		}
	}

	// Getters and Setters

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(double basePrice) {
		this.basePrice = basePrice;
	}

	// Instance Methods

	// *** CREATE ***

	public boolean addOptionToSet(String setName, String optionName, double price) throws AutoException {
		if (optionSetExists(setName)) {
			return optionSets.get(indexOfOptionSet(setName)).createOption(optionName, price);
		}

		return false;
	}

	public boolean addOptionToSet(int setIndex, String optionName, double price) {
		if (optionSets.get(setIndex) != null) {
			return optionSets.get(setIndex).createOption(optionName, price);
		}

		return false;
	}

	public boolean createSet(String setName) throws AutoException {
		if (setName == null || setName == "") {
			throw new AutoException(3);
		}

		return optionSets.add(new OptionSet(setName));
	}

	// *** RETRIEVE ***

	public String [] getOptionSetsNames() {
		String [] result = new String[optionSets.size()];
		int i = 0;

		for (OptionSet set : optionSets) {
			result[i] = set.getName();
			++i;
		}

		return result;
	}

	public String [] getOptionsWithPrices() {
		String [] result = new String[optionSets.size()];
		int i = 0;

		for (OptionSet set : optionSets) {
			result[i] = set.toString();
			++i;
		}

		return result;
	}
	
	public String[] getOptionsInSet(String setName) {
		try {
			return getOptionsInSet(indexOfOptionSet(setName));
		
		} catch (AutoException e) {
			e.printIssue();
			return null;
		}
	}
	
	public String[] getOptionsInSet(int setIndex) {
		OptionSet result = optionSets.get(setIndex);
		
		if (result != null) {
			String[] resultArray = new String[result.getNumberOfOptions()];
			int index = 0;
			
			for (Option option: result.getOptions()) {
				resultArray[index] = option.toString();
				++index;
			}
			
			return resultArray;
		}

		return null;
	}

	public double getOptionPriceInSet(String setName, String optionName) throws AutoException {
		if (setName == null || setName == "") {
			throw new AutoException(3);
		}

		if (optionName == null || optionName == "") {
			throw new AutoException(4);
		}

		if (optionSetExists(setName)) {
			return optionSets.get(indexOfOptionSet(setName)).getOptionPrice(optionName);
		}

		return 0;
	}

	public double getOptionPriceInSet(int setIndex, String optionName) throws AutoException {
		if (optionName == null || optionName == "") {
			throw new AutoException(4);
		}

		OptionSet result = optionSets.get(setIndex);

		if (result != null) {
			return result.getOptionPrice(optionName);
		}

		return 0;
	}

	public boolean hasOptionSets() {
		for (OptionSet set : optionSets) {
			if (set != null) {
				return true;
			}
		}

		return false;
	}

	public int getNumbersOfOptionSets() {
		return optionSets.size();
	}

	public int getNumberOfOptionsInSet(String setName) throws AutoException {
		if (setName == null || setName == "") {
			throw new AutoException(3);
		}

		if (optionSetExists(setName)) {
			return optionSets.get(indexOfOptionSet(setName)).getNumberOfOptions();
		}

		return -1;
	}

	public int getNumberOfOptionsInSet(int setIndex) {
		OptionSet result = optionSets.get(setIndex);

		if (result != null) {
			return result.getNumberOfOptions();
		}

		return -1;
	}

	// Returns -1 if not found
	public int getIndexOfOptionSetWithOption(String optionName) throws AutoException {
		if (optionName == null || optionName == "") {
			throw new AutoException(4);
		}

		for (int i = 0; i < optionSets.size(); ++i) {
			if (optionSets.get(i).containsOption(optionName)) {
				return i;
			}
		}

		return -1;
	}

	public int getIndexOfOptionSetNamed(String setName) throws AutoException {
		if (setName == null || setName == "") {
			throw new AutoException(3);
		}

		for (int i = 0; i < optionSets.size(); ++i) {
			if (optionSets.get(i).getName().equalsIgnoreCase(setName)) {
				return i;
			}
		}

		return -1;
	}

	public double getTotalPrice() {
		double total = basePrice;

		for (OptionSet set : optionSets) {
			total += set.getSelectedOptionPrice();
		}

		return total;
	}

	public String [] getSelectedOptionsWithPrices() {
		ArrayList<String> result = new ArrayList<>();

		for (OptionSet set : optionSets) {
			Option selected = set.getSelectedOption();

			if (selected != null) {
				result.add(selected.toString());
			}
		}
		
		String[] resultArray = new String[result.size()];
		
		int i = 0;
		
		for (String res: result) {
			resultArray[i] = res;
			++i;
		}

		return resultArray;
	}

	// *** UPDATE ***

	public boolean updateOptionSetName(String setName, String newName) throws AutoException {
		if (setName == null || setName == "" || newName == null || newName == "") {
			throw new AutoException(3);
		}

		if (optionSetExists(setName)) {
			optionSets.get(indexOfOptionSet(setName)).setName(newName);
			return true;
		}

		return false;
	}

	public boolean updateOptionInSet(String setName, String optionName, String newName, double newPrice) throws AutoException {
		if (setName == null || setName == "") {
			throw new AutoException(3);
		}

		if (optionName == null || optionName == "" || newName == null || newName == "") {
			throw new AutoException(4);
		}

		if (optionSetExists(setName)) {
			return optionSets.get(indexOfOptionSet(setName)).updateOption(optionName, newName, newPrice);
		}

		return false;
	}

	public boolean updateOptionInSet(int setIndex, String optionName, String newName, double newPrice) throws AutoException {
		if (optionName == null || optionName == "" || newName == null || newName == "") {
			throw new AutoException(4);
		}

		OptionSet result = optionSets.get(setIndex);

		if (result != null) {
			return result.updateOption(optionName, newName, newPrice);
		}

		return false;
	}

	public boolean updateOptionNameInSet(String setName, String optionName, double newPrice) throws AutoException {
		if (setName == null || setName == "") {
			throw new AutoException(3);
		}

		if (optionName == null || optionName == "") {
			throw new AutoException(4);
		}

		if (optionSetExists(setName)) {
			return optionSets.get(indexOfOptionSet(setName)).updateOption(optionName, newPrice);
		}

		return false;
	}

	public boolean updateOptionPriceInSet(int setIndex, String optionName, double newPrice) throws AutoException {
		if (optionName == null || optionName == "") {
			throw new AutoException(4);
		}

		OptionSet result = optionSets.get(setIndex);

		if (result != null) {
			return result.updateOption(optionName, newPrice);
		}

		return false;
	}

	public boolean updateOptionNameInSet(String setName, String optionName, String newName) throws AutoException {
		if (setName == null || setName == "") {
			throw new AutoException(3);
		}

		if (optionName == null || optionName == "" || newName == null || newName == "") {
			throw new AutoException(4);
		}

		if (optionSetExists(setName)) {
			return optionSets.get(indexOfOptionSet(setName)).updateOption(setName, newName);
		}

		return false;
	}

	public boolean updateOptionPriceInSet(int setIndex, String optionName, String newName) throws AutoException {
		if (optionName == null || optionName == "" || newName == null || newName == "") {
			throw new AutoException(4);
		}

		OptionSet result = optionSets.get(setIndex);

		if (result != null) {
			return result.updateOption(optionName, newName);
		}

		return false;
	}

	public boolean updateOptionInSet(String setName, int optionIndex, String newName, double newPrice) throws AutoException {
		if (setName == null || setName == "") {
			throw new AutoException(3);
		}

		if (newName == null || newName == "") {
			throw new AutoException(4);
		}

		if (optionSetExists(setName)) {
			return optionSets.get(indexOfOptionSet(setName)).updateOption(optionIndex, newName, newPrice);
		}

		return false;
	}

	public boolean updateOptionInSet(int setIndex, int optionIndex, String newName, double newPrice) throws AutoException {
		if (newName == null || newName == "") {
			throw new AutoException(4);
		}

		OptionSet result = optionSets.get(setIndex);

		if (result != null) {
			return result.updateOption(optionIndex, newName, newPrice);
		}

		return false;
	}

	public boolean updateOptionNameInSet(String setName, int optionIndex, double newPrice) throws AutoException {
		if (setName == null || setName == "") {
			throw new AutoException(3);
		}

		if (optionSetExists(setName)) {
			return optionSets.get(indexOfOptionSet(setName)).updateOption(optionIndex, newPrice);
		}

		return false;
	}

	public boolean updateOptionPriceInSet(int setIndex, int optionIndex, double newPrice) {
		OptionSet result = optionSets.get(setIndex);

		if (result != null) {
			return result.updateOption(optionIndex, newPrice);
		}

		return false;
	}

	public boolean updateOptionPriceInSet(int setIndex, int optionIndex, String newName) throws AutoException {
		if (newName == null || newName == "") {
			throw new AutoException(4);
		}

		OptionSet result = optionSets.get(setIndex);

		if (result != null) {
			return result.updateOption(optionIndex, newName);
		}

		return false;
	}

	public boolean selectOption(String setName, String optionName) throws AutoException {
		if (setName == null || setName == "") {
			throw new AutoException(3);
		}

		if (optionSetExists(setName)) {
			OptionSet existingOptionSet = optionSets.get(indexOfOptionSet(setName));
			
			if (existingOptionSet.containsOption(optionName)) {
				return existingOptionSet.setSelectedOption(optionName);
			}
		}

		return false;
	}

	// *** DELETE ***

	public void deleteAllOptionSets() {
		optionSets.clear();
	}

	public boolean deleteOptionSet(String setName) throws AutoException {
		if (setName == null || setName == "") {
			throw new AutoException(3);
		}

		if (optionSetExists(setName)) {
			optionSets.remove(indexOfOptionSet(setName));
			return true;
		}

		return false;
	}

	public boolean deleteOptionInSet(String setName, String optionName) throws AutoException {
		if (setName == null || setName == "") {
			throw new AutoException(3);
		}

		if (optionName == null || optionName == "") {
			throw new AutoException(4);
		}

		if (optionSetExists(setName)) {
			return optionSets.get(indexOfOptionSet(setName)).deleteOption(optionName);
		}

		return false;
	}

	public boolean deleteOptionInSet(int setIndex, String optionName) throws AutoException {
		if (optionName == null || optionName == "") {
			throw new AutoException(4);
		}

		OptionSet result = optionSets.get(setIndex);

		if (result != null) {
			return result.deleteOption(optionName);
		}

		return false;
	}

	public boolean deleteAllOptionsInSet(String setName) throws AutoException {
		if (setName == null || setName == "") {
			throw new AutoException(3);
		}

		if (optionSetExists(setName)) {
			optionSets.get(indexOfOptionSet(setName)).deleteAllOptions();
			return true;
		}

		return false;
	}

	public boolean deleteAllOptionsInSet(int setIndex) {
		OptionSet result = optionSets.get(setIndex);

		if (result != null) {
			result.deleteAllOptions();
			return true;
		}

		return false;
	}

	// Helper Methods

	public boolean optionSetExists(String setName) throws AutoException {
		if (setName == null || setName == "") {
			throw new AutoException(3);
		}

		if (indexOfOptionSet(setName) == -1) {
			return false;
		}

		return true;
	}

	public int indexOfOptionSet(String setName) throws AutoException {
		if (setName == null || setName == "") {
			throw new AutoException(3);
		}

		for (int i = 0; i < optionSets.size(); ++i) {
			if (optionSets.get(i).getName().equalsIgnoreCase(setName)) {
				return i;
			}
		}

		return -1;
	}

	@SuppressWarnings("unused")
	private OptionSet optionSetAtIndex(int index) {
		if (optionSets.size() < index) {
			return null;
		}

		return optionSets.get(index);
	}

	// Overridden Methods

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		String NEW_LINE = System.getProperty("line.separator");

		builder.append("Automotive").append(NEW_LINE).append("Name: ");

		if (name == "") {
			builder.append("UNKNOWN");
		} else {
			builder.append(name);
		}

		builder.append(NEW_LINE).append("BasePrice: $").append(basePrice);
		
		builder.append(NEW_LINE).append("TotalPrice: $").append(getTotalPrice());
		
		builder.append(NEW_LINE).append("OptionSets:").append(NEW_LINE);

		if (hasOptionSets()) {
			for (OptionSet set : optionSets) {
				if (set != null) {
					builder.append(set);
				}
			}
		} else {
			builder.append("NONE");
		}

		return builder.toString();
	}
}
