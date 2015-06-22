package data;

import java.io.Serializable;
import java.util.HashMap;

public class Dictionary implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	HashMap<String, Integer> words = new HashMap<String,Integer>();
	private int index = 0; 
	
	public void addWord(String s){
		if (!words.containsKey(s))
		words.put(s, index++);
	}
	
	public Integer getWordId(String s){
		return words.get(s);
	}
	
}
