package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import nlp.EuroLangTwokenizer;

public class DataFactory {

	private static List<NGDoc> NGTotal, NGTrain, NGTest;
	private static Random r = new Random();

	public static List<NominalPair> getNominalPairs(String file) {
		List<NominalPair> pairs = new ArrayList<NominalPair>();

		String line = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File(file)));
			while ((line = br.readLine()) != null) {
				pairs.add(getNominalPair(line));
			}
			return pairs;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String ag[]) {
		getNominalPairs(ag[0]);
	}

	private static NominalPair getNominalPair(String line) {

		NominalPair np = new NominalPair();
		String[] t = line.split("\t");
		np.wa = t[1];
		np.posa = t[2];
		np.wb = t[3];
		np.posb = t[4];
		np.ctxta_pw = t[5].split("<b>")[0].trim().split(" ");
		np.ctxta_nw = t[5].split("</b>")[1].trim().split(" ");
		np.ctxtb_pw = t[6].split("<b>")[0].trim().split(" ");
		np.ctxtb_nw = t[6].split("</b>")[1].trim().split(" ");

		np.averageRating = Double.parseDouble(t[7]);
		return np;
	}

	public static List<NGDoc> getNGDocs(String type, boolean reset) {
		if (NGTotal == null)
			readAllDocs(PATH.newsgroups);

		if (reset)
			generateNewSplit();
		else if (NGTrain == null || NGTest == null)
			generateNewSplit();

		if (type.equalsIgnoreCase("train")) {
			return NGTrain;
		} else if (type.equalsIgnoreCase("test")) {
			return NGTest;
		}
		throw new UnsupportedOperationException("Type should be train or test for getting NGDocs.");
	}

	private static void generateNewSplit() {

		NGTest = new ArrayList<NGDoc>();
		NGTrain = new ArrayList<NGDoc>();
		for (int i = 0; i < NGTotal.size(); i++) {
			if (r.nextDouble() > 0.2)
				NGTrain.add(NGTotal.get(i));
			else
				NGTest.add(NGTotal.get(i));
		}
	}

	private static void readAllDocs(String newsgroups) {
		List<File> name = new ArrayList<File>();
		listFilesForFolder(new File(newsgroups), name);
		if (NGTotal == null)
		{
			NGTotal = new ArrayList<NGDoc>();
			for (int i = 0; i < name.size(); i++) {
				NGTotal.add(readNGDoc(name.get(i)));
			}
		}
	}

	private static NGDoc readNGDoc(File file) {
		NGDoc d = new NGDoc();
		boolean isContent = false;
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("Newsgroups:"))
				{
					d.label = line;
					continue;
				}
				else if (line.startsWith("Path:"))
				{
					d.id = line;
					continue;
				}
				else if (line.trim().length() == 0 && !isContent)
				{
					isContent = true;
					continue;
				}
				else if (isContent) {
					d.text.addAll(EuroLangTwokenizer.tokenize(line));
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return d;
	}

	public static void listFilesForFolder(final File folder, List<File> name) {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry, name);
			} else {
				name.add(fileEntry);
			}
		}
	}
}
