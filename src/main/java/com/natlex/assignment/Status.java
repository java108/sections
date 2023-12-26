package com.natlex.assignment;

public enum Status {

	ERROR ("ERROR"),
	IN_PROGRESS ("IN PROGRESS"),
	DONE ("DONE");

	private  String value;
	Status(String string) {
		value=string;	
	}	
	
	@Override
	public String toString() {
		return value;
	}
}
