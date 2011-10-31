package org.destecs.vdm;

import java.util.List;
import java.util.Vector;

public class ValueContents {

	public List<Double> value = new Vector<Double>();
	public List<Integer> size = new Vector<Integer>();
	
	public ValueContents(List<Double> value, List<Integer> size) {
		this.value = value;
		this.size = size;
	}
	
}
