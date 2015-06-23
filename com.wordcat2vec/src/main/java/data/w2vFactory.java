package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class w2vFactory {

	public static VectorDictionary getWordVector(String path) throws IOException {

		HashMap<String, double[]> wordMap;
		String line = null;
		BufferedReader br = new BufferedReader(new FileReader(new File(path)));

		String fline = br.readLine();
		int lines = Integer.parseInt(fline.split(" ")[0]);
		int dim = Integer.parseInt(fline.split(" ")[1]);
		wordMap = new HashMap<String, double[]>(lines);

		while ((line = br.readLine()) != null) {
			double[] numbs = new double[dim];
			String[] t = line.split(" ");
			for (int i = 1; i < dim; i++) {
				numbs[i] = Double.parseDouble(t[i]);
			}
			wordMap.put(t[0], numbs);
		}
		VectorDictionary v = new VectorDictionary(wordMap, dim);
		return v;
	}
	public static void main(String a[]) throws IOException{
		w2vFactory.getWordVector("/home/zhangwei/Documents/w2v/code/w2v/vectors.bin");
	}
}
