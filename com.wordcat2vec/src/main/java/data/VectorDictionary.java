package data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;

public class VectorDictionary implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public VectorDictionary(HashMap<String, double[]> wordMap, int dim) {
		this.map = wordMap;
		this.dim = dim;
		this.nullarray = new double[dim];
		Arrays.fill(nullarray, 0.0);
	}

	private int dim;
	private HashMap<String, double[]> map;
	private double[] nullarray;

	public double[] getNewNullArray() {
		double[] n = new double[dim];
		Arrays.fill(n, 0.0);
		return n;
	}

	public boolean contains(String word) {
		return map.containsKey(word);
	}

	public double[] getVector(String word) {
		if (word == null)
			return getNewNullArray();
		if (map.containsKey(word) == false)
			return getNewNullArray();
		return map.get(word);
	}
}
