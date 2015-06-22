package data;

import java.util.List;

import nlp.StringUtils;

public class TextFilter {

	public static void filterListOfText(List<String> list){
		for (int i = 0 ; i < list.size(); i++){
			String n = StringUtils.normalize(list.get(i));
			list.set(i, n);
		}
	}

}
