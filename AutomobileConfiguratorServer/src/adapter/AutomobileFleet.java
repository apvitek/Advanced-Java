package adapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import model.Automobile;

public class AutomobileFleet {
	private LinkedHashMap<String, Automobile> autos;

	public AutomobileFleet() {
		autos = new LinkedHashMap<>();
	}

	protected Automobile getAutomobile(String modelName) {
		if (autos.containsKey(modelName)) {
			return autos.get(modelName);
		}

		return null;
	}

	protected Automobile getAutomobile(Automobile auto) {
		return getAutomobile(auto.getName());
	}

	protected void addAutomobile(Automobile auto) {
		if (auto != null) {
			autos.put(auto.getName(), auto);
		}
	}

	protected boolean removeAutomobile(String autoName) {
		if (autoName != null) {
			autos.remove(autoName);
			return true;
		}

		return false;
	}

	protected boolean removeAutomobile(Automobile auto) {
		if (auto != null && autos.containsValue(auto)) {
			return removeAutomobile(auto.getName());
		}

		return false;
	}

	protected ArrayList<Automobile> getAllAutomobiles() {
		ArrayList<Automobile> resultArray = new ArrayList<>();

		if (autos.isEmpty() == false) {

			for (Automobile auto : autos.values()) {
				resultArray.add(auto);
			}
		}

		return resultArray;
	}
}
