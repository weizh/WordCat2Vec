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

	private static List<NGDoc> NGTrain, NGTest;
	private static Random r = new Random();

	public static List<NGDoc> getNGDocs(String type) {
		if (NGTrain == null || NGTest == null)
			readAllDocs(PATH.newsgroups);
		if (type.equalsIgnoreCase("train")) {
			return NGTrain;
		} else if (type.equalsIgnoreCase("test")) {
			return NGTest;
		}
		throw new UnsupportedOperationException("Type should be train or test for getting NGDocs.");
	}

	private static void readAllDocs(String newsgroups) {
		NGTest = new ArrayList<NGDoc>();
		NGTrain = new ArrayList<NGDoc>();
		List<File> name = new ArrayList<File>();
		listFilesForFolder(new File(newsgroups), name);
		for (int i = 0; i < name.size(); i++) {
			System.out.println(name.get(i).getAbsolutePath());
			double n = r.nextDouble();
			if (n < 0.2)
				NGTest.add(readNGDoc(name.get(i)));
			else
				NGTrain.add(readNGDoc(name.get(i)));
		}
	}

	private static NGDoc readNGDoc(File file) {
		NGDoc d = new NGDoc();
		boolean isContent = false;
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("Newsgroups:") )
				{
					d.label = line;
					continue;
				}
				else if (line.startsWith("Path:") )
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
