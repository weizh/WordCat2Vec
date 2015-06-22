package data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NGDoc implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String id;
	String label;
	List<String> text=new ArrayList<String>();
}
