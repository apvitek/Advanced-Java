package server;

import java.io.ObjectOutputStream;
import java.util.ArrayList;

import model.Automobile;

public interface AutoServer {
	public void sendAutomobileToStream(ObjectOutputStream stream, String autoName);
	public ArrayList<Automobile> getAllAutomobiles();
}