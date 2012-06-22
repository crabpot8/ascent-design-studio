package org.ascent.binpacking;

public class Element {

	private int id_;
	protected String name_;

	public Element(int id, String name) {
		super();
		id_ = id;
		name_ = name;
	}

	public int getId() {
		return id_;
	}

	public void setId(int id) {
		id_ = id;
	}

	public String getName() {
		return name_;
	}

	public void setName(String name) {
		name_ = name;
	}

}
