package data;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import nlp.StringUtils;

public class SENT2LIBLINEAR {

	public static void main(String argb[]) {
		// List<NGDoc> Train = DataFactory.getNGDocs("train");
		// List<NGDoc> Test = DataFactory.getNGDocs("test");
		//
		// store(Train, "newsgroup-train");
		// store(Test, "newsgroup-test");

//		List<NGDoc> Train = load("newsgroup-train");
		List<NGDoc> Train = load("newsgroup-test");

		for (int i = 0; i < Train.size(); i++)
		{
			System.out.println(Train.get(i).id);
			System.out.println(Train.get(i).label);
			List<String> norm = Train.get(i).text;
			System.out.println(Train.get(i).text);
			for (int ii = 0 ; ii < norm.size() ; ii ++){
				norm.set(ii, StringUtils.normalize(norm.get(ii)));
			}
			System.out.println(Train.get(i).text);
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
