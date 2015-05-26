package wikiCategoryInfo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

public class WikiCatScanner {
	static final String wiki_text_pattern = "\"text\": \"(.*?)\",$";
	static final String wiki_docno_pattern = "\"DOCNO\": \"(.*?)\",";
	static final String wiki_category_pattern = "\\[\\[Category:(.*?)\\]\\]";
//	Map<String, List<List<Integer>>> doc2paragraphs = new HashMap<String, List<List<Integer>>>();
	Map<String, Integer> dict = new HashMap<String, Integer>();
	Map<String, List<String>> category2docno = new HashMap<String, List<String>>();
	BufferedWriter docWriter = null;
	String rsFolder = null;
	
	public void scan(String file) throws IOException {
		Pattern pDocNo = Pattern.compile(wiki_docno_pattern);
		Pattern pText = Pattern.compile(wiki_text_pattern, Pattern.DOTALL);
		Pattern pCategory = Pattern.compile(wiki_category_pattern);
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = br.readLine();
		int lineNum = 1, numMatched = 0, lastMatchedLineNum = 0;
		long t1 = System.currentTimeMillis();
		boolean expectDocNo = true;
		String currentDocNo = null;
		while (line != null) {
			if (expectDocNo) {
				Matcher m = pDocNo.matcher(line);
				if (m.find()) {
					currentDocNo = m.group(1);
					expectDocNo = false;
				}
			} else {
				if (line.matches("\\s+\"text\": \"(.*?)")) {
					String text = line.substring(line.indexOf(": \"") + 3, line.length() - 2);//m.group(1);
					Matcher mCat = pCategory.matcher(text);
					int beginCatPos = -1;
					while (mCat.find()) {
						addDocInCategory(currentDocNo, mCat.group(1));
						if (beginCatPos == -1)
							beginCatPos = mCat.start();
					}
					if (beginCatPos != -1) {
						text = text.substring(0, beginCatPos);
						String[] paragraphs = text.split("\\\\n");
						for (String para : paragraphs) {
							if (para.isEmpty())
								continue;
							List<String> tokens = tokenize(para);
							addParaInDoc(tokens, currentDocNo);
						}
					}
					expectDocNo = true;
				}
			}
			++lineNum;
			line = br.readLine();
		}
		long t2 = System.currentTimeMillis();
		System.out.println(t2 - t1);
		br.close();
	}
	
	private void addParaInDoc(List<String> para, String docno) {
		try {
			if (docWriter == null)
				docWriter = new BufferedWriter(new FileWriter(rsFolder + "/documents", true));
			docWriter.write(docno + "\t");
			docWriter.write(getOffsets(para).toString());
			docWriter.write(System.lineSeparator());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		List<List<Integer>> paragraphs = doc2paragraphs.get(docno);
//		if (paragraphs == null) {
//			paragraphs = new ArrayList<List<Integer>>();
//			doc2paragraphs.put(docno, paragraphs);
//		}
//		paragraphs.add(getOffsets(para));
	}
	
	private List<Integer> getOffsets(List<String> tokens) {
		List<Integer> offsets = new ArrayList<Integer>();
		for (String t : tokens) {
			offsets.add(getTokenIndex(t));
		}
		return offsets;
	}
	
	private int getTokenIndex(String token) {
		Integer i = dict.get(token);
		if (i == null) {
			i = dict.size();
			dict.put(token, i);
		}
		return i;
	}
	
	protected List<String> tokenize(String s) throws IOException {
		List<String> response = new ArrayList<String>();
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_48, CharArraySet.EMPTY_SET);
		TokenStream stream  = analyzer.tokenStream(null, new StringReader(s));
		CharTermAttribute cta = stream.addAttribute(CharTermAttribute.class);
		stream.reset();
		while (stream.incrementToken()) {
			String analyzedText = cta.toString();
			response.add(analyzedText);
		}
		stream.close();	
		analyzer.close();
		return response;
	}
	
	private void addDocInCategory(String docno, String category) {
		List<String> docs = category2docno.get(category);
		if (docs == null) {
			docs = new ArrayList<String>();
			category2docno.put(category, docs);
		}
		docs.add(docno);
	}
	
	public void scanFolder(String folder, String rsFolder) {
		this.rsFolder = rsFolder;
		Queue<String> folders = new LinkedList<String>();
		folders.add(folder);
		int fCount = 0;
		while (!folders.isEmpty()) {
			String currentFolder = folders.poll();
	        File[] files = new File(currentFolder).listFiles(new FilenameFilter(){
	            public boolean accept(File directory, String fileName) {
	                if (fileName.matches("xml-splitTrecTrim-CDM_chunks_[0-9]*") || fileName.endsWith(".json")) {
	                    return true;
	                }
	                return false;
	            }
	        } );
	        for (File f : files) {
	        	if (f.isDirectory()) {
	        		folders.add(f.getAbsolutePath());
	        	} else {
	        		try {
	        			if (++fCount % 10 == 0) {
	    	        		System.out.println(f);
	        				scan(f.getAbsolutePath());
	        			}
					} catch (IOException e) {
						e.printStackTrace();
					}
	        	}
	        }
		}
		try {
			docWriter.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			dumpResults(rsFolder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void dumpResults(String rsFolder) throws IOException {
		BufferedWriter bw = null;
		bw = new BufferedWriter(new FileWriter(rsFolder + "/dict"));
		for (String w : dict.keySet()) {
			bw.write(w + "\t" + dict.get(w) + System.lineSeparator());
		}
		bw.close();
		
		bw = new BufferedWriter(new FileWriter(rsFolder + "/categories"));
		for (String docno : category2docno.keySet()) {
			bw.write(docno + "\t");
			for (String d : category2docno.get(docno))
				bw.write(d + ",");
			bw.write(System.lineSeparator());
		}
		bw.close();
		
	}
	
	public static void main(String[] args) throws IOException {
		WikiCatScanner tool = new WikiCatScanner();
//		String file = "/ingestion/r2.17.0/20140918-2100/intermediate/wp-20140707/xml-splitTrecTrim-CDM_chunks_107/enwiki.311.xml_SOURCE.json";//34/enwiki.221.xml_SOURCE.json";
		//"/home/yangyu/tmp/wikiPage";//
		tool.scanFolder(args[0], args[1]);//"/ingestion/r2.17.0/20140918-2100/intermediate/wp-20140707/"
//		tool.scan(file);
	}
}
