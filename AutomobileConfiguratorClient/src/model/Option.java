package model;

import java.io.Serializable;

public class Option implements Serializable {
	// Instance Variables

	private String name;
	private double price;

	// Static Variables

	private static final long serialVersionUID = -2934125115722642647L;

	// Constructors

	protected Option() {
		name = "";
		price = 0;
	}

	protected Option(String name, double price) {
		this.name = name;
		this.price = price;
	}

	protected Option(String name) {
		this.name = name;
	}

	protected Option(double price) {
		this.price = price;
	}

	// Getters and Setters

	protected String getName() {
		return name;
	}

	protected void setName(String name) {
		this.name = name;
	}

	protected double getPrice() {
		return price;
	}

	protected void setPrice(double price) {
		this.price = price;
	}

	// Instance Methods

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(name);
		result.append(", $");
		result.append(price);
		return result.toString();
	}
}