package data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class SENT2LIBLINEAR {

	static HashSet<String> labels = new HashSet<String>();
	static HashMap<String, Integer> lids = new HashMap<String,Integer>();
	static VectorDictionary w2v;
	static {

		String[] l = new String[] { "alt.atheism", "comp.graphics", "comp.os.ms-windows.misc", "comp.sys.mac.hardware",
				"comp.windows.x", "misc.forsale", "rec.autos", "rec.motorcycles", "rec.sport.baseball", "rec.sport.hockey",
				"sci.crypt", "sci.electronics", "sci.med", "sci.space", "soc.religion.christian", "talk.politics.guns",
				"talk.politics.mideast", "talk.politics.misc", "talk.religion.misc" };
		for (String lb : l) {
			labels.add(lb);
			lids.put(lb, lids.size());
		}
	}
	static List<String> categories = new ArrayList<String>();

	public static void main(String argb[]) throws IOException{
		
		if (argb.length<3) 
			System.err.println("THis is for 10 fold cross validation. format: trainOut testOut vectorPath CVFolds");

		w2v = w2vFactory.getWordVector(argb[2]);

		for (int i = 0 ; i < Integer.parseInt(argb[3]);i++){
			generate(argb,i);
		}
	}
	
	public static void generate(String argb[], int iteration) throws IOException {

		List<NGDoc> Train = DataFactory.getNGDocs("train",true);
		 List<NGDoc> Test = DataFactory.getNGDocs("test",true);

//		 store(Train, "newsgroup-train");
//		 store(Test, "newsgroup-test");

//		List<NGDoc> Train = load(argb[0]);
//		List<NGDoc> Test = load(argb[1]);

		BufferedWriter trainbw = new BufferedWriter(new FileWriter(new File(argb[0]+"_"+iteration)));
		BufferedWriter testbw = new BufferedWriter(new FileWriter(new File(argb[1]+"_"+iteration)));

		for (int i = 0; i < Train.size(); i++)
		{
			writeInstanceLine(Train.get(i), trainbw);
		}
		for (int i = 0; i < Test.size(); i++) {
			writeInstanceLine(Test.get(i), testbw);
		}
		trainbw.close();
		testbw.close();
	}

	private static void writeInstanceLine(NGDoc ngDoc, BufferedWriter bw) throws IOException {
		String label = ngDoc.label;
		String[] ls = label.split(",");
		for (String l : ls) {
			if (labels.contains(l) == false)
				continue;
			List<String> texts = ngDoc.text;
			double wcount = 0.0;
			double[] sum = w2v.getNewNullArray();
			for (String text : texts) {
				if (!w2v.contains(text))
					continue;
				wcount++;
				double[] v = w2v.getVector(text);
				for (int i = 0; i < v.length; i++) {
					sum[i] += v[i];
				}
			}
			if (wcount != 0.0)
				for (int i = 0; i < sum.length; i++) {
					sum[i] = sum[i] / wcount;
				}
			
			bw.write(lids.get(l)+"");
			for (int i = 0; i < sum.length;i ++)
				bw.write(" "+(i+1)+":"+ sum[i]);
			bw.newLine();
		}

	}

	private static List<NGDoc> load(String f) {
		try {
			FileInputStream fis = new FileInputStream(f);
			ObjectInputStream ois = new ObjectInputStream(fis);
			return (List<NGDoc>) ois.readObject();

		} catch (IOException e) {
		} catch (ClassNotFoundException e) {
		}
		return null;
	}

	private static void store(List<NGDoc> test, String string) {
		try {
			FileOutputStream fos = new FileOutputStream(string);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(test);
			oos.close();
			fos.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

	}
}
