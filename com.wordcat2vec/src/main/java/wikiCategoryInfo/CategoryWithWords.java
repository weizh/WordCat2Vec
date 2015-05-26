package wikiCategoryInfo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CategoryWithWords {
	private Map<String, Integer> catIndex = new HashMap<String, Integer>();
	private List<String> categories = new ArrayList<String>();

	public void process(String dictFile, String docFile, String catFile, String cat2wordCountsOutputFile, String catIndexOutputFile, String word2catOutputFile) throws IOException {
//		Map<String, Integer> dict = readDict(dictFile);
		Map<Integer, Map<Integer, Integer>> doc2wordCounts = document2wordCounts(docFile);
		System.out.println("Document to word counts finished");
		Map<Integer, Map<Integer, Integer>> cat2wordCounts = category2words(catFile, doc2wordCounts);
		System.out.println("Category to word counts finished");
		BufferedWriter bw = new BufferedWriter(new FileWriter(cat2wordCountsOutputFile));
		for (Integer catId : cat2wordCounts.keySet()) {
			bw.write(catId + "\t");
			Map<Integer, Integer> wordCounts = cat2wordCounts.get(catId);
			for (Integer w : wordCounts.keySet()) {
				bw.write(w + "," + wordCounts.get(w) + ";");
			}
			bw.write(System.lineSeparator());
		}
		bw.close();
		bw = new BufferedWriter(new FileWriter(catIndexOutputFile));
		for (String cat : categories)
			bw.write(cat + System.lineSeparator());
		bw.close();
		Map<Integer, Set<Integer>> word2cats = word2categories(cat2wordCounts);
		bw = new BufferedWriter(new FileWriter(word2catOutputFile));
		for (Integer w : word2cats.keySet()) {
			bw.write(w + "\t");
			Set<Integer> cats = word2cats.get(w);
			for (Integer c : cats) {
				bw.write(c + ",");
			}
			bw.write(System.lineSeparator());
		}
		bw.write(System.lineSeparator());
		bw.close();
	}

	protected Map<Integer, Set<Integer>> word2categories(Map<Integer, Map<Integer, Integer>> cat2wordCounts) {
		Map<Integer, Set<Integer>> word2cats = new HashMap<Integer, Set<Integer>>();
		for (Integer catId : cat2wordCounts.keySet()) {
			Map<Integer, Integer> wordCountsInCat = cat2wordCounts.get(catId);
			for (Integer w : wordCountsInCat.keySet()) {
				Set<Integer> cats = word2cats.get(w);
				if (cats == null) {
					cats = new HashSet<Integer>();
					word2cats.put(w, cats);
				}
				cats.add(catId);
			}
		}
		return word2cats;
	}

	protected Map<Integer, Map<Integer, Integer>> document2wordCounts(String docFile) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(docFile));
		String line = br.readLine();
		Map<Integer, Map<Integer, Integer>> doc2wordCounts = new HashMap<Integer, Map<Integer, Integer>>();
		int lineCount = 0;
		while (line != null) {
			String[] fields = line.split("\t");
			int docno = Integer.parseInt(fields[0]);
			Map<Integer, Integer> wordCountInDoc = doc2wordCounts.get(docno);
			if (wordCountInDoc == null) {
				wordCountInDoc = new HashMap<Integer, Integer>();
				doc2wordCounts.put(docno, wordCountInDoc);
			}
			String[] words = fields[1].substring(1, fields[1].length() - 1).split(",");
			for (String w : words) {
				if (w.isEmpty())
					continue;
				int wid = Integer.parseInt(w.trim());
				Integer count = wordCountInDoc.get(wid);
				if (count == null)
					wordCountInDoc.put(wid, 1);
				else
					wordCountInDoc.put(wid, count + 1);
			}
			line = br.readLine();
			if (++lineCount % 1000000 == 0)
				System.out.println(lineCount + " documents processed.");
		}
		br.close();
		return doc2wordCounts;
	}
	
	protected Map<Integer, List<Integer>> document2categories(String catFile) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(catFile));
		String line = br.readLine();
		Map<Integer, List<Integer>> doc2cats = new HashMap<Integer, List<Integer>>();
		while (line != null) {
			String[] fields = line.split("\t");
			String[] docs = fields[1].split(",");
			for (String d : docs) {
				Integer dInt = Integer.parseInt(d);
				List<Integer> cats = doc2cats.get(dInt);
				if (cats == null) {
					cats = new ArrayList<Integer>();
					doc2cats.put(dInt, cats);
				}
				cats.add(getCategoryId(fields[0]));
			}
			line = br.readLine();
		}
		br.close();
		return doc2cats;
	}

	protected Map<Integer, Map<Integer, Integer>> category2words(String catFile, Map<Integer, Map<Integer, Integer>> doc2wordCounts) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(catFile));
		String line = br.readLine();
		Map<Integer, Map<Integer, Integer>> cat2wordCounts = new HashMap<Integer, Map<Integer, Integer>>();
		int lineCount = 0;
		while (line != null) {
			String[] fields = line.split("\t");
			int catId = getCategoryId(fields[0]);
			Map<Integer, Integer> wordCountsInCat = new HashMap<Integer, Integer>();
			String[] docs = fields[1].split(",");
			for (String d : docs) {
				Map<Integer, Integer> wordCountsInDoc = doc2wordCounts.get(Integer.parseInt(d));
				mergeMap(wordCountsInCat, wordCountsInDoc);
			}
			cat2wordCounts.put(catId, wordCountsInCat);
			line = br.readLine();
			if (++lineCount % 10000 == 0)
				System.out.println(lineCount + " categories processed.");
		}
		br.close();
		return cat2wordCounts;
	}

	protected void mergeMap(Map<Integer, Integer> m1, Map<Integer, Integer> m2) {
		if (m2 == null || m2.isEmpty())
			return;
//		Map<Integer, Integer> rs = new HashMap<Integer, Integer>(m1);
		for (Integer i : m2.keySet()) {
			if (m1.get(i) == null)
				m1.put(i, m2.get(i));
			else
				m1.put(i, m1.get(i) + m2.get(i));
		}
	}
	
	protected int getCategoryId(String cat) {
		if (catIndex.get(cat) == null) {
			catIndex.put(cat, catIndex.size());
			categories.add(cat);
		}
		return catIndex.get(cat);
	}
	
	protected Map<String, Integer> readDict(String dictFile) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(dictFile));
		String line = br.readLine();
		Map<String, Integer> dict = new HashMap<String, Integer>();
		while (line != null) {
			String[] fields = line.split("\t");
			dict.put(fields[0], Integer.parseInt(fields[1]));
			line = br.readLine();
		}
		br.close();
		return dict;
	}
	
	public static void main(String[] args) throws IOException {
		CategoryWithWords tool = new CategoryWithWords();
		tool.process(args[0], args[1], args[2], args[3], args[4],args[5]);
	}
}
