package adapter;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import model.Automobile;

public abstract class ProxyAutomobile {
	protected static AutomobileFleet autos = new AutomobileFleet();

	protected Automobile getAutomobile(String autoName) {
		return autos.getAutomobile(autoName);
	}

	protected Automobile getAutomobile(Automobile auto) {
		return autos.getAutomobile(auto);
	}

	protected void addAutomobile(Automobile auto) {
		autos.addAutomobile(auto);
	}

	protected boolean removeAutomobile(Automobile auto) {
		return removeAutomobile(auto.getName());
	}

	protected boolean removeAutomobile(String autoName) {
		return removeAutomobile(autoName);
	}

	protected ArrayList<Automobile> getAllAutomobiles() {
		return autos.getAllAutomobiles();
	}

	public void sendAutomobileToStream(ObjectOutputStream stream, String autoName) {
		Automobile auto = autos.getAutomobile(autoName);

		if (auto == null) {
			System.out.println("The model \"" + autoName + "\" does not exist.");

		} else {
			try {
				stream.writeObject(auto);
				stream.flush();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}